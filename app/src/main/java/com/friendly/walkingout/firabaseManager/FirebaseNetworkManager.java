package com.friendly.walkingout.firabaseManager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.friendly.walkingout.util.JWLog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * Created by jungjiwon on 2017. 10. 25..
 */

public class FirebaseNetworkManager {

    private static FirebaseNetworkManager       mSelf;
    private Context                             mContext;

    private FirebaseDatabase                    firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference                   databaseReference = firebaseDatabase.getReference();

    // firebase 로그인 인증
    private FirebaseAuth                        mAuth;
    private FirebaseAuth.AuthStateListener      mAuthListener;

    public interface FirebaseNetworkCallback {
        public void onCompleted(boolean result);
    }

    public static FirebaseNetworkManager getInstance(Context context) {
        if(mSelf == null) {
            mSelf = new FirebaseNetworkManager(context);
        }
        mSelf.mContext = context;
        return mSelf;
    }

    public FirebaseNetworkManager(Context context) {
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
    }

    public void createAccount(String email, String password, final FirebaseNetworkCallback callback) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        JWLog.d("", "@@@ createUserWithEmail:onComplete:" + task.isSuccessful());
                        callback.onCompleted(task.isSuccessful());
                    }
                });
    }


}
