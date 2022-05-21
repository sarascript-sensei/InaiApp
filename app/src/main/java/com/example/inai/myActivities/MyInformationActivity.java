package com.example.inai.myActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inai.R;
import com.example.inai.models.ApiModel;
import com.example.inai.models.User;
import com.example.inai.utils.Api;
import com.example.inai.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyInformationActivity extends AppCompatActivity {
    private boolean disabled = true;
    private boolean changed = false;

    Button edit;
    Button logout;
    EditText nameInput;
    TextView emailInput;
    TextView idInput;
    CharSequence name_CS;
    CharSequence email_CS;
    CharSequence id_CS;

    User user;
    User editedUser;
    private SharedPreferences mPreferences;
    private final String TAG = "MyInformation";
    ApiModel api;

    String Name;
    String Email;
    String StudentId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.inai.R.layout.activity_my_information);
        api = Api.getInstance().apiModel;

        Log.d(TAG, "onCreate: Created");
        edit = findViewById(R.id.edit);
        logout = findViewById(R.id.logout);
        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        idInput = findViewById(R.id.idInput);

        editedUser = new User("Place holder", "placeholder@mail.com", "1000000");


        Gson gson = new Gson();
        mPreferences = getSharedPreferences(Constants.SHARED_PREF_FILE, MODE_PRIVATE);
        String json = mPreferences.getString(Constants.USER_KEY, null);
        if (json == null) {
            Log.i(TAG, "is null");
            Intent intent = new Intent(MyInformationActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        } else {
            Log.i(TAG, "json");
            user = gson.fromJson(json, User.class);

            Name = user.getName();
            Email = user.getEmail();
            StudentId = user.getStudentId();

            idInput.setText(StudentId, TextView.BufferType.EDITABLE);
            emailInput.setText(Email, TextView.BufferType.EDITABLE);
            nameInput.setText(Name, TextView.BufferType.EDITABLE);;

            emailInput.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Toast.makeText(MyInformationActivity.this, "Это поле нельзя редактировать", Toast.LENGTH_SHORT).show();
                }
            });

            idInput.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Toast.makeText(MyInformationActivity.this, "Это поле нельзя редактировать", Toast.LENGTH_SHORT).show();
                }
            });


            nameInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    name_CS = s;
                }
            });

            emailInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    email_CS = s;
                }
            });

            idInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    id_CS = s;
                }
            });
        }

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor preferencesEditor = mPreferences.edit();
                preferencesEditor.clear();
                preferencesEditor.commit();
                try {
                    FirebaseAuth.getInstance().signOut();
                } catch (Exception e) {}

                Intent intent = new Intent(MyInformationActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editedUser.setName(nameInput.getText().toString());
                editedUser.setStudentId(idInput.getText().toString());
                Log.d(TAG, "onClick: " + editedUser.getStudentId());
                editedUser.setEmail(emailInput.getText().toString());
                editedUser.setId(user.getId());
                editedUser.setPermission(user.getPermission());
                editedUser.setEvents(user.getEvents());
                editUser();
            }
        });
    }

    private void editUser() {
        Call<User> call = api.editUser(editedUser);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(MyInformationActivity.this, R.string.backend_error,
                            Toast.LENGTH_LONG).show();

                    nameInput.setText(user.getName());
                    idInput.setText(user.getStudentId());
                    emailInput.setText(user.getEmail());
                } else {
                    Gson gson = new Gson();
                    SharedPreferences.Editor preferencesEditor = mPreferences.edit();
                    preferencesEditor.remove(Constants.USER_KEY);
                    preferencesEditor.putString(Constants.USER_KEY, gson.toJson(editedUser));
                    preferencesEditor.apply();
                    Toast.makeText(MyInformationActivity.this, "Обновлено !",
                            Toast.LENGTH_LONG).show();

                    nameInput.setText(editedUser.getName());
                    idInput.setText(editedUser.getStudentId());
                    emailInput.setText(editedUser.getEmail());
                    
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(MyInformationActivity.this, R.string.connection_error,
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}