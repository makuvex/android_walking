package com.friendly.walking.firabaseManager;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.friendly.walking.dataSet.UserData;
import com.friendly.walking.util.JWLog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


/**
 * Created by jungjiwon on 2017. 10. 25..
 */

public class FireBaseNetworkManager {

    private static FireBaseNetworkManager      mSelf;
    private Context                             mContext;

    private FirebaseDatabase                    firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference                   databaseReference = firebaseDatabase.getReference();

    private FirebaseStorage                     firebaseStorage;
    private StorageReference                    storageRef;

    // firebase 로그인 인증
    private FirebaseAuth                        mAuth;
    private FirebaseAuth.AuthStateListener      mAuthListener;

    public interface FireBaseNetworkCallback {
        public void onCompleted(boolean result, Task<AuthResult> task);
    }

    public static FireBaseNetworkManager getInstance(Context context) {
        if(mSelf == null) {
            mSelf = new FireBaseNetworkManager(context);
        }
        mSelf.mContext = context;
        return mSelf;
    }

    public FireBaseNetworkManager(Context context) {
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    JWLog.d("", "@@@ onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    JWLog.d("", "@@@ onAuthStateChanged:signed_out");
                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);
        firebaseStorage = FirebaseStorage.getInstance();
        storageRef = firebaseStorage.getReference();
    }

    public void onStart() {
        if (mAuthListener != null) {
            mAuth.addAuthStateListener(mAuthListener);
        }
    }

    public void onStop() {
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void createAccount(String email, String password, final FireBaseNetworkCallback callback) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        JWLog.d("", "@@@ createUserWithEmail:onComplete:" + task.isSuccessful());
                        callback.onCompleted(task.isSuccessful(), task);

                        //createUserData(task.getResult().getUser().getEmail(), task.getResult().getUser().getUid(), "은비", null);
                    }
                });
    }

    public void loginEmailWithPassword(String email, String password, final FireBaseNetworkCallback callback) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        JWLog.d("", "signInWithEmail:onComplete:" + task.isSuccessful());
                        callback.onCompleted(task.isSuccessful(), null);
                    }
                });
    }

    public void createUserData(UserData data, final FireBaseNetworkCallback callback) {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                JWLog.w("", "@@@ onDataChange");
                UserData data = dataSnapshot.getValue(UserData.class);
                if(callback != null) {
                    callback.onCompleted(true, null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                JWLog.w("", "error : "+ databaseError.toException());
                if(callback != null) {
                    callback.onCompleted(false, null);
                }
            }
        };

        databaseReference.addValueEventListener(listener);
        databaseReference.child("users").child(data.getUID()).setValue(data);
    }

    public void createUserData(String email, String uid, String  petName, final FireBaseNetworkCallback callback) {
        UserData data = new UserData(email, uid, petName);

        createUserData(data, callback);
    }

    public void findUserEmail(final String email, final FireBaseNetworkManager.FireBaseNetworkCallback callback) {
        Query myTopPostsQuery = databaseReference.child("users").orderByChild("loginEmail").equalTo(email);

        myTopPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() > 0) {
                    callback.onCompleted(true, null);
                } else {
                    callback.onCompleted(false, null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                JWLog.e("","e :" + databaseError.getDetails());
                callback.onCompleted(false, null);
            }
        });
    }

    public void uploadImage(Uri file) {

        // File or Blob
        //file = Uri.fromFile(new File("path/to/mountains.jpg"));

// Create the file metadata
        StorageMetadata  metadata = new StorageMetadata.Builder()
                .setContentType("image/jpeg")
                .build();

// Upload file and metadata to the path 'images/mountains.jpg'
        UploadTask uploadTask = storageRef.child("images/"+file.getLastPathSegment()).putFile(file, metadata);

// Listen for state changes, errors, and completion of the upload.
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                System.out.println("Upload is " + progress + "% done");
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                System.out.println("Upload is paused");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful uploads on complete
                Uri downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
            }
        });

    }

}