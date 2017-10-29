package com.friendly.walkingout.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.friendly.walkingout.GlobalConstantID;
import com.friendly.walkingout.R;
import com.friendly.walkingout.dataSet.PetData;
import com.friendly.walkingout.dataSet.PetRelationData;
import com.friendly.walkingout.dataSet.UserData;
import com.friendly.walkingout.firabaseManager.FireBaseNetworkManager;
import com.friendly.walkingout.main.MainActivity;
import com.friendly.walkingout.util.CommonUtil;
import com.friendly.walkingout.util.JWLog;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import stfalcon.universalpickerdialog.UniversalPickerDialog;


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
    public static final int                     REQ_CODE_SELECT_IMAGE = 101;
    public static final int                     REQ_CODE_CROP_IMAGE = 102;

    private String                              mEmail;
    private String                              mPassword;
    private EditText                            mPetName;
    private EditText                            mPetBirthDate;
    private EditText                            mPetSpecies;
    private EditText                            mPetRelation;

    private ImageButton                         mAddProfile;
    private ImageButton                         mMaleCheck;
    private ImageButton                         mFemaleCheck;
    private Button                              mSignUp;

    private static PetData[]                    mPetData;
    private static PetRelationData[]            mRelationData;
    private int                                  mPetGender = -1;
    private Uri                                  mImageCaptureUri;

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

        mPetData = new PetData[]{new PetData(0, "웰시코기"),
                new PetData(1, "말티즈"),
                new PetData(2, "시츄"),
                new PetData(3, "이탈리안 그레이하운드"),
                new PetData(4, "사모예드")};

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

        Intent intent = getIntent();
        mEmail = intent.getStringExtra(GlobalConstantID.SIGN_UP_EMAIL);
        mPassword = intent.getStringExtra(GlobalConstantID.SIGN_UP_PASSWORD);

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
            showPetSpeciesDialog();
        } else if(v == mPetRelation) {
            showPetRelationDialog();
        } else if(v == mSignUp) {
            if(checkEmptyFields()) {
                Toast.makeText(SignUpPetActivity.this, "비어있는 항목을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            } else {
                FireBaseNetworkManager.getInstance(this).createAccount(mEmail, mPassword, new FireBaseNetworkManager.FireBaseNetworkCallback() {
                    @Override
                    public void onCompleted(boolean result, Task<AuthResult> task) {
                        if (result) {
                            Toast.makeText(SignUpPetActivity.this, "계정 만들기 성공", Toast.LENGTH_SHORT).show();

                            // public UserData(String email, String uid, String petName, boolean petGender, String birthDay, String petSpecies, String petRelation) {
                            UserData data = new UserData(mEmail,
                                    task.getResult().getUser().getUid(),
                                    mPetName.getText().toString(),
                                    mPetGender == 0 ? false : true,
                                    mPetBirthDate.getText().toString(),
                                    mPetSpecies.getText().toString(),
                                    mPetRelation.getText().toString());

                            FireBaseNetworkManager.getInstance(getApplicationContext()).createUserData(data, new FireBaseNetworkManager.FireBaseNetworkCallback() {
                                @Override
                                public void onCompleted(boolean result, Task<AuthResult> task) {
                                    if (result) {
                                        Toast.makeText(SignUpPetActivity.this, "유저 데이터 만들기 성공", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(SignUpPetActivity.this, "유저 데이터 만들기 실패", Toast.LENGTH_SHORT).show();
                                    }

                                    Intent intent = new Intent(SignUpPetActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                    startActivity(intent);
                                }
                            });
                        } else {
                            Toast.makeText(SignUpPetActivity.this, "계정 만들기 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
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
                .show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        mPetBirthDate.setText( year + getString(R.string.year) + (monthOfYear+1) + getString(R.string.month) + " " + dayOfMonth +getString(R.string.day));
        Toast.makeText(getApplicationContext(), year + "년 " + (monthOfYear+1) + "월 " + dayOfMonth +"일", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPick(int[] selectedValues, int key) {
        JWLog.e("","selectedValues :"+selectedValues[0]+", key :"+key);
        String data = null;

        if(selectedValues[0] == PICKER_DATA_TYPE_SPECIES) {
            data = mPetData[selectedValues[0]].getSpecies();
            mPetSpecies.setText(data);
        } else if(selectedValues[0] == PICKER_DATA_TYPE_RELATION) {
            data = mRelationData[selectedValues[0]].getName();
            mPetRelation.setText(data);
        }
        Toast.makeText(getApplicationContext(), data, Toast.LENGTH_SHORT).show();
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
        final List<String> ListItems = new ArrayList<>();
        ListItems.add("사진 촬영");
        ListItems.add("앨범 선택");
        ListItems.add("취소");

        final CharSequence[] items =  ListItems.toArray(new String[ ListItems.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("업로드할 이미지 선택");
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
        builder.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK) {
            return;
        }

        switch(requestCode) {
            case REQ_CODE_CROP_IMAGE:  {
                // 크롭이 된 이후의 이미지를 넘겨 받습니다.
                // 이미지뷰에 이미지를 보여준다거나 부가적인 작업 이후에
                // 임시 파일을 삭제합니다.
                final Bundle extras = data.getExtras();

                if(extras != null) {
                    Bitmap photo = extras.getParcelable("data");
                    mAddProfile.setBackground(new BitmapDrawable(photo));
                }

                File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);




                JWLog.e("","path :"+path.getAbsolutePath());
                mImageCaptureUri = Uri.parse(path.getAbsolutePath() + "/pet_profile.jpg");
                JWLog.e("","mImageCaptureUri :"+mImageCaptureUri.getPath());

                saveBitmapToFileCache((Bitmap) extras.getParcelable("data"), mImageCaptureUri.getPath());

//                // 임시 파일 삭제
//                File f = new File(mImageCaptureUri.getPath());
//                if(f.exists())  {
//                    f.delete();
//                }

                FireBaseNetworkManager.getInstance(this).uploadImage(mImageCaptureUri);
                break;
            }
            case REQ_CODE_SELECT_IMAGE:  {
                // 이후의 처리가 카메라와 같으므로 일단  break없이 진행합니다.
                // 실제 코드에서는 좀더 합리적인 방법을 선택하시기 바랍니다.
                mImageCaptureUri = data.getData();
            }
            case REQ_CODE_CAPTURE_IMAGE:  {
                // 이미지를 가져온 이후의 리사이즈할 이미지 크기를 결정합니다.
                // 이후에 이미지 크롭 어플리케이션을 호출하게 됩니다.
/*
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");

                intent.putExtra("outputX", 90);
                intent.putExtra("outputY", 90);
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, REQ_CODE_CROP_IMAGE);
                */




                mImageCaptureUri = data.getData();
                JWLog.e("","mImageCaptureUri :"+mImageCaptureUri.getPath());

                //saveBitmapToFileCache((Bitmap) extras.getParcelable("data"), mImageCaptureUri.getPath());

//                // 임시 파일 삭제
//                File f = new File(mImageCaptureUri.getPath());
//                if(f.exists())  {
//                    f.delete();
//                }

                FireBaseNetworkManager.getInstance(this).uploadImage(mImageCaptureUri);

                break;
            }
        }
    }

    public String getImageNameToUri(Uri data) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        String imgPath = cursor.getString(column_index);
        String imgName = imgPath.substring(imgPath.lastIndexOf("/")+1);

        return imgName;
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
        mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));

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
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);

    }

    private void saveBitmapToFileCache(Bitmap bitmap, String strFilePath) {
        File fileCacheItem = new File(strFilePath);
        OutputStream out = null;

        try {
            fileCacheItem.createNewFile();
            out = new FileOutputStream(fileCacheItem);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
