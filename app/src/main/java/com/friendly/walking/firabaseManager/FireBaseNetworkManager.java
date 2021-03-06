package com.friendly.walking.firabaseManager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.ImageView;
import com.friendly.walking.util.JWToast;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.friendly.walking.GlobalConstantID;
import com.friendly.walking.R;
import com.friendly.walking.activity.ChangePasswordActivity;
import com.friendly.walking.broadcast.JWBroadCast;
import com.friendly.walking.dataSet.LocationData;
import com.friendly.walking.dataSet.PetData;
import com.friendly.walking.dataSet.UserData;
import com.friendly.walking.dataSet.WalkingData;
import com.friendly.walking.preference.PreferencePhoneShared;
import com.friendly.walking.util.CommonUtil;
import com.friendly.walking.util.Crypto;
import com.friendly.walking.util.JWLog;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
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

import org.w3c.dom.Text;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * Created by jungjiwon on 2017. 10. 25..
 */

public class FireBaseNetworkManager implements GoogleApiClient.OnConnectionFailedListener {

    public static final String                  DB_TABLE_USER = "user";
    public static final String                  DB_TABLE_PET = "pet";
    public static final int                     RC_GOOGLE_SIGN_IN = 9001;


    private static FireBaseNetworkManager       mSelf;
    private Context                             mContext;

    private FirebaseDatabase                    firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference                   databaseReference = firebaseDatabase.getReference();

    // storage
    private FirebaseStorage                     firebaseStorage;
    private StorageReference                    storageRef;

    // firebase email 로그인 인증
    private FirebaseAuth                        mAuth;
    private FirebaseAuth.AuthStateListener      mAuthListener;
    private Task<AuthResult>                    mTask;

    // google login api
    public GoogleApiClient                      mGoogleApiClient;

    // facebook login
    private CallbackManager                     mCallbackManager;

    private UserData                            mUserData;
    private List<PetData>                       mPetList;

    private long                                mUserIndex = -1;


    public interface FireBaseNetworkCallback {
        public void onCompleted(boolean result, Object object);
    }

    public static FireBaseNetworkManager getInstance(AppCompatActivity activity) {
        if(mSelf == null) {
            mSelf = new FireBaseNetworkManager(activity);
        }
        mSelf.mContext = activity;

        if(!mSelf.checkInternetConnection()) {
            JWToast.showToast(R.string.internet_connection_faild);
        }

        return mSelf;
    }

    public static FireBaseNetworkManager getInstance(Context context) {
        if(mSelf == null) {
            mSelf = new FireBaseNetworkManager(context);
        }
        mSelf.mContext = context;

        if(!mSelf.checkInternetConnection()) {
            JWToast.showToast(R.string.internet_connection_faild);
        }
        return mSelf;
    }

    private FireBaseNetworkManager(Context context) {
        JWLog.e("FireBaseNetworkManager context "+context);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    try {
                        List<String> list = user.getProviders();
                        String provider = list.get(0).toString();

                        JWLog.e("", "onAuthStateChanged provider :"+provider);

                        /*
                        if(!PreferencePhoneShared.getAutoLoginYn(mContext)) {
                            logoutAccount();
                            return;
                        }
*/
                        String key = user.getUid().substring(0, 16);
                        String email = user.getEmail();
                        if(TextUtils.isEmpty(email)) {
                            email = user.getDisplayName();
                        }
                        String encryptedEmail = Crypto.encryptAES(CommonUtil.urlEncoding(email, 0), key);
                        PreferencePhoneShared.setLoginYn(mContext, true);
                        if(provider.equals("google.com")) {
                            PreferencePhoneShared.setAutoLoginType(mContext, GlobalConstantID.LOGIN_TYPE_GOOGLE);
                        } else if(provider.equals("facebook.com")) {
                            PreferencePhoneShared.setAutoLoginType(mContext, GlobalConstantID.LOGIN_TYPE_FACEBOOK);
                            facebookUpdateUI(email);
                        } else if(provider.equals("password")) {
                            PreferencePhoneShared.setAutoLoginType(mContext, GlobalConstantID.LOGIN_TYPE_EMAIL);
                            emailUpdateUI(email);
                        }

                        //PreferencePhoneShared.setAutoLoginYn(mContext, true);
                        PreferencePhoneShared.setLoginID(mContext, encryptedEmail);
                        PreferencePhoneShared.setUserUID(mContext, user.getUid());
                        //PreferencePhoneShared.setNickName(mContext, user.getDisplayName());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // User is signed in
                    JWLog.e("", "@@@ onAuthStateChanged:signed_in:" + user.getUid()+", displayName:"+user.getDisplayName());

                } else {
                    // User is signed out
                    JWLog.e("", "@@@ onAuthStateChanged:signed_out");
                    mUserData = null;
                    mPetList = null;

                    if(PreferencePhoneShared.getAutoLoginType(mContext) != GlobalConstantID.LOGIN_TYPE_KAKAO) {
                        PreferencePhoneShared.setLoginYn(mContext, false);
                        PreferencePhoneShared.setAutoLoginType(mContext, GlobalConstantID.LOGIN_TYPE_NONE);
                        PreferencePhoneShared.setAutoLoginYn(mContext, false);
                        PreferencePhoneShared.setLoginID(mContext, "");
                        PreferencePhoneShared.setUserUID(mContext, "");
                        PreferencePhoneShared.setNickName(mContext, "");
                        PreferencePhoneShared.setWalkingCoin(mContext, 0);
                        PreferencePhoneShared.setPetName(mContext, "");

                        PreferencePhoneShared.setNotificationYn(mContext, false);
                        PreferencePhoneShared.setGeoNotificationYn(mContext, false);
                        PreferencePhoneShared.setLocationYn(mContext, false);

                        PreferencePhoneShared.setMyLocationAcceptedYn(mContext, false);
                        PreferencePhoneShared.setChattingAcceptYn(mContext, false);
                    }
                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);
        firebaseStorage = FirebaseStorage.getInstance();
        storageRef = firebaseStorage.getReference();

    }

    public void reset() {
        mUserData = null;
        mPetList = null;
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

    public void deleteFireBaseUser(final FireBaseNetworkCallback callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null) {
            JWLog.e("계정 삭제중 오류 발생 user : "+user);
            JWToast.showToast("계정 삭제중 오류 발생");
            return;
        }
        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            JWLog.e("User account deleted.");
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
    }

    public void deleteUserData(final FireBaseNetworkCallback callback) {
        JWLog.e("", "@@@ ");

        String uid = PreferencePhoneShared.getUserUid(mContext);

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

    public void deleteUserImage(final FireBaseNetworkCallback callback) {
        try {
            String key = PreferencePhoneShared.getUserUid(mContext);
            String paddedKey = key.substring(0, 16);

            JWLog.e("", "uid :" + paddedKey);

            String decEmail = CommonUtil.urlDecoding(Crypto.decryptAES(PreferencePhoneShared.getLoginID(mContext), paddedKey));
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            Uri uri = Uri.parse(path.getAbsolutePath() + "/" + decEmail + "_pet_profile.jpg");

            storageRef.child("profile/" + uri.getLastPathSegment()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    if(callback != null) {
                        callback.onCompleted(true, null);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if(callback != null) {
                        callback.onCompleted(false, null);
                    }
                }
            });

        } catch(Exception e) {
            e.printStackTrace();
        }
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
                        mUserData = userData;
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

    public void readPetData(final String email, final FireBaseNetworkManager.FireBaseNetworkCallback callback) {
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
                        if(userData.pet_list != null) {
                            mPetList = userData.pet_list;
                        }
                        callback.onCompleted(true, userData.pet_list);
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

    public void readWalkingTimeList(final String email, final FireBaseNetworkManager.FireBaseNetworkCallback callback) {
        if(mUserData != null) {
            if(callback != null) {
                JWLog.e("userData walking_list : "+mUserData.walking_time_list);
                callback.onCompleted(true, mUserData.walking_time_list);
            }
        } else {
            readUserData(email, new FireBaseNetworkCallback() {
                @Override
                public void onCompleted(boolean result, Object object) {
                    if(result) {
                        if(mUserData != null) {
                            if(callback != null) {
                                JWLog.e("callback walking_list : "+mUserData.walking_time_list);
                                callback.onCompleted(true, mUserData.walking_time_list);
                                return;
                            }
                        }
                    }

                    if (callback != null) {
                        callback.onCompleted(false, null);
                    }
                }
            });
        }
    }

    public void refreshWalkingTimeList(final String email, final FireBaseNetworkManager.FireBaseNetworkCallback callback) {
        mUserData = null;
        readUserData(email, new FireBaseNetworkCallback() {
            @Override
            public void onCompleted(boolean result, Object object) {
                if(result) {
                    if(mUserData != null) {
                        if(callback != null) {
                            JWLog.e("callback walking_list : "+mUserData.walking_time_list);
                            callback.onCompleted(true, mUserData.walking_time_list);
                            return;
                        }
                    }
                }
                if (callback != null) {
                    callback.onCompleted(false, null);
                }
            }
        });
    }

    public void readWalkingLocationList(final String email, final FireBaseNetworkManager.FireBaseNetworkCallback callback) {
        if(mUserData != null) {
            if(callback != null) {
                JWLog.e("userData walking_location_list : "+mUserData.walking_location_list);
                callback.onCompleted(true, mUserData.walking_location_list);
            }
        } else {
            readUserData(email, new FireBaseNetworkCallback() {
                @Override
                public void onCompleted(boolean result, Object object) {
                    if(result) {
                        if(mUserData != null) {
                            if(callback != null) {
                                JWLog.e("callback walking_location_list : "+mUserData.walking_location_list);
                                callback.onCompleted(true, mUserData.walking_location_list);
                                return;
                            }
                        }
                    }
                    if (callback != null) {
                        callback.onCompleted(false, null);
                    }
                }
            });
        }
    }

    public void readCurrentWalkingList(final FireBaseNetworkManager.FireBaseNetworkCallback callback) {
        final Query myTopPostsQuery = databaseReference.child("walking");

        myTopPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                WalkingData walkingData = null;
                ArrayList<WalkingData> list = new ArrayList<>();
                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    walkingData = data.getValue(WalkingData.class);
                    list.add(walkingData);
                    JWLog.e("","walkingData :"+walkingData);
                }

                if(callback != null) {
                    if (list.size() > 0) {
                        callback.onCompleted(true, list);
                    } else {
                        callback.onCompleted(false, null);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                JWLog.e("","e :" + databaseError.getDetails());

                if (callback != null) {
                    callback.onCompleted(false, null);
                }

            }
        });

    }

    public void readPetSpeciesList(final FireBaseNetworkManager.FireBaseNetworkCallback callback) {
        final Query myTopPostsQuery = databaseReference.child("pet").child("species");

        myTopPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String species;
                ArrayList<String> list = new ArrayList<>();
                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    species = data.getValue(String.class);
                    list.add(species);
                    JWLog.e("","species :"+species);
                }

                if(callback != null) {
                    if (list.size() > 0) {
                        callback.onCompleted(true, list);
                    } else {
                        callback.onCompleted(false, null);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                JWLog.e("","e :" + databaseError.getDetails());
                if (callback != null) {
                    callback.onCompleted(false, null);
                }
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
                if (callback != null) {
                    callback.onCompleted(false, null);
                }
            }
        });

    }

    public void findNickName(final String nickName, final FireBaseNetworkManager.FireBaseNetworkCallback callback) {

        final Query myTopPostsQuery = databaseReference.child("users").orderByChild("mem_nickname").equalTo(nickName);

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
                if (callback != null) {
                    callback.onCompleted(false, null);
                }
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
                    //Toast.makeText(mContext, "Fail !!", Toast.LENGTH_LONG).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            if(callback != null) {
                callback.onCompleted(false, null);
            }
        }

    }

    public void downloadImageForUri(String email, final FireBaseNetworkManager.FireBaseNetworkCallback callback) {
        String url = "profile/"+email+"_pet_profile.jpg";
        storageRef.child(url).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                JWLog.e("@@@@ uri :"+uri.toString());
                if(callback != null) {
                    if(uri != null) {
                        callback.onCompleted(true, uri.toString());
                    } else {
                        callback.onCompleted(false, null);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                exception.printStackTrace();
            }
        });
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

    public void updateWalkingLocationList(String email, final ArrayList<LocationData> list, final FireBaseNetworkManager.FireBaseNetworkCallback callback) {
        JWLog.e("email :"+email+", list :"+list.toString());

        final Query myTopPostsQuery = databaseReference.child("users").orderByChild("mem_email").equalTo(email);

        myTopPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserData userData = null;
                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    userData = data.getValue(UserData.class);
                }
                JWLog.e("","userData :"+userData);
                if(userData == null) {
                    JWToast.showToast("유저 데이터가 없어 위치 정보를 업데이트 하지 못했습니다.");
                    return;
                }

                Date date = new Date(System.currentTimeMillis());
                SimpleDateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd", Locale.KOREA );
                String dateTime = formatter.format(date);

                ArrayList<LocationData> oriList = userData.walking_location_list.get(dateTime);
                if(oriList == null) {
                    oriList = new ArrayList<>();
                }
                oriList.addAll(list);
                userData.walking_location_list.put(dateTime, oriList);

                databaseReference.child("users").child(userData.uid).child("walking_location_list").setValue(userData.walking_location_list);

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

    public void updateWalkingTimeList(String email, final long min, final FireBaseNetworkManager.FireBaseNetworkCallback callback) {
        JWLog.e("email :"+email+", min :"+min);

        final Query myTopPostsQuery = databaseReference.child("users").orderByChild("mem_email").equalTo(email);

        myTopPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserData userData = null;
                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    userData = data.getValue(UserData.class);
                }
                JWLog.e("","userData :"+userData);
                if(userData == null) {
                    JWToast.showToast("유저 데이터가 없어 산책 시간을 업데이트 하지 못했습니다.");
                    return;
                }

                long cur = System.currentTimeMillis();
                Date date = new Date(cur);
                SimpleDateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd", Locale.KOREA );
                String dateTime = formatter.format(date);

                String oriTime = userData.walking_time_list.get(dateTime);
                if(oriTime == null) {
                    oriTime = "0";
                }
                long resultTime = Long.parseLong(oriTime) + min;
                userData.walking_time_list.put(dateTime, ""+resultTime);

                databaseReference.child("users").child(userData.uid).child("walking_time_list").setValue(userData.walking_time_list);

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

    public void updateLastLoginTime(String uid, final FireBaseNetworkManager.FireBaseNetworkCallback callback) {
        JWLog.e("uid :"+uid);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserData data = dataSnapshot.getValue(UserData.class);
                JWLog.e("", "@@@ onDataChange data :"+data);
                if(callback != null) {
                    if (data != null) {
                        callback.onCompleted(true, data);
                    } else {
                        callback.onCompleted(false, null);
                    }
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

        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd:HH:mm:ss", Locale.KOREA );
        String dateTime = formatter.format(date);
        databaseReference.child("users").child(uid).child("mem_last_login_datetime").setValue(dateTime);
    }

    public void updateAutoLoginCheck(String uid, boolean result, final FireBaseNetworkManager.FireBaseNetworkCallback callback) {
        JWLog.e("uid :"+uid);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserData data = dataSnapshot.getValue(UserData.class);
                JWLog.e("", "@@@ onDataChange data :"+data);
                if(callback != null) {
                    if (data != null) {
                        callback.onCompleted(true, data);
                    } else {
                        callback.onCompleted(false, null);
                    }
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

        databaseReference.child("users").child(uid).child("mem_auto_login").setValue(result);
    }

    public void updateUserData(String email, final UserData modifiedUserData, final FireBaseNetworkManager.FireBaseNetworkCallback callback) {
        JWLog.e("email :"+email+", modifiedUserData :"+modifiedUserData);

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

        databaseReference.child("users").child(modifiedUserData.uid).setValue(modifiedUserData);
    }

    public void updatePetData(UserData userData, final PetData petData, final FireBaseNetworkManager.FireBaseNetworkCallback callback) {
        JWLog.e("email :"+userData.mem_email+", petData :"+petData);

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

        List<PetData> petList = new ArrayList<>();
        petList.add(petData);
        databaseReference.child("users").child(userData.uid).child("pet_list").setValue(petList);
    }

    public void updateLocationYn(String uid, boolean result, final FireBaseNetworkManager.FireBaseNetworkCallback callback) {
        JWLog.e("uid :"+uid);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserData data = dataSnapshot.getValue(UserData.class);
                JWLog.e("", "@@@ onDataChange data :"+data);
                if(callback != null) {
                    if (data != null) {
                        callback.onCompleted(true, data);
                    } else {
                        callback.onCompleted(false, null);
                    }
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

        databaseReference.child("users").child(uid).child("mem_location_yn").setValue(result);
    }

    public void updateNotificationYn(String uid, boolean result, final FireBaseNetworkManager.FireBaseNetworkCallback callback) {
        JWLog.e("uid :"+uid);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserData data = dataSnapshot.getValue(UserData.class);
                JWLog.e("", "@@@ onDataChange data :"+data);
                if(callback != null) {
                    if (data != null) {
                        callback.onCompleted(true, data);
                    } else {
                        callback.onCompleted(false, null);
                    }
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

        databaseReference.child("users").child(uid).child("mem_notification_yn").setValue(result);
    }

    public void updateGeoNotificationYn(String uid, boolean result, final FireBaseNetworkManager.FireBaseNetworkCallback callback) {
        JWLog.e("uid :"+uid);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserData data = dataSnapshot.getValue(UserData.class);
                JWLog.e("", "@@@ onDataChange data :"+data);
                if(callback != null) {
                    if (data != null) {
                        callback.onCompleted(true, data);
                    } else {
                        callback.onCompleted(false, null);
                    }
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

        databaseReference.child("users").child(uid).child("mem_geo_notification_yn").setValue(result);
    }

    public void updateWalkingMyLocationYn(String uid, boolean result, final FireBaseNetworkManager.FireBaseNetworkCallback callback) {
        JWLog.e("uid :"+uid);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserData data = dataSnapshot.getValue(UserData.class);
                JWLog.e("", "@@@ onDataChange data :"+data);
                if(callback != null) {
                    if (data != null) {
                        callback.onCompleted(true, data);
                    } else {
                        callback.onCompleted(false, null);
                    }
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

        databaseReference.child("users").child(uid).child("mem_walking_my_location_yn").setValue(result);
    }

    public void updateWalkingChattingYn(String uid, boolean result, final FireBaseNetworkManager.FireBaseNetworkCallback callback) {
        JWLog.e("uid :"+uid);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserData data = dataSnapshot.getValue(UserData.class);
                JWLog.e("", "@@@ onDataChange data :"+data);
                if(callback != null) {
                    if (data != null) {
                        callback.onCompleted(true, data);
                    } else {
                        callback.onCompleted(false, null);
                    }
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

        databaseReference.child("users").child(uid).child("mem_walking_chatting_yn").setValue(result);
    }

    public void logoutAccount() {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();

        PreferencePhoneShared.setAutoLoginYn(mContext, false);
        PreferencePhoneShared.setLoginYn(mContext, false);
        PreferencePhoneShared.setUserUID(mContext, "");
        PreferencePhoneShared.setLoginPassword(mContext, "");
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
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mSelf.mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .enableAutoManage(activity /* FragmentActivity */, mSelf /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        activity.startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
    }

    public void facebookSignIn(AppCompatActivity activity, LoginButton loginButton, final FireBaseNetworkManager.FireBaseNetworkCallback callback) {
        mCallbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                JWLog.e("", "facebook:onSuccess:" + loginResult+", loginResult.getAccessToken() "+loginResult.getAccessToken());
                handleFacebookAccessToken(loginResult.getAccessToken(), callback);
            }

            @Override
            public void onCancel() {
                JWLog.e("", "facebook:onCancel");
                JWToast.showToast("취소 되었습니다");
                if(callback != null) {
                    callback.onCompleted(false, null);
                }
            }

            @Override
            public void onError(FacebookException error) {
                JWLog.e("", "facebook:onError" + error);
                JWToast.showToast("에러가 발생 했습니다.");
                if(callback != null) {
                    callback.onCompleted(false, null);
                }
            }
        });
    }

    public void initFaceBookCallbackManager(final FireBaseNetworkManager.FireBaseNetworkCallback callback) {

        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                JWLog.e("", "facebook:onSuccess:" + loginResult+", loginResult.getAccessToken() "+loginResult.getAccessToken());
                handleFacebookAccessToken(loginResult.getAccessToken(), callback);
            }

            @Override
            public void onCancel() {
                JWLog.e("", "facebook:onCancel");
                JWToast.showToast("Facebook 로그인 취소 되었습니다");
                if(callback != null) {
                    callback.onCompleted(false, null);
                }
            }

            @Override
            public void onError(FacebookException error) {
                JWLog.e("", "facebook:onError" + error);
                JWToast.showToast("Facebook 로그인 에러가 발생 했습니다.");
                if(callback != null) {
                    callback.onCompleted(false, null);
                }
            }
        });

    }

    public void readVersionInfo(final FireBaseNetworkManager.FireBaseNetworkCallback callback) {

        final Query myTopPostsQuery = databaseReference.child("version");

        myTopPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                JWLog.e(""+dataSnapshot+", "+dataSnapshot.getValue());

                if(callback != null) {
                    if (dataSnapshot.getValue() != null) {
                        callback.onCompleted(true, dataSnapshot.getValue());
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

    public CallbackManager getFacebookCallback() {
        return mCallbackManager;
    }

    public FirebaseUser getCurrentUser() {
        if(mAuth != null) {
            return mAuth.getCurrentUser();
        }
        return null;
    }

    public void firebaseAuthWithGoogle(GoogleSignInAccount acct, final FireBaseNetworkCallback callback) {
        JWLog.d("", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            JWLog.d("", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(callback != null) {
                                callback.onCompleted(task.isSuccessful(), user);
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            JWLog.w("", "signInWithCredential:failure" + task.getException());
                            JWToast.showToast("Authentication failed.");
                            if(callback != null) {
                                callback.onCompleted(task.isSuccessful(), null);
                            }
                        }
                    }
                });
    }

    private void revokeAuthGoogleAccess() {
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
            new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {

                }
            });
    }

    private void handleFacebookAccessToken(AccessToken token, final FireBaseNetworkManager.FireBaseNetworkCallback callback) {
        JWLog.d("", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            JWLog.d("", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            if(callback != null) {
                                callback.onCompleted(true, task);
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            JWLog.w("", "signInWithCredential:failure "+ task.getException());
                            if(callback != null) {
                                callback.onCompleted(false, null);
                            }
                        }

                        if(callback != null) {
                            callback.onCompleted(false, null);
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        JWLog.e("",connectionResult.getErrorMessage());
    }

    public boolean checkInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    public long getUserIndex() {
        return mUserIndex;
    }

    public void facebookUpdateUI(String email) {
        JWLog.e("","");
        Intent intent = new Intent(JWBroadCast.BROAD_CAST_FACEBOOK_LOGIN);
        intent.putExtra("email", email);

        JWBroadCast.sendBroadcast(mContext, intent);
    }

    public void emailUpdateUI(String email) {
        JWLog.e("","");
        Intent intent = new Intent(JWBroadCast.BROAD_CAST_EMAIL_LOGIN);
        intent.putExtra("email", email);

        JWBroadCast.sendBroadcast(mContext, intent);
    }

    public void updteWalkingData(WalkingData data, final FireBaseNetworkCallback callback) {
        JWLog.e("", "@@@ ");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                WalkingData data = dataSnapshot.getValue(WalkingData.class);
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
        databaseReference.child("walking").child(data.uid).setValue(data);
    }

    public void deleteWalkingData(final FireBaseNetworkCallback callback) {
        JWLog.e("", "@@@ ");

        String uid = PreferencePhoneShared.getUserUid(mContext);

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

        databaseReference.child("walking").child(uid).removeValue();
    }

    public void updateWalkingCoin(String uid, int coin, final FireBaseNetworkManager.FireBaseNetworkCallback callback) {
        JWLog.e("uid :"+uid);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserData data = dataSnapshot.getValue(UserData.class);
                JWLog.e("", "@@@ onDataChange data :"+data);
                if(callback != null) {
                    if (data != null) {
                        callback.onCompleted(true, data);
                    } else {
                        callback.onCompleted(false, null);
                    }
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

        databaseReference.child("users").child(uid).child("walking_coin").setValue(coin);
    }
}
