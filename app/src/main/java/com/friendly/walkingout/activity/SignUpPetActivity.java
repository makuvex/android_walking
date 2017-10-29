package com.friendly.walkingout.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
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

import java.text.SimpleDateFormat;
import java.util.Date;

import stfalcon.universalpickerdialog.UniversalPickerDialog;


/**
 * Created by jungjiwon on 2017. 10. 25..
 */

public class SignUpPetActivity extends BaseActivity implements View.OnFocusChangeListener, DatePickerDialog.OnDateSetListener, UniversalPickerDialog.OnPickListener {

    public static final int                     PICKER_DATA_TYPE_SPECIES = 0;
    public static final int                     PICKER_DATA_TYPE_RELATION = 1;

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
    private int                                 mPetGender = -1;


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

        mPetName.setOnFocusChangeListener(this);
        mPetBirthDate.setOnFocusChangeListener(this);
        mPetSpecies.setOnFocusChangeListener(this);
        mPetRelation.setOnFocusChangeListener(this);

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

}
