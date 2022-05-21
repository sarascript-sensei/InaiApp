package com.example.inai.myActivities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.inai.R;
import com.example.inai.models.ApiModel;
import com.example.inai.models.User;
import com.example.inai.utils.Api;
import com.example.inai.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.inai.utils.Constants.SHARED_PREF_FILE;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SIGN UP";
    public static int FIELDS = 4;
    private FirebaseAuth mAuth;

    private SharedPreferences mPreferences;

    ApiModel api;
    User user;

    int[] fieldIds;
    EditText[] inputFields = new EditText[FIELDS];
    CheckBox pdpa;
    Button submit;

    CharSequence name;
    CharSequence email;
    CharSequence studentId;
    CharSequence password;

    boolean completed = false;
    boolean disabled = true;

    LottieAnimationView animation;
    ImageView overlay;


    private static boolean validateEmail(String emailStr) {
        Pattern generic_email =
                Pattern.compile("^[0-9?A-z0-9?]+(\\.)?[0-9?A-z0-9?]+@[A-z]+\\.[A-z]{3}.?[A-z]{0,3}$", Pattern.CASE_INSENSITIVE);
        Pattern sutd_email = Pattern.compile("^[0-9?A-z0-9?]+(\\.)?[0-9?A-z0-9?]+@[[A-z]*]{0,8}\\.sutd\\.edu\\.sg$", Pattern.CASE_INSENSITIVE);
        Matcher m_generic = generic_email.matcher(emailStr);
        Matcher m_sutd = sutd_email.matcher(emailStr);
        return(m_sutd.find() || m_generic.find());
    }

    private boolean validatePassword(String passStr) {
        Pattern pass_wo_spl = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{6,}$");
        Pattern pass_w_spl = Pattern.compile("(?=^.{6,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$");

        Matcher m_wo_spl = pass_wo_spl.matcher(passStr);
        Matcher m_w_spl = pass_w_spl.matcher(passStr);
        if (m_wo_spl.matches() || m_w_spl.matches() && passStr.length() > 6) {
            return(true);
        }
        else {
            return(false);
        }

    }

    private boolean validateStudentId(String studentIdStr) {
        String regex = "^(AIN\\d\\d\\d\\d\\d)$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(studentIdStr);
        if (m.matches()) {
            return(true);
        }
        else {
            return(false);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkCompleted() {
        Log.i(TAG, "telegramId is " + password + ", " + disabled);
        completed = pdpa.isChecked()
                && name != null && !name.toString().isEmpty()
                && password != null && !password.toString().isEmpty()
                && email != null && !email.toString().isEmpty()
                && studentId != null && !studentId.toString().isEmpty();
        if (disabled && completed) {
            disabled = false;
            submit.setTextAppearance(R.style.Primary_Button);
            submit.setBackgroundResource(R.drawable.primary_button);
        } else if (!disabled && !completed){
            disabled = true;
            submit.setTextAppearance(R.style.Disabled_Button);
            submit.setBackgroundResource(R.drawable.disabled_button);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        Log.d(TAG, "Activity Created");
        api = Api.getInstance().apiModel;
        mAuth = FirebaseAuth.getInstance();
        mPreferences = getSharedPreferences(SHARED_PREF_FILE, Context.MODE_PRIVATE);
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        animation = findViewById(R.id.animation);
        overlay = findViewById(R.id.overlay);

        fieldIds = new int[]{R.id.nameInput, R.id.emailInput, R.id.idInput, R.id.passwordInput};

        for (int i=0; i<FIELDS; i++) {
            inputFields[i] = findViewById(fieldIds[i]);
            inputFields[i].setWidth((int)(width*0.9));
            inputFields[i].setPadding((int)(width*0.05), 0,0,0);
        }

        pdpa = findViewById(R.id.pdpa);
        submit = findViewById(R.id.submit);
        submit.setTextAppearance(R.style.Disabled_Button);
        submit.setBackgroundResource(R.drawable.disabled_button);


        inputFields[0].addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                name = s;
                checkCompleted();
            }
        });

        inputFields[1].addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                email = s;
                checkCompleted();
            }
        });
        inputFields[2].addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                studentId = s;
                checkCompleted();
            }
        });
        inputFields[3].addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                password = s;
                checkCompleted();
            }
        });

        pdpa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCompleted();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlay.setVisibility(View.VISIBLE);
                overlay.bringToFront();
                animation.bringToFront();
                animation.setVisibility(View.VISIBLE);
                if (!disabled) {
                    if (validateEmail(email.toString())) {
                        if (validatePassword(password.toString())) {
                            if (validateStudentId(studentId.toString()))
                                authenticateWithFirebase();
                            else {
                                Toast.makeText(SignUpActivity.this, "Введите студенческое ID правильно!",
                                        Toast.LENGTH_LONG).show();
                                animation.setVisibility(View.INVISIBLE);
                                overlay.setVisibility(View.INVISIBLE);
                            }
                        }
                        else {
                            Toast.makeText(SignUpActivity.this, "Пароль не соответсвует требованиям!",
                                    Toast.LENGTH_LONG).show();
                            animation.setVisibility(View.INVISIBLE);
                            overlay.setVisibility(View.INVISIBLE);
                        }

                    } else {
                        Toast.makeText(SignUpActivity.this, "Неверный формат почты", Toast.LENGTH_LONG).show();
                        animation.setVisibility(View.INVISIBLE);
                        overlay.setVisibility(View.INVISIBLE);
                    }
                } else {
                    Toast.makeText(SignUpActivity.this, "Заполните форму полностью!!", Toast.LENGTH_LONG).show();
                    animation.setVisibility(View.INVISIBLE);
                    overlay.setVisibility(View.INVISIBLE);
                }

            }
        });

    }

    private void authenticateWithFirebase() {
        mAuth.createUserWithEmailAndPassword(email.toString(), password.toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            createUser();
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Ошибка. Хотели бы войти в аккаунт?",
                                    Toast.LENGTH_SHORT).show();
                            animation.setVisibility(View.INVISIBLE);
                            overlay.setVisibility(View.INVISIBLE);
//                            Toast.makeText(SignUpActivity.this, "Oops, this email has already been used. Log in instead!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void createUser() {
        user = new User(name.toString(), email.toString(), studentId.toString());
        Call<User> call = api.createUser(user);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(SignUpActivity.this, R.string.backend_error, Toast.LENGTH_LONG).show();
                } else {
                    if (response.body() != null) {
                        user.setId(response.body().getId());
                        user.setPermission(response.body().getPermission());
                        user.setOrganisedEvents(new ArrayList<String>());
                        onSubmitSuccess();
                    } else {
                        Toast.makeText(SignUpActivity.this, "Студенческое ID уже зарегистрировано. Войдите или введите другой ID", Toast.LENGTH_LONG).show();
                        animation.setVisibility(View.INVISIBLE);
                        overlay.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

                t.printStackTrace();

                Toast.makeText(SignUpActivity.this, R.string.connection_error, Toast.LENGTH_LONG).show();
                animation.setVisibility(View.INVISIBLE);
                overlay.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void onSubmitSuccess() {
        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        Gson gson = new Gson();
        preferencesEditor.putString(Constants.USER_KEY, gson.toJson(user));
        preferencesEditor.apply();

        Intent intent = new Intent(SignUpActivity.this, FindEventsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        finish();
    }


}
