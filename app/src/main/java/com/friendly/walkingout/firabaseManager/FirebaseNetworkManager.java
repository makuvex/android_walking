package com.friendly.walkingout.firabaseManager;

import android.content.Context;

import com.friendly.walkingout.util.JWLog;
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

//    private FirebaseDatabase                    firebaseDatabase = FirebaseDatabase.getInstance();
//    private DatabaseReference                   databaseReference = firebaseDatabase.getReference();

    // firebase 로그인 인증
//    private FirebaseAuth                        mAuth = FirebaseAuth.getInstance();
//    private FirebaseAuth.AuthStateListener      mAuthListener;

    public static FirebaseNetworkManager getInstance(Context context) {
        if(mSelf == null) {
            mSelf = new FirebaseNetworkManager(context);
        }
        return mSelf;
    }

    public FirebaseNetworkManager(Context context) {
        mContext = context;

//        mAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//                if (user != null) {
//                    // User is signed in
//                    JWLog.d("", "@@@ onAuthStateChanged:signed_in:" + user.getUid());
//                } else {
//                    // User is signed out
//                    JWLog.d("", "@@@ onAuthStateChanged:signed_out");
//                }
//            }
//        };
    }




}
