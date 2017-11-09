package com.friendly.walking.network;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

import com.friendly.walking.R;
import com.friendly.walking.activity.KakaoSignupActivity;
import com.friendly.walking.activity.LoginActivity;
import com.friendly.walking.main.MainActivity;
import com.friendly.walking.util.JWLog;
import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;

/**
 * Created by jungjiwon on 2017. 11. 7..
 */

public class KakaoLoginManager {

    private static KakaoLoginManager                    mSelf;
    private Context                                     mContext;
    private SessionCallback                             mSessionCallback;
    private UserProfile                                 mUserProfile;

    private class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            JWLog.e("","");
            redirectSignupActivity();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            JWLog.e("","");
            if(exception != null) {
                JWLog.e("", ""+exception);
            }
        }
    }

    public interface KakaoLoginManagerCallback {
        public void onCompleted(boolean result, Object object);
    }

    public static KakaoLoginManager getInstance(Context context) {
        if(mSelf == null) {
            mSelf = new KakaoLoginManager(context);
        }
        mSelf.mContext = context;

        return mSelf;
    }

    private KakaoLoginManager(Context context) {
        mSessionCallback = new SessionCallback();
        Session.getCurrentSession().addCallback(mSessionCallback);
    }

    public void terminate() {
        Session.getCurrentSession().removeCallback(mSessionCallback);
        mSelf = null;
        mContext = null;
        mUserProfile = null;
        mSessionCallback = null;
    }

    public void redirectSignupActivity() {
        JWLog.e("","");
        final Intent intent = new Intent(mContext, KakaoSignupActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        mContext.startActivity(intent);
    }

    public void redirectLoginActivity(boolean refresh) {
        JWLog.e("","");
        Intent intent = new Intent(mContext, LoginActivity.class);
        if(refresh) {
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intent.putExtra("key", "refresh");
        }
        mContext.startActivity(intent);
    }

    public void redirectMainActivity() {
        JWLog.e("","");
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);
    }

    public boolean handleKakaoActivityResult(int requestCode, int resultCode, Intent data) {
        return Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data);
    }

    public boolean hasKakaoLoginSession() {
        JWLog.e("isClosed : "+ Session.getCurrentSession().isClosed());

        return !Session.getCurrentSession().isClosed();
    }

    /**
     * 사용자의 상태를 알아 보기 위해 me API 호출을 한다.
     */
    public void requestMe(final KakaoLoginManagerCallback callback) { //유저의 정보를 받아오는 함수
        JWLog.e("","");

        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                JWLog.e("","requestMe onFailure");
                String message = "failed to get user info. msg=" + errorResult;
                JWLog.e("", message);

                ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                if (result == ErrorCode.CLIENT_ERROR_CODE) {
                    if(callback != null) {
                        callback.onCompleted(false, null);
                    }
                } else {
                    //redirectLoginActivity();
                }
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                JWLog.e(""," requestMe onSessionClosed");
                if(callback != null) {
                    callback.onCompleted(false, null);
                }
                //redirectLoginActivity();
            }

            @Override
            public void onNotSignedUp() {
                JWLog.e("","requestMe onNotSignedUp");
                if(callback != null) {
                    callback.onCompleted(false, null);
                }
            } // 카카오톡 회원이 아닐 시 showSignup(); 호출해야함

            @Override
            public void onSuccess(UserProfile userProfile) {  //성공 시 userProfile 형태로 반환
                JWLog.e("", "requestMe onSuccess UserProfile : " + userProfile);
                mUserProfile = userProfile;
                if(callback != null) {
                    callback.onCompleted(true, userProfile);
                }
                //redirectMainActivity(); // 로그인 성공시 MainActivity로
            }
        });
    }

    public void requestLogout(final KakaoLoginManagerCallback callback) {
        UserManagement.requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                if(callback != null) {
                    callback.onCompleted(true, null);
                }
            }
        });
    }

    public void unlinkApp(final KakaoLoginManagerCallback callback) {

        UserManagement.requestUnlink(new UnLinkResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                JWLog.e("", errorResult.toString());
                if(callback != null) {
                    callback.onCompleted(false, null);
                }
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                //redirectLoginActivity();
                JWLog.e("","onSessionClosed");
                if(callback != null) {
                    callback.onCompleted(false, null);
                }
            }

            @Override
            public void onNotSignedUp() {
                JWLog.e("","onNotSignedUp");
                //redirectSignupActivity();
                if(callback != null) {
                    callback.onCompleted(false, null);
                }
            }

            @Override
            public void onSuccess(Long userId) {
                JWLog.e("","userId :"+userId);
                if(callback != null) {
                    callback.onCompleted(true, ""+userId);
                }
            }
        });

        /*
        final String appendMessage = mContext.getString(R.string.com_kakao_confirm_unlink);
        new AlertDialog.Builder(mContext)
                .setMessage(appendMessage)
                .setPositiveButton(mContext.getString(R.string.com_kakao_ok_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                UserManagement.requestUnlink(new UnLinkResponseCallback() {
                                    @Override
                                    public void onFailure(ErrorResult errorResult) {
                                        JWLog.e("", errorResult.toString());
                                        if(callback != null) {
                                            callback.onCompleted(false, null);
                                        }
                                    }

                                    @Override
                                    public void onSessionClosed(ErrorResult errorResult) {
                                        //redirectLoginActivity();
                                        JWLog.e("","onSessionClosed");
                                        if(callback != null) {
                                            callback.onCompleted(false, null);
                                        }
                                    }

                                    @Override
                                    public void onNotSignedUp() {
                                        JWLog.e("","onNotSignedUp");
                                        //redirectSignupActivity();
                                        if(callback != null) {
                                            callback.onCompleted(false, null);
                                        }
                                    }

                                    @Override
                                    public void onSuccess(Long userId) {
                                        JWLog.e("","userId :"+userId);
                                        if(callback != null) {
                                            callback.onCompleted(true, ""+userId);
                                        }
                                    }
                                });
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(mContext.getString(R.string.com_kakao_cancel_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
*/
    }

    public String getEmail() {
        if(mUserProfile != null) {
            return mUserProfile.getEmail();
        }
        return null;
    }

    public String getNickName() {
        if(mUserProfile != null) {
            return mUserProfile.getNickname();
        }
        return null;
    }

    public String getProfileImagePath() {
        if(mUserProfile != null) {
            return mUserProfile.getProfileImagePath();
        }
        return null;
    }

}
