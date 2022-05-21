package com.example.inai.myActivities;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.View;
import android.widget.*;
import android.os.*;
import android.util.*;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.inai.R;
import com.example.inai.models.ActivityType;
import com.example.inai.models.ApiModel;
import com.example.inai.models.User;
import com.example.inai.models.Event;
import com.example.inai.utils.Api;
import com.example.inai.utils.Constants;
import com.example.inai.utils.ImageUtils;
import com.google.gson.Gson;

import androidx.core.app.ActivityCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateEvents extends AppCompatActivity {

    final static int REQUEST_IMAGE_GET = 1;
    final static String TAG = "CREATE_EVENT";
    private SharedPreferences mPreferences;
    private String userId;

    TextView create_event;
    Button submit;
    TextView event_title;
    EditText title_input;
    TextView event_type;
    Spinner types;
    TextView date;
    TextView time;
    TimePicker start_time;
    TextView to;
    TimePicker end_time;
    TextView location;
    EditText location_input;
    TextView description;
    EditText description_input;
    TextView poster;
    Button upload;
    TextView telegram;
    EditText telegram_input;
    ImageView uploaded_image;
    ApiModel api;
    TextView date_picker;
    DatePickerDialog picker;

    LocalDate eventDate;
    Event event;
    Bitmap posterBit;
    String imageFilename;
    String imageUrl;
    ActivityType type;
    LottieAnimationView progressBar;
    ImageView overlay;

    // exit page
    public void ClosePage(View view) {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.CalendarTheme);
        setContentView(R.layout.create_event);
        api =  Api.getInstance().apiModel;
        mPreferences = getSharedPreferences(Constants.SHARED_PREF_FILE, MODE_PRIVATE);
        userId = getUserFromSharedPref().getId();

        // finding and setting views
        create_event = findViewById(R.id.create_event);
        event_title = findViewById(R.id.event_title);
        event_type = findViewById(R.id.event_type);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        to = findViewById(R.id.to);
        location = findViewById(R.id.location);
        description = findViewById(R.id.description);
        poster = findViewById(R.id.poster);
        telegram = findViewById(R.id.telegram);
        uploaded_image = findViewById(R.id.uploaded_image);
        date_picker = findViewById(R.id.date_picker);
        start_time = findViewById(R.id.start_time);
        end_time = findViewById(R.id.end_time);
        types = findViewById(R.id.types);
        title_input = findViewById(R.id.title_input);
        location_input = findViewById(R.id.location_input);
        description_input = findViewById(R.id.description_input);
        telegram_input = findViewById(R.id.telegram_input);
        progressBar = findViewById(R.id.animation);
        overlay = findViewById(R.id.overlay);

        // удалить загрузку при создании
        progressBar.setVisibility(View.INVISIBLE);

        // установка spinner значений
        String[] items = new String[]{"Спорт", "Экзамены", "Мероприятия"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_text_item, items);
        types.setAdapter(adapter);

        // установка date picker как null так EditText только для показа
        // щелчок по полю откроет компонент календаря для выбора даты
        date_picker.setInputType(InputType.TYPE_NULL);

        // установка времени на 24 часа
        start_time.setIs24HourView(true);
        end_time.setIs24HourView(true);

        // on click handler для кнопки "Готово"
        submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (isCompleted()) {
                        // если все обязательные поля заполнены (т.е. все, кроме телеграммы и изображения)
                        // проверка было ли загружено изображение
                        // загрузка изображения в Cloudinary, если изображение присутствует
                        if (imageFilename != null && !imageFilename.isEmpty()) {
                            overlay.bringToFront();
                            progressBar.bringToFront();
                            overlay.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.VISIBLE);
                            MediaManager.get().upload(imageFilename).unsigned("iybnngkh").callback(new UploadCallback() {
                                @Override
                                public void onStart(String requestId) {}
                                @Override
                                public void onProgress(String requestId, long bytes, long totalBytes) {}
                                @Override
                                public void onSuccess(String requestId, Map resultData) {
                                    // create event only upon successful image upload
                                    imageUrl = resultData.get("secure_url").toString();
                                    createEvent();
                                    Log.i(TAG, "create event return url: " + imageUrl);
                                }

                                @Override
                                public void onError(String requestId, ErrorInfo error) {
                                    Log.i(TAG,"CLOUDINARY UPLOAD ERROR: " + error.getDescription());
                                    Toast.makeText(CreateEvents.this, "Ошибка! Попробуйте еще раз.", Toast.LENGTH_LONG).show();
                                }
                                @Override
                                public void onReschedule(String requestId, ErrorInfo error) {}
                            }).dispatch();
                        } else {
                            // если изображения не загружены, быстрая загрузка
                            overlay.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.VISIBLE);
                            createEvent();
                        }
                    } else {
                        // сообщение при ошибке
                        Toast.makeText(CreateEvents.this, "Заполнните форму полностью!", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    // сообщение при ошибке
                    Toast.makeText(CreateEvents.this, "Ошибка! Попробуйте еще раз.", Toast.LENGTH_LONG).show();
                }
            }
        });

        // on click handler для загрузки изображений
        upload = findViewById(R.id.upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Browse", "CLICKED");
                try {
                    // Проверка есть ли у пользователя разрешения для галереи
                    if (ActivityCompat.checkSelfPermission(CreateEvents.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        // запрос на разрешение (если его нет)
                        Log.i("Browse", "AskingForPermission");
                        ActivityCompat.requestPermissions(CreateEvents.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_IMAGE_GET);
                    } else {
                        // редирект в галерею для загрузки изображения
                        Log.i("Browse", "RedirectinigToGallery");
                        Intent intent = new Intent(); //Intent.ACTION_PICK
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_PICK);
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            Log.i("Browse", "Success");
                            startActivityForResult(intent, REQUEST_IMAGE_GET);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("Browse", e.toString());
                }
            }
        });

        date_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // диалоговое окно date picker
                picker = new DatePickerDialog(CreateEvents.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        eventDate = LocalDate.of(year, monthOfYear+1, dayOfMonth);
                        if (eventDate.isBefore(LocalDate.now(ZoneId.of("Asia/Singapore")))) {
                            Toast.makeText(CreateEvents.this, "Oops, this date has already passed!", Toast.LENGTH_LONG).show();
                            eventDate = null;
                        } else {
                            date_picker.setText(String.format("%d / %d / %d", dayOfMonth, monthOfYear + 1, year));
                            date_picker.setTextAppearance(R.style.Login_Body);
                        }
                    }
                }, year, month, day);
                picker.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK) {
            try {
                // просмотр загруженного изображения в image view
                posterBit = MediaStore.Images.Media.getBitmap(CreateEvents.this.getContentResolver(), data.getData());
                uploaded_image.setImageBitmap(posterBit);
                // сохранить в Cloudinary
                imageFilename = ImageUtils.getImageFilePath(this, data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void createEvent() {
        // создание объекта event
        LocalTime startTime = LocalTime.of(start_time.getHour(), start_time.getMinute());
        LocalTime endTime = LocalTime.of(end_time.getHour(), end_time.getMinute());
        if (!startTime.isBefore(endTime)) {
            // время конца < время начала
            Toast.makeText(CreateEvents.this, "Упс, кажется, что ваше время начала еще до вашего времени окончания!", Toast.LENGTH_LONG).show();
        } else if (LocalDate.now(ZoneId.of("Asia/Singapore")).isEqual(eventDate) && startTime.isBefore(LocalTime.now(ZoneId.of("Asia/Singapore")))) {
            // проверка не прошло ли уже время начала
            Toast.makeText(CreateEvents.this, "Упс, похоже, ваше время начала уже прошло!", Toast.LENGTH_LONG).show();
        } else {
            type = ActivityType.valueOf(((types.getSelectedItem().toString()).toUpperCase()).replace(" ","_"));
            event = new Event(title_input.getText().toString().trim(), eventDate.toString(), startTime.toString(), endTime.toString(),
                    location_input.getText().toString().trim(), description_input.getText().toString().trim(),
                    telegram_input.getText().toString().trim(), type, imageUrl, userId);

            // API
            Call<Event> call = api.createEvent(event);
            call.enqueue(new Callback<Event>() {
                @Override
                public void onResponse(Call<Event> call, Response<Event> response) {
                    overlay.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    if (!response.isSuccessful()) {
                        Toast.makeText(CreateEvents.this, R.string.backend_error, Toast.LENGTH_LONG).show();
                    } else {
                        // редирект к страницы создания мероприятий в случае успешного добавления мероприятия
                        Toast.makeText(CreateEvents.this, "Мероприятие создано!", Toast.LENGTH_LONG).show();
                        finish();
                        Log.i(TAG, "retrieved event id: " + response.body().getId());
                    }
                }

                @Override
                public void onFailure(Call<Event> call, Throwable t) {
                    overlay.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    t.printStackTrace();
                    Toast.makeText(CreateEvents.this, R.string.connection_error, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public boolean editTextIsEmpty(EditText editTextField) {
        return editTextField.getText().toString().trim().isEmpty();
    }

    public boolean isCompleted() {
        // telegram и изображения необзятальны
        // дефолт для начала и конца времени
        return !editTextIsEmpty(title_input) && !editTextIsEmpty(location_input)
                && !editTextIsEmpty(description_input)
                && eventDate != null;
    }

    private User getUserFromSharedPref() {
        Gson gson = new Gson();
        String json = mPreferences.getString(Constants.USER_KEY, null);

        if (json == null) {
            Log.i(TAG, "is null");
            // перенаправление на страницу входа в систему, если информация о пользователе не сохранена
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            finish();

            return null;
        }
        return gson.fromJson(json, User.class);
    }
}
