package com.friendly.walking.firabaseManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.friendly.walking.R;
import com.friendly.walking.dataSet.UserData;
import com.friendly.walking.preference.PreferencePhoneShared;
import com.friendly.walking.util.JWLog;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;


/**
 * Created by jungjiwon on 2017. 10. 25..
 */

public class FireBaseNetworkManager implements GoogleApiClient.OnConnectionFailedListener {

    public static final String                  DB_TABLE_USER = "user";
    public static final String                  DB_TABLE_PET = "pet";
    public static final int                     RC_GOOGLE_SIGN_IN = 9001;


    private static FireBaseNetworkManager      mSelf;
    private Context                             mContext;

    private FirebaseDatabase                    firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference                   databaseReference = firebaseDatabase.getReference();

    private FirebaseStorage                     firebaseStorage;
    private StorageReference                    storageRef;

    // firebase 로그인 인증
    private FirebaseAuth                        mAuth;
    private FirebaseAuth.AuthStateListener      mAuthListener;
    private Task<AuthResult>                    mTask;
    public GoogleApiClient                     mGoogleApiClient;

    private long                                mUserIndex = -1;


    public interface FireBaseNetworkCallback {
        public void onCompleted(boolean result, Object object);
    }

    public static FireBaseNetworkManager getInstance(AppCompatActivity activity) {
        if(mSelf == null) {
            mSelf = new FireBaseNetworkManager(activity);
        }
        mSelf.mContext = activity;

        if(!mSelf.checkInternetConnection(activity)) {
            Toast.makeText(activity, R.string.internet_connection_faild, Toast.LENGTH_SHORT).show();
        }

        if(activity instanceof AppCompatActivity && mSelf.mGoogleApiClient == null) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(activity.getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            mSelf.mGoogleApiClient = new GoogleApiClient.Builder(activity)
                    .enableAutoManage(activity /* FragmentActivity */, mSelf /* OnConnectionFailedListener */)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();

        }

        return mSelf;
    }

    public static FireBaseNetworkManager getInstance(Context context) {
        if(mSelf == null) {
            mSelf = new FireBaseNetworkManager(context);
        }
        mSelf.mContext = context;

        if(!mSelf.checkInternetConnection(context)) {
            Toast.makeText(context, R.string.internet_connection_faild, Toast.LENGTH_SHORT).show();
        }
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
                        JWLog.e("", "@@@ createUserWithEmail:onComplete:" + task.isSuccessful());
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
                        JWLog.e("", "signInWithEmail:onComplete:" + task.isSuccessful());
                        mTask = task;
                        callback.onCompleted(task.isSuccessful(), task);
                    }
                });
    }

    public void createUserData(UserData data, final FireBaseNetworkCallback callback) {
        JWLog.e("", "@@@ ");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserData data = dataSnapshot.getValue(UserData.class);
                JWLog.e("", "@@@ onDataChange data :"+data);
                if(callback != null) {
                    callback.onCompleted(true, null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                JWLog.e("", "error : "+ databaseError.toException());
                if(callback != null) {
                    callback.onCompleted(false, null);
                }
            }
        });
        databaseReference.child("users").child(data.uid).setValue(data);
    }

    public void deleteUserData(final FireBaseNetworkCallback callback) {
        JWLog.e("", "@@@ ");
        String uid = mTask.getResult().getUser().getUid();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                JWLog.e("", "@@@ onDataChange data");
                if(callback != null) {
                    callback.onCompleted(true, null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                JWLog.e("", "error : "+ databaseError.toException());
                if(callback != null) {
                    callback.onCompleted(false, null);
                }
            }
        });

        databaseReference.child("users").child(uid).removeValue();
    }

    public void readUserData(final String email, final FireBaseNetworkManager.FireBaseNetworkCallback callback) {
        final Query myTopPostsQuery = databaseReference.child("users").orderByChild("mem_email").equalTo(email);

        myTopPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserData userData = null;
                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    userData = data.getValue(UserData.class);
                }
                JWLog.e("","userData :"+userData);
                if(callback != null) {
                    if (userData != null) {
                        callback.onCompleted(true, userData);
                    } else {
                        callback.onCompleted(false, null);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                JWLog.e("","e :" + databaseError.getDetails());
                callback.onCompleted(false, null);
            }
        });
    }

    public void findUserEmail(final String email, final FireBaseNetworkManager.FireBaseNetworkCallback callback) {

        final Query myTopPostsQuery = databaseReference.child("users").orderByChild("mem_email").equalTo(email);

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

    public void uploadProfileImage(Uri file, final FireBaseNetworkManager.FireBaseNetworkCallback callback) {
        StorageMetadata  metadata = new StorageMetadata.Builder()
                .setContentType("image/jpeg")
                .build();

        UploadTask uploadTask = storageRef.child("profile/"+file.getLastPathSegment()).putFile(file, metadata);

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
                callback.onCompleted(false, null);
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful uploads on complete
                Uri downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                callback.onCompleted(true, null);
            }
        });

    }

    public void downloadProfileImage(Uri file, final FireBaseNetworkManager.FireBaseNetworkCallback callback) {
        StorageReference imageRef = storageRef.child("profile/"+file.getLastPathSegment());

        try {
            final  File imageFile = File.createTempFile("image", "jpg");
            imageRef.getFile(imageFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Success Case
                    Bitmap bitmapImage = BitmapFactory.decodeFile(imageFile.getPath());
                    if(callback != null) {
                        callback.onCompleted(true, bitmapImage);
                    }
                        //imageView.setImageBitmap(bitmapImage);
                        //Toast.makeText(mContext, "Success !!", Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Fail Case
                    e.printStackTrace();
                    Toast.makeText(mContext, "Fail !!", Toast.LENGTH_LONG).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            if(callback != null) {
                callback.onCompleted(false, null);
            }
        }

    }

    public void changePassword(String password, final FireBaseNetworkManager.FireBaseNetworkCallback callback) {
        try {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            user.updatePassword(password)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                JWLog.e("", "User password updated.");
                                if(callback != null) {
                                    callback.onCompleted(true, null);
                                }
                            } else {
                                if(callback != null) {
                                    callback.onCompleted(false, null);
                                }
                            }
                        }
                    });

        } catch(Exception e) {
            e.printStackTrace();
            if(callback != null) {
                callback.onCompleted(false, null);
            }
        }
    }

    public void logoutAccount(Context context) {
        FirebaseAuth.getInstance().signOut();

        PreferencePhoneShared.setAutoLoginYn(context, false);
        PreferencePhoneShared.setLoginYn(context, false);
        PreferencePhoneShared.setUserUID(context, "");
        PreferencePhoneShared.setLoginPassword(context, "");
    }

    public void queryUserIndex(final FireBaseNetworkManager.FireBaseNetworkCallback callback) {

        final Query myTopPostsQuery = databaseReference.child("users").orderByChild("mem_email");

        myTopPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() > 0) {
                    mUserIndex = dataSnapshot.getChildrenCount();
                    callback.onCompleted(true, null);
                } else {
                    mUserIndex = 0;
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

    public void googleSignIn(AppCompatActivity activity) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        activity.startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
    }

    public void signInWithCredential(AuthCredential credential) {
        if(mAuth != null) {
            mAuth.signInWithCredential(credential);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        JWLog.e("",connectionResult.getErrorMessage());
    }

    public boolean checkInternetConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        return isConnected;

    }

    public long getUserIndex() {
        return mUserIndex;
    }

}
