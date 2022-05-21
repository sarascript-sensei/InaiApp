package com.example.inai.myActivities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.inai.R;
import com.example.inai.models.ApiModel;
import com.example.inai.models.Event;
import com.example.inai.models.User;
import com.example.inai.utils.Api;
import com.example.inai.adapters.CalendarAdapter;
import com.example.inai.utils.Constants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.model.CalendarEvent;
import devs.mulham.horizontalcalendar.utils.CalendarEventsPredicate;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CalendarActivity extends MenuActivity {

    private static final String TAG = "CALENDAR";
    private ApiModel api;
    private SharedPreferences mPreferences;
    private User user;

    final ArrayList<Event> allUserEvents = new ArrayList<>();
    final ArrayList<Event> userEvents = new ArrayList<>();
    RecyclerView recyclerView;
    CalendarAdapter calendarAdapter;
    LinearLayout calendar_placeholder;

    final Calendar defaultDate = Calendar.getInstance();

    CalendarEvent c1 = new CalendarEvent(Color.parseColor("#ff6160"), "event");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("ALC", "CREATE");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar);
        api = Api.getInstance().apiModel;
        mPreferences = getSharedPreferences(Constants.SHARED_PREF_FILE, MODE_PRIVATE);
        user = getUserFromSharedPref();
        calendar_placeholder = findViewById(R.id.calendar_placeholder);

        recyclerView = findViewById(R.id.calendar_events);

        Bundle test = getIntent().getExtras();
        if (test != null ) {
            String[] date = test.getString("DATE").split("-");
            defaultDate.set(Integer.valueOf(date[0]), Integer.valueOf(date[1]), Integer.valueOf(date[2]));
            Log.i(TAG, "calendar " + test.getString("DATE"));
        }

        // начинается до 1 месяца с сегодняшнего дня
        final Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, -1);

        // кончается после 1 месяца с сегодняшнего дня
        final Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 1);

        LocalDate startLocalDate =  LocalDate.of(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH)+1, startDate.get(Calendar.DATE));
        LocalDate endLocalDate =  LocalDate.of(endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH)+1, endDate.get(Calendar.DATE));

        Call<ArrayList<Event>> call = api.getCalendarEvents(user.getId(), startLocalDate.toString(), endLocalDate.toString());
        call.enqueue(new Callback<ArrayList<Event>>() {
            @Override
            public void onResponse(Call<ArrayList<Event>> call, Response<ArrayList<Event>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(CalendarActivity.this, R.string.backend_error, Toast.LENGTH_LONG).show();
                } else {
                    for (Event e: response.body()) {
                        allUserEvents.add(e);
                        userEvents.add(e);
                    }
                    //Log.i(TAG, "calendar upon retrieving data " + allUserEvents);

                    calendarAdapter = new CalendarAdapter(userEvents); // this array list is the dynamic one we will vary based on date selected
                    RecyclerView.LayoutManager pLayoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(pLayoutManager);
                    recyclerView.setAdapter(calendarAdapter);

                    calendarAdapter.filterEvents(defaultDate);
                    //Log.i(TAG, defaultDate.toString());
                    HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(CalendarActivity.this, R.id.calendarView).range(startDate, endDate)
                            .datesNumberOnScreen(5)
                            .defaultSelectedDate(defaultDate)
                            .addEvents(new CalendarEventsPredicate() {
                                @Override
                                public List<CalendarEvent> events(Calendar date) {
                                    Log.i(TAG, "calendar allUserEvents " + allUserEvents);
                                    final List<CalendarEvent> events = new ArrayList<>();
                                    final LocalDate selectedDay = LocalDate.of(date.get(Calendar.YEAR), date.get(Calendar.MONTH)+1, date.get(Calendar.DATE));
                                    for(Event e: allUserEvents){
                                        Log.i("EventDate", (e.getDate().toString()));
                                        if(e.getDate().isEqual(selectedDay)){
                                            events.add(c1);
                                        }
                                    }
                                    return events;
                                }
                            })
                            .build();
                    horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
                        @Override
                        public void onDateSelected(Calendar date, int position) {
                            calendarAdapter.filterEvents(date);
                            getIntent().putExtra("DATE", "" + date.get(Calendar.YEAR) + "-" + date.get(Calendar.MONTH) + "-" + date.get(Calendar.DATE));
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Event>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(CalendarActivity.this, R.string.connection_error, Toast.LENGTH_LONG).show();
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CalendarActivity.this, SearchActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                LocalDate today = LocalDate.now();
                ArrayList<Event> upcomingEvents = new ArrayList<>();
                for (Event e: allUserEvents) {
                    if (e.getDate().isEqual(today) || e.getDate().isAfter(today)) {
                        upcomingEvents.add(e);
                    }
                }
                Gson gson = new Gson();
                String jsonString = gson.toJson(upcomingEvents);
                intent.putExtra(Constants.RETRIEVED_EVENTS, jsonString);
                intent.putExtra(Constants.PAGE_TITLE, "Найти встречу по душе");
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        user = getUserFromSharedPref();
        for (Event e: allUserEvents) {
            if (!user.getEvents().contains(e.getId())) {
                recreate();
            }
        }
    }

    private User getUserFromSharedPref() {
        Gson gson = new Gson();
        String json = mPreferences.getString(Constants.USER_KEY, null);

        if (json == null) {
            Log.i(TAG, "is null");
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            finish();

            return null;
        }
        return gson.fromJson(json, User.class);
    }


    }
