package com.friendly.walking.activity;

import android.app.DatePickerDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;

import com.friendly.walking.BuildConfig;
import com.friendly.walking.dataSet.WalkingData;
import com.friendly.walking.fragment.SettingFragment;
import com.friendly.walking.receiver.LoginOutReceiver;
import com.friendly.walking.util.JWToast;

import com.friendly.walking.ApplicationPool;
import com.friendly.walking.GlobalConstantID;
import com.friendly.walking.R;
import com.friendly.walking.broadcast.JWBroadCast;
import com.friendly.walking.dataSet.PetData;
import com.friendly.walking.dataSet.PetRelationData;
import com.friendly.walking.dataSet.UserData;
import com.friendly.walking.firabaseManager.FireBaseNetworkManager;
import com.friendly.walking.main.MainActivity;
import com.friendly.walking.permission.PermissionManager;
import com.friendly.walking.preference.PreferencePhoneShared;
import com.friendly.walking.util.CommonUtil;
import com.friendly.walking.util.Crypto;
import com.friendly.walking.util.JWLog;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import stfalcon.universalpickerdialog.UniversalPickerDialog;

import static com.friendly.walking.GlobalConstantID.LOGIN_TYPE_EMAIL;


/**
 * Created by jungjiwon on 2017. 10. 25..
 */

public class SignUpPetActivity extends BaseActivity implements View.OnFocusChangeListener,
                                                                    DatePickerDialog.OnDateSetListener,
                                                                    UniversalPickerDialog.OnPickListener,
                                                                    View.OnClickListener {

    public static final int                     PICKER_DATA_TYPE_SPECIES = 0;
    public static final int                     PICKER_DATA_TYPE_RELATION = 1;

    public static final int                     REQ_CODE_CAPTURE_IMAGE = 100;
    public static final int                     REQ_CODE_PICK_FROM_ALBUM = 101;
    public static final int                     REQ_CODE_CROP_FROM_ALBUM = 102;

    protected String                              mEmail;
    protected String                              mPassword;
    protected EditText                            mPetName;
    protected EditText                            mPetBirthDate;
    protected EditText                            mPetSpecies;
    protected EditText                            mPetRelation;

    protected ImageButton                         mAddProfile;
    protected ImageButton                         mMaleCheck;
    protected ImageButton                         mFemaleCheck;
    protected Button                              mSignUp;

    protected static String[]                     mPetData;
    protected static PetRelationData[]            mRelationData;
    protected int                                 mPetGender = -1;
    protected Uri                                 mImageCaptureUri;
    protected int                                 mSignUpType;

    protected UserData                            mUserData;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_signup_pet);

        mAddProfile = (ImageButton) findViewById(R.id.add_profile);
        mPetName = (EditText) findViewById(R.id.pet_name);
        mMaleCheck = (ImageButton) findViewById(R.id.male_check);
        mFemaleCheck = (ImageButton) findViewById(R.id.female_check);
        mPetBirthDate = (EditText) findViewById(R.id.pet_birth_date);
        mPetSpecies = (EditText) findViewById(R.id.pet_species);
        mPetRelation = (EditText) findViewById(R.id.pet_relation);
        mSignUp = (Button) findViewById(R.id.sign_up);
        mAddProfile = (ImageButton)findViewById(R.id.add_profile);

        mPetName.setOnFocusChangeListener(this);
        mPetBirthDate.setOnFocusChangeListener(this);
        mPetSpecies.setOnFocusChangeListener(this);
        mPetRelation.setOnFocusChangeListener(this);
        mAddProfile.setOnClickListener(this);

//        mPetData = new PetData[]{
//                new PetData(0, "웰시코기"),
//                new PetData(1, "말티즈"),
//                new PetData(2, "시츄"),
//                new PetData(3, "이탈리안 그레이하운드"),
//                new PetData(4, "사모예드")};

        mRelationData = new PetRelationData[]{
                new PetRelationData(0, "엄마"),
                new PetRelationData(1, "아빠"),
                new PetRelationData(2, "누나"),
                new PetRelationData(3, "언니"),
                new PetRelationData(4, "오빠"),
                new PetRelationData(5, "형"),
                new PetRelationData(6, "집사"),
                new PetRelationData(7, "할머니"),
                new PetRelationData(8, "할아버지"),
                new PetRelationData(9, "친구")};
//
        Intent intent = getIntent();
        //mEmail = intent.getStringExtra(GlobalConstantID.SIGN_UP_EMAIL);

        mPassword = intent.getStringExtra(GlobalConstantID.SIGN_UP_PASSWORD);
        mSignUpType = intent.getIntExtra(GlobalConstantID.SIGN_UP_TYPE, LOGIN_TYPE_EMAIL);

        ApplicationPool pool = (ApplicationPool) getApplicationContext();
        mUserData = (UserData) pool.getExtra(SignUpActivity.KEY_USER_DATA, getIntent());
        if(mUserData != null) {
            mEmail = mUserData.mem_email;
        }

        JWLog.e("","mUserData : "+mUserData+", mEmail :"+mEmail);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

    public void onClickCallback(View v) {
        JWLog.e("","v : "+v.getId());

        if(v.getId() == R.id.male_check) {
            if(mFemaleCheck.isSelected()) {
                mFemaleCheck.setSelected(false);
            }
            mMaleCheck.setSelected(!mMaleCheck.isSelected());
            mPetGender = 0;
        } else if(v.getId() == R.id.female_check) {
            if(mMaleCheck.isSelected()) {
                mMaleCheck.setSelected(false);
            }
            mFemaleCheck.setSelected(!mFemaleCheck.isSelected());
            mPetGender = 1;
        } else if(v == mPetBirthDate) {
            showDatePicker();
        } else if(v == mPetSpecies) {
            setProgressBar(View.VISIBLE);
            FireBaseNetworkManager.getInstance(this).readPetSpeciesList(new FireBaseNetworkManager.FireBaseNetworkCallback() {
                @Override
                public void onCompleted(boolean result, Object object) {
                    setProgressBar(View.GONE);
                    ArrayList<String> list = (ArrayList<String>) object;
                    mPetData = list.toArray(new String[list.size()]);

                    showPetSpeciesDialog();

//                    mPetData = new PetData[]{
//                            new PetData(0, "웰시코기"),
//                            new PetData(1, "말티즈"),
//                            new PetData(2, "시츄"),
//                            new PetData(3, "이탈리안 그레이하운드"),
//                            new PetData(4, "사모예드")};

                    //JWLog.e("result :"+result+", object :"+object);
                    //mCurrentWalkingList = (ArrayList<WalkingData>) object;

//                    if(result) {
//                        updateWalkingList(mCurrentWalkingList.size());
//                    }
                }
            });

            //showPetSpeciesDialog();
        } else if(v == mPetRelation) {
            showPetRelationDialog();
        } else if(v == mSignUp) {
            if(checkEmptyFields() && !BuildConfig.IS_DEBUG ) {
                JWToast.showToast("비어있는 항목을 입력해 주세요.");
            } else {
                setProgressBar(View.VISIBLE);
                createPetData();

                if(mSignUpType == GlobalConstantID.LOGIN_TYPE_KAKAO) {
                    FireBaseNetworkManager.getInstance(this).findUserEmail(mUserData.mem_email, new FireBaseNetworkManager.FireBaseNetworkCallback() {
                        @Override
                        public void onCompleted(boolean result, Object object) {
                            setProgressBar(View.INVISIBLE);

                            if(result) {
                                JWToast.showToast(getApplication().getText(R.string.unavailable_id).toString());
                            } else {
                                //Toast.makeText(getApplicationContext(), R.string.available_id, Toast.LENGTH_SHORT).show();
                                queryUserIndex(null);
                            }
                        }
                    });
                } else if(mSignUpType == GlobalConstantID.LOGIN_TYPE_FACEBOOK || mSignUpType == GlobalConstantID.LOGIN_TYPE_GOOGLE ) {

                    queryUserIndex(mUserData.uid);
                } else {
                    FireBaseNetworkManager.getInstance(this).createAccount(mEmail, mPassword, new FireBaseNetworkManager.FireBaseNetworkCallback() {
                        @Override
                        public void onCompleted(boolean result, Object object) {
                            final Task<AuthResult> task = (Task<AuthResult>) object;
                            JWLog.e("@@ result "+result+ ", object "+object);

                            if (result) {
                                JWToast.showToast("계정 만들기 성공");

                                mUserData.uid = task.getResult().getUser().getUid();
                                queryUserIndex(mUserData.uid);
                            } else {
                                setProgressBar(View.INVISIBLE);
                                JWToast.showToast("계정 만들기 실패");
                            }
                        }
                    });
                }
            }
        }
    }

    public void queryUserIndex(final String uid) {

        FireBaseNetworkManager.getInstance(getApplicationContext()).queryUserIndex(new FireBaseNetworkManager.FireBaseNetworkCallback() {
            @Override
            public void onCompleted(boolean result, Object object) {
                JWLog.e("result "+result);
                if(result) {
                    long userIndex = FireBaseNetworkManager.getInstance(getApplicationContext()).getUserIndex();
                    mUserData.member_index = ++userIndex;

                    createUserData(uid);
                }
            }
        });
    }

    private void createUserData(final String uid) {

        FireBaseNetworkManager.getInstance(getApplicationContext()).createUserData(mUserData, new FireBaseNetworkManager.FireBaseNetworkCallback() {
            @Override
            public void onCompleted(boolean result, Object object) {
                setProgressBar(View.INVISIBLE);

                if (result) {
                    JWToast.showToast("유저 데이터 만들기 성공");

                    try {
                        if(uid != null) {
                            if(mSignUpType == LOGIN_TYPE_EMAIL) {
                                String key = uid.substring(0, 16);
                                String encryptedPassword = "";
                                encryptedPassword = Crypto.encryptAES(CommonUtil.urlEncoding(mPassword, 0), key);
                                PreferencePhoneShared.setLoginPassword(getApplicationContext(), encryptedPassword);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        JWToast.showToast("preference 저장 실패");
                    }
                    if (mImageCaptureUri != null) {
                        try {
                            FireBaseNetworkManager.getInstance(SignUpPetActivity.this).uploadProfileImage(mImageCaptureUri, new FireBaseNetworkManager.FireBaseNetworkCallback() {
                                @Override
                                public void onCompleted(boolean result, Object object) {

                                    if (result) {
                                        JWToast.showToast("프로필 사진 업로드 성공");

                                        if(PreferencePhoneShared.getAutoLoginType(getApplicationContext()) == GlobalConstantID.LOGIN_TYPE_FACEBOOK) {
                                            UpdateMainProfileUI(mEmail, JWBroadCast.BROAD_CAST_FACEBOOK_LOGIN);
                                        } else if(PreferencePhoneShared.getAutoLoginType(getApplicationContext()) == GlobalConstantID.LOGIN_TYPE_GOOGLE) {
                                            UpdateMainProfileUI(mEmail, JWBroadCast.BROAD_CAST_GOOGLE_LOGIN);
                                        } else if(PreferencePhoneShared.getAutoLoginType(getApplicationContext()) == GlobalConstantID.LOGIN_TYPE_KAKAO) {
                                            UpdateMainProfileUI(mEmail, JWBroadCast.BROAD_CAST_KAKAO_LOGIN);
                                        }
                                    } else {
                                        JWToast.showToast( "프로필 사진 업로드 실패");
                                    }
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    JWToast.showToast("유저 데이터 만들기 실패");
                }

                if(PreferencePhoneShared.getAutoLoginType(getApplicationContext()) == LOGIN_TYPE_EMAIL) {
                    // userData 생성 후 readUserData를 바로 읽을 수 없음
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent i = new Intent(JWBroadCast.BROAD_CAST_LOGIN);
                            i.putExtra("email", mEmail);
                            i.putExtra("password", mPassword);
                            i.putExtra("autoLogin", mUserData.mem_auto_login);
                            i.setPackage(getPackageName());
                            //i.setComponent(new ComponentName(getApplicationContext(), LoginOutReceiver.class));
                            //i.setComponent(new ComponentName(getApplicationContext(), SettingFragment.class));

                            JWBroadCast.sendBroadcast(getApplicationContext(), i);
                        }
                    }, 0);
                }

                Intent intent = new Intent(SignUpPetActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        JWLog.d("","hasFocus :"+hasFocus);

        if(hasFocus) {
            onClickCallback(v);
        } else {
            if(v == mPetName) {
                CommonUtil.hideKeyboard(this, v);
            }
        }
    }

    public void UpdateMainProfileUI(String email, String broadCast) {
        JWLog.e("","email :"+email+", broadCast :"+broadCast);
        Intent intent = new Intent(broadCast);
        intent.putExtra("email", email);

        JWBroadCast.sendBroadcast(this, intent);
    }

    private void showDatePicker() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);

        SimpleDateFormat CurYearFormat = new SimpleDateFormat("yyyy");
        SimpleDateFormat CurMonthFormat = new SimpleDateFormat("MM");
        SimpleDateFormat CurDayFormat = new SimpleDateFormat("dd");

        String strCurYear = CurYearFormat.format(date);
        String strCurMonth = CurMonthFormat.format(date);
        String strCurDay = CurDayFormat.format(date);

        DatePickerDialog dialog = new DatePickerDialog(this, this, Integer.parseInt(strCurYear), Integer.parseInt(strCurMonth)-1, Integer.parseInt(strCurDay));
        dialog.show();
    }

    private void showPetSpeciesDialog() {

        new UniversalPickerDialog.Builder(this)
                .setTitle(R.string.pet_species)
                .setListener(this)
                .setContentTextSize(14)
                .setInputs(
                        new UniversalPickerDialog.Input(PICKER_DATA_TYPE_SPECIES, mPetData)
                )
                .setKey(PICKER_DATA_TYPE_SPECIES)
                .show();
    }

    private void showPetRelationDialog() {

        new UniversalPickerDialog.Builder(this)
                .setTitle(R.string.pet_relation)
                .setListener(this)
                .setContentTextSize(14)
                .setInputs(
                        new UniversalPickerDialog.Input(PICKER_DATA_TYPE_RELATION, mRelationData)
                )
                .setKey(PICKER_DATA_TYPE_RELATION)
                .show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        mPetBirthDate.setText( year + getString(R.string.year) + (monthOfYear+1) + getString(R.string.month) + " " + dayOfMonth +getString(R.string.day));
        JWToast.showToast( year + "년 " + (monthOfYear+1) + "월 " + dayOfMonth +"일");
    }

    @Override
    public void onPick(int[] selectedValues, int key) {
        JWLog.e("","selectedValues :"+selectedValues[0]+", key :"+key);
        String data = null;


        if(key == PICKER_DATA_TYPE_SPECIES) {
            data = mPetData[selectedValues[0]];
            mPetSpecies.setText(data);
        } else if(key == PICKER_DATA_TYPE_RELATION) {
            data = mRelationData[selectedValues[0]].getName();
            mPetRelation.setText(data);
        }
        JWToast.showToast(data);
    }

    private boolean checkEmptyFields() {
        boolean checkEmpty = false;

        if(TextUtils.isEmpty(mPetName.getText().toString())) {
            checkEmpty = true;
            mPetName.setHint(R.string.pet_name_hint);
        }
        if(TextUtils.isEmpty(mPetBirthDate.getText().toString())) {
            checkEmpty = true;
            mPetBirthDate.setHint(R.string.birthday);
        }
        if(TextUtils.isEmpty(mPetSpecies.getText().toString())) {
            checkEmpty = true;
            mPetSpecies.setHint(R.string.pet_species);
        }
        if(TextUtils.isEmpty(mPetRelation.getText().toString())) {
            checkEmpty = true;
            mPetRelation.setHint(R.string.pet_relation);
        }
        if(mPetGender == -1) {
            checkEmpty = true;
        }

        if(checkEmpty) {
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        if(!PermissionManager.isAcceptedCameraPermission(this)) {
            PermissionManager.requestCameraPermission(this);
            return ;
        } else if(!PermissionManager.isAcceptedStoragePermission(this)) {
            PermissionManager.requestStoragePermission(this);
            return ;
        }

        final List<String> ListItems = new ArrayList<>();
        //ListItems.add("사진 촬영");
        ListItems.add("앨범 선택");
        ListItems.add("취소");

        final CharSequence[] items =  ListItems.toArray(new String[ ListItems.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("업로드할 이미지 선택");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int pos) {
                if(pos == 0) {
                    doTakeAlbumAction();
                } else {
                    dialog.dismiss();
                }
            }
        });

        /*
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int pos) {
                if(pos == 0) {
                    doTakePhotoAction();
                } else if(pos == 1) {
                    doTakeAlbumAction();
                } else {
                    dialog.dismiss();
                }
            }
        });
        */
        builder.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK) {
            return;
        }

        switch(requestCode) {
            case REQ_CODE_CROP_FROM_ALBUM :
/*
                File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                JWLog.e("","email :"+mEmail+", path :"+path.getAbsolutePath());
                mImageCaptureUri = Uri.parse(path.getAbsolutePath() + "/" + mEmail +"_pet_profile.jpg");
*/

                try {
                                      /*
                    Bitmap image = new BitmapDrawable(mImageCaptureUri.getPath()).getBitmap();

                    ExifInterface exif = new ExifInterface(mImageCaptureUri.getPath());
                    int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                    int exifDegree = exifOrientationToDegrees(ExifInterface.ORIENTATION_ROTATE_90);
                    image = rotateCropImage(image, exifDegree);

                    saveBitmaptoJpeg(image, Environment.DIRECTORY_DOWNLOADS, mEmail + "_pet_profile");
                    //mImageCaptureUri = Uri.fromFile(new File(mImageCaptureUri.getPath()));
                    mImageCaptureUri = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID+".fileprovider", new File(mImageCaptureUri.getPath()));

                    mAddProfile.setBackground(new BitmapDrawable(mImageCaptureUri.getPath()));
*/

                    try {
                        InputStream inputStream = getContentResolver().openInputStream(mImageCaptureUri);
                        Drawable drawable = Drawable.createFromStream(inputStream, mImageCaptureUri.toString() );
                        mAddProfile.setBackground(drawable);
                        findViewById(R.id.add_image).setVisibility(View.GONE);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                } catch(Exception e) {
                    e.printStackTrace();
                }
                break;
            case REQ_CODE_PICK_FROM_ALBUM:
                mImageCaptureUri = data.getData();
            case REQ_CODE_CAPTURE_IMAGE:
                beginCrop(mImageCaptureUri);
                break;
        }
    }

    private File createFile() throws IOException {
        // 임시파일명 생성
        String tempFilename = mEmail +"_pet_profile";
        // 임시파일 저장용 디렉토리 생성
        File tempDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/walking/");
        if(!tempDir.exists()){
            tempDir.mkdirs();
        }
        // 실제 임시파일을 생성
        File tempFile = new File(tempDir + "/" + tempFilename + ".jpg");
        //File tempFile = File.createTempFile(tempFilename, ".jpg", tempDir);
        if(tempFile.exists()) {
            tempFile.delete();
        }
        return tempFile;
    }

    public int exifOrientationToDegrees(int exifOrientation) {
        if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    public Bitmap rotateCropImage(Bitmap bitmap, int degrees) {
        if(degrees != 0 && bitmap != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2,
                    (float) bitmap.getHeight() / 2);

            try {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), m, true);
                if(bitmap != converted) {
                    bitmap.recycle();
                    bitmap = converted;
                }
            } catch(OutOfMemoryError ex) {
                // 메모리가 부족하여 회전을 시키지 못할 경우 그냥 원본을 반환합니다.
            }
        }
        return bitmap;
    }

    private void doTakePhotoAction() {
    /*
     * 참고 해볼곳
     * http://2009.hfoss.org/Tutorial:Camera_and_Gallery_Demo
     * http://stackoverflow.com/questions/1050297/how-to-get-the-url-of-the-captured-image
     * http://www.damonkohler.com/2009/02/android-recipes.html
     * http://www.firstclown.us/tag/android/
     */


        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // 임시로 사용할 파일의 경로를 생성
        String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        //mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));
        File file = new File(Environment.getExternalStorageDirectory(), url);
        mImageCaptureUri = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID+".fileprovider", file);

        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        // 특정기기에서 사진을 저장못하는 문제가 있어 다음을 주석처리 합니다.
        //intent.putExtra("return-data", true);

        startActivityForResult(intent, REQ_CODE_CAPTURE_IMAGE);
    }

    /**
     * 앨범에서 이미지 가져오기
     */
    private void doTakeAlbumAction()  {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);

        startActivityForResult(intent, REQ_CODE_PICK_FROM_ALBUM);
    }

    public static void saveBitmaptoJpeg(Bitmap bitmap,String folder, String name){
        String ex_storage =Environment.getExternalStorageDirectory().getAbsolutePath();
        // Get Absolute Path in External Sdcard
        String foler_name = "/" + folder + "/";
        String file_name = name + ".jpg";
        String string_path = ex_storage+foler_name;

        File file_path;
        try{
            file_path = new File(string_path);
            if(!file_path.isDirectory()){
                file_path.mkdirs();
            }
            File image = new File(string_path+file_name);

            if(image.exists()) {
                image.delete();
            }
            FileOutputStream out = new FileOutputStream(image.getAbsolutePath());

            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, out);
            out.close();

        }catch(FileNotFoundException exception){
            JWLog.e("FileNotFoundException", exception.getMessage());
        }catch(IOException exception){
            JWLog.e("IOException", exception.getMessage());
        }
    }

    private void beginCrop(Uri source) {
        //Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        /*
        Uri destination = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID+".fileprovider", new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
        */

        File file = null;
        try {
            file = createFile();
            refreshMedia(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mImageCaptureUri = Uri.fromFile(file);
//        afterCrop = FileProvider.getUriForFile(getBaseContext(), BuildConfig.APPLICATION_ID+".provider", file);
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(source, "image/*");  // 뭐를 자를 것인지
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);  // 어디에 저장할 것인지
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        startActivityForResult(intent, REQ_CODE_CROP_FROM_ALBUM);
    }

    private void refreshMedia(File photoFile) {
        MediaScannerConnection.scanFile(this,
                new String[] {photoFile.getAbsolutePath()},
                null,
                new MediaScannerConnection.OnScanCompletedListener(){
                    public void onScanCompleted(String path, Uri uri){

                    }
                });
    }

    private void createPetData() {
        PetData petData = new PetData();
        petData.index = 0;
        petData.mem_email = mEmail;
        petData.petName = mPetName.getText().toString();
        petData.petGender = mPetGender == 0 ? false : true;
        petData.birthDay = mPetBirthDate.getText().toString();
        petData.petSpecies = mPetSpecies.getText().toString();
        petData.petRelation = mPetRelation.getText().toString();
/*
        if(BuildConfig.IS_DEBUG) {
            petData.petName = "은비";
            petData.petGender = true;
            petData.birthDay = "2017년 1월 29일";
            petData.petSpecies = "웰시코기";
            petData.petRelation = "아빠";
        }
        */
        mUserData.pet_list.add(petData);
    }
}
