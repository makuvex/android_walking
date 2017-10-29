package com.friendly.walkingout.firabaseManager;

import android.content.Context;
import android.support.annotation.NonNull;

import com.friendly.walkingout.dataSet.UserData;
import com.friendly.walkingout.util.JWLog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import static java.lang.System.in;


/**
 * Created by jungjiwon on 2017. 10. 25..
 */

public class FireBaseNetworkManager {

    private static FireBaseNetworkManager mSelf;
    private Context                             mContext;

    private FirebaseDatabase                    firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference                   databaseReference = firebaseDatabase.getReference();

    // firebase 로그인 인증
    private FirebaseAuth                        mAuth;
    private FirebaseAuth.AuthStateListener      mAuthListener;

    public interface FireBaseNetworkCallback {
        public void onCompleted(boolean result);
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
                        callback.onCompleted(task.isSuccessful());

                        createUserData(task.getResult().getUser().getEmail(), task.getResult().getUser().getUid(), "은비", null);
                    }
                });
    }

    public void loginEmailWithPassword(String email, String password, final FireBaseNetworkCallback callback) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        JWLog.d("", "signInWithEmail:onComplete:" + task.isSuccessful());
                        callback.onCompleted(task.isSuccessful());
                    }
                });
    }

    public void createUserData(String email, String uid, String  petName, final FireBaseNetworkCallback callback) {
        UserData data = new UserData(email, uid, petName);

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                JWLog.w("", "@@@ onDataChange");
                UserData data = dataSnapshot.getValue(UserData.class);
                if(callback != null) {
                    callback.onCompleted(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                JWLog.w("", "error : "+ databaseError.toException());
                if(callback != null) {
                    callback.onCompleted(false);
                }
            }
        };

        databaseReference.addValueEventListener(listener);
        databaseReference.child("users").child(uid).setValue(data);
    }

    public void findUserEmail(final String email, FireBaseNetworkManager.FireBaseNetworkCallback callback) {
        Query myTopPostsQuery = databaseReference.child("users");

        myTopPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    UserData user = data.getValue(UserData.class);

                    JWLog.e("","@@@ email : "+user.getLoginEmail());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                JWLog.e("","e :" + databaseError.getDetails());
            }

        });
    }



}
