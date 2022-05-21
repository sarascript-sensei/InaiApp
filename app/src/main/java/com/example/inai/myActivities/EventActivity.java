package com.example.inai.myActivities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.airbnb.lottie.LottieAnimationView;
import com.example.inai.R;
import com.example.inai.models.ApiModel;
import com.example.inai.models.Event;
import com.example.inai.models.User;
import com.example.inai.utils.Api;
import com.example.inai.utils.Constants;
import com.example.inai.utils.DateTimeUtils;
import com.example.inai.utils.NetworkImage;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.util.HashMap;

import static com.example.inai.utils.Constants.SELECTED_EVENT_KEY;


public class EventActivity extends MenuActivity {

    private SharedPreferences mPreferences;
    private User user;
    private Event event;
    private String eventId;
    private static final String TAG = "EVENT";
    ApiModel api;
    private String creator_email;
    private User creator;

    Button signUpButton;
    Button joinTelegramGroupButton;
    TextView location;
    TextView timeDate;
    TextView eventHeader;
    ImageView eventPoster;
    TextView mainHeader;
    TextView description;
    TextView clashText;
    LinearLayout event_activity_linear_layout;
    LottieAnimationView progressBar;
    View divider;

    int permission = 1; // разрешение для пользователя

    void backToFindEvents() {
        setTheme(R.style.CalendarTheme);
        Toast.makeText(this, "Мероприятие недоступно!", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_activity);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        divider = findViewById(R.id.divider);

        mPreferences = getSharedPreferences(Constants.SHARED_PREF_FILE, MODE_PRIVATE);
        api =  Api.getInstance().apiModel;

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            backToFindEvents();
            Toast.makeText(this, "Значения пусты", Toast.LENGTH_LONG).show();
        } else {
            eventId = extras.getString(SELECTED_EVENT_KEY);
            if (eventId == null) {
                backToFindEvents();
                Toast.makeText(this, "Значения не пусты, но event id null", Toast.LENGTH_LONG).show();
            }
        }

        location = findViewById(R.id.location);
        timeDate = findViewById(R.id.time_date);
        eventHeader = findViewById(R.id.event_header);
        eventPoster = findViewById(R.id.athletics_poster);
        description = findViewById(R.id.post_body);
        clashText = findViewById(R.id.warning1);
        event_activity_linear_layout = findViewById(R.id.event_activity_linear_layout);
        joinTelegramGroupButton = findViewById(R.id.join_telegram_group_button);
        signUpButton = findViewById(R.id.sign_up_button);

        user = getUserFromSharedPref();
        if (user != null && user.getEvents().contains(eventId)) {
            setButtonAppearanceCancel();
        }

        fetchEventOnCreate();

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> userAndEvent = new HashMap<>();
                userAndEvent.put("user_id", user.getId());
                userAndEvent.put("event_id", eventId);

                final boolean alreadyGoing = user.getEvents().contains(eventId);
                userAndEvent.put("sign_up", !alreadyGoing);
                signUpOnClick(userAndEvent, alreadyGoing);
            }
        });

        joinTelegramGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: OnClick started ");
                if (event.getTelegram() == null || event.getTelegram().trim().equals("")) {
                    Log.d(TAG, "onClick: getTelegram is null");
                    String creatorId = event.getCreatorId();
                    String creatorEmail = getCreatorEmail(creatorId);
                    //Log.d(TAG, "onClick: clipboard gonna crash");
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Event Organiser Email", creatorEmail);
                    clipboard.setPrimaryClip(clip);
                    //Log.d(TAG, "onClick: SIKE it didnt ");
                    Toast.makeText(EventActivity.this, "Email организатора скопирован"
                    , Toast.LENGTH_LONG).show();

                }
                else {
                    Log.d(TAG, "onClick: the else loop started");
                    String url = event.getTelegram();
                    Log.d(TAG, "onClick:  gets tele");
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    Log.d(TAG, "onClick: parse works");
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    Log.d(TAG, "onClick: " + (url==""));

                    startActivity(intent);
                }
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventActivity.this, CalendarActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
            }
        });

    }

    private String getCreatorEmail(String creatorId) {
        Call<User> call = api.getCreatorInfo(creatorId);


        call.enqueue(new Callback<User>(){
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(EventActivity.this, R.string.backend_error, Toast.LENGTH_LONG).show();
                }
                else {
                    creator = response.body();
                    if (creator == null) {
                        Toast.makeText(EventActivity.this, "Организатор не найден", Toast.LENGTH_LONG).show();
                    }
                    else {
                        creator_email = creator.getEmail();
                    }
                }

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(EventActivity.this, R.string.connection_error, Toast.LENGTH_LONG).show();
            }
        });
        return(creator_email);
    }

    private User getUserFromSharedPref() {
        Gson gson = new Gson();
        String json = mPreferences.getString(Constants.USER_KEY, null);

        if (json == null) {
            Log.i(TAG, "is null");
            Intent intent = new Intent(EventActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            finish();

            return null;
        }
        return gson.fromJson(json, User.class);
    }

    private void fetchEventOnCreate() {
        Log.i("Fetch", "Fast");
        progressBar.setVisibility(View.VISIBLE);

        Call<Event> call = api.getEvent(eventId, user.getId());
        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                Log.i("Fetch", "Faster");
                progressBar.setVisibility(View.INVISIBLE);
                divider.setVisibility(View.VISIBLE);
                if (!response.isSuccessful()) {
                    Toast.makeText(EventActivity.this, R.string.backend_error, Toast.LENGTH_LONG).show();
                } else {
                    event = response.body();
                    signUpButton.setVisibility(View.VISIBLE);
                    joinTelegramGroupButton.setVisibility(View.VISIBLE);

                    if (event == null) {
                        backToFindEvents();
                        clashText.setVisibility(View.GONE);
                    } else {
                        if (event.getTelegram() == null || event.getTelegram().isEmpty()) {
//                            joinTelegramGroupButton.setVisibility(View.INVISIBLE);
                            joinTelegramGroupButton.setText("Связаться с организатором");
                        }
                        String dateString = DateTimeUtils.getDayOfWeek(event.getDate()) + ", " + DateTimeUtils.formatDate(event.getDate()) + ", "
                                + DateTimeUtils.formatTime12H(event.getStartTime()) + " - " + DateTimeUtils.formatTime12H(event.getEndTime());
                        eventHeader.setText(event.getTitle());
                        location.setText(event.getLocation());
                        timeDate.setText(dateString);
                        description.setText(event.getDescription());
                        clashText.setVisibility(View.VISIBLE);
                        clashText.setText(event.getClashString());
                        Log.i(TAG, "event activity " + user.getEvents());
                        if (event.getImageUrl() == null || event.getImageUrl().trim().isEmpty()) {
                            eventPoster.setImageResource(R.drawable.poster_placeholder1);
                        } else {
                            try {
                                new NetworkImage.NetworkImageBuilder().setImageView(eventPoster).build().execute(event.getImageUrl());
                            } catch (Exception e) {
                                Log.e(TAG, "Невозможно загрузить картинку мероприятия");
                                eventPoster.setImageResource(R.drawable.poster_placeholder1);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                t.printStackTrace();
                Toast.makeText(EventActivity.this, R.string.connection_error, Toast.LENGTH_LONG).show();
                ;
            }
        });
    }

    private void signUpOnClick(HashMap<String, Object> userAndEvent, final boolean alreadyGoing) {
        Call<HashMap<String, Object>> call = api.signUp(userAndEvent);

        call.enqueue(new Callback<HashMap<String,Object>>() {
            @Override
            public void onResponse(Call<HashMap<String, Object>> call, Response<HashMap<String, Object>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(EventActivity.this, R.string.backend_error, Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.INVISIBLE);
                } else {
                    if (response.body() != null) {
                        progressBar.setVisibility(View.INVISIBLE);
                        if (alreadyGoing) {
                            AlertDialog.Builder confirmCancel = new AlertDialog.Builder(EventActivity.this, R.style.AlertDialogCustom);
                            confirmCancel.setTitle("Отмените участие в " + eventHeader.getText() + " ?");
                            confirmCancel.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast toast_success = Toast.makeText(EventActivity.this, R.string.remove_attendance_success_toast, Toast.LENGTH_LONG);
                                    toast_success.setGravity(Gravity.CENTER_VERTICAL, 0,0 );
                                    toast_success.show();

                                    user.cancelAttendance(eventId);
                                    event.removeAttendee(user.getId());
                                    setButtonAppearanceSignUp();
                                    updateUserSharedPref();
                                }
                            });
                            confirmCancel.setNegativeButton(
                                    "Нет",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog alert = confirmCancel.create();
                            alert.show();
                        } else {
                            Toast toast_success = Toast.makeText(EventActivity.this, R.string.sign_up_success_toast, Toast.LENGTH_LONG);
                            toast_success.setGravity(Gravity.CENTER_VERTICAL, 0,0 );
                            toast_success.show();

                            user.signUp(eventId);
                            event.addAttendee(user.getId());
                            // Отменить участие
                            setButtonAppearanceCancel();
                            updateUserSharedPref();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, Object>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(EventActivity.this, R.string.connection_error, Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void setButtonAppearanceCancel() {
        signUpButton.setTextAppearance(R.style.Cancel_Button);
        signUpButton.setBackgroundResource(R.drawable.cancel_button_stroke);
        signUpButton.setText("Отменить участие");
    }

    private void setButtonAppearanceSignUp() {
        signUpButton.setText("Стать участником");
        signUpButton.setTextAppearance(R.style.Primary_Button);
        signUpButton.setBackgroundResource(R.drawable.primary_button);
    }

    private void updateUserSharedPref() {
        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        Gson gson = new Gson();
        preferencesEditor.putString(Constants.USER_KEY, gson.toJson(user));
        preferencesEditor.apply();
    }

}
