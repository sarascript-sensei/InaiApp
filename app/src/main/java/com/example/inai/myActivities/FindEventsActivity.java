package com.example.inai.myActivities;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.inai.R;
import com.example.inai.models.ActivityType;
import com.example.inai.models.ApiModel;
import com.example.inai.models.Event;
import com.example.inai.utils.Api;
import com.example.inai.utils.Constants;
import com.example.inai.adapters.EventAdapter;
import com.google.gson.Gson;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class FindEventsActivity extends MenuActivity {

    final static String TAG = "FIND EVENTS";
    Event emptyEvent = new Event("Загрузка...",LocalDate.of(2020, 11, 29).toString(), LocalTime.of(9, 0).toString(), LocalTime.of(12, 0).toString(), "", "", "", null,""); ;
    ArrayList<Event> events = new ArrayList<>();
    ArrayList<Event> fifthRowEvents = new ArrayList<>();
    ArrayList<Event> studentLifeEvents = new ArrayList<>();
    ArrayList<Event> industryTalkEvents = new ArrayList<>();
    ApiModel api;
    CardView fifth_row_events_card_view;
    CardView industry_talks_card_view;
    CardView student_life_card_view;
    RecyclerView fr_cluster_recyclerView,it_cluster_recyclerView,sl_cluster_recyclerView;
    EventAdapter fr_adapter, it_adapter, sl_adapter;
    DisplayMetrics displayMetrics;
    TextView fr_button, sl_button, it_button;
    TextView seeAllfr, seeAllsl, seeAllit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_events_activity);
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        api = Api.getInstance().apiModel;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        fifth_row_events_card_view = findViewById(R.id.fifth_row_events_card_view);
        TextView header = fifth_row_events_card_view.findViewById(R.id.cluster_header);
        header.setText(R.string.fifth_row_activities);
        fr_cluster_recyclerView = fifth_row_events_card_view.findViewById(R.id.horizontal_recycler_view);
        seeAllfr = fifth_row_events_card_view.findViewById(R.id.see_all_button);

        industry_talks_card_view = findViewById(R.id.industry_talks_card_view);
        TextView header1 = industry_talks_card_view.findViewById(R.id.cluster_header);
        header1.setText(R.string.industry_talks);
        seeAllit = industry_talks_card_view.findViewById(R.id.see_all_button);
        it_cluster_recyclerView = industry_talks_card_view.findViewById(R.id.horizontal_recycler_view);

        student_life_card_view = findViewById(R.id.student_life_card_view);
        TextView header2 = student_life_card_view.findViewById(R.id.cluster_header);
        header2.setText(R.string.student_life);
        seeAllsl = student_life_card_view.findViewById(R.id.see_all_button);
        sl_cluster_recyclerView = student_life_card_view.findViewById(R.id.horizontal_recycler_view);

        for (int i = 0; i < 6; i++) {
            fifthRowEvents.add(emptyEvent);
            industryTalkEvents.add(emptyEvent);
            studentLifeEvents.add(emptyEvent);
        }
        fr_adapter = EventAdapter.singleTypeEventAdapter(fifthRowEvents, ActivityType.СПОРТ, this);
        fr_cluster_recyclerView.setAdapter(fr_adapter);
        it_adapter = EventAdapter.singleTypeEventAdapter(industryTalkEvents, ActivityType.ЭКЗАМЕНЫ, this);
        it_cluster_recyclerView.setAdapter(it_adapter);
        sl_adapter = EventAdapter.singleTypeEventAdapter(studentLifeEvents, ActivityType.МЕРОПРИЯТИЯ, this);
        sl_cluster_recyclerView.setAdapter(sl_adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        retrieveEvents();
    }
    
    public void redirectToSeeAll(View view) {

        fr_button = findViewById(R.id.fifth_row_events_card_view).findViewById(R.id.see_all_button);
        sl_button = findViewById(R.id.student_life_card_view).findViewById(R.id.see_all_button);
        it_button = findViewById(R.id.industry_talks_card_view).findViewById(R.id.see_all_button);

        Gson gson = new Gson();

        if (fr_button.equals(view)) { // if its button1
            Intent intent1 = new Intent(FindEventsActivity.this, SeeAllActivity.class);
            String jsonString = gson.toJson(fifthRowEvents);
            intent1.putExtra("EventList", jsonString);
            startActivity(intent1);

        } else if (sl_button.equals(view)) {
            Intent intent2 = new Intent(FindEventsActivity.this, SeeAllActivity.class);
            String jsonString = gson.toJson(studentLifeEvents);
            intent2.putExtra("EventList", jsonString);
            startActivity(intent2);
        } else if (it_button.equals(view)) {
            Intent intent3 = new Intent(FindEventsActivity.this, SeeAllActivity.class);
            String jsonString = gson.toJson(industryTalkEvents);
            intent3.putExtra("EventList", jsonString);
            startActivity(intent3);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem search_icon = menu.findItem(R.id.search_events);
        search_icon.setVisible(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.search_events){
            Gson gson = new Gson();
            Intent intent = new Intent(FindEventsActivity.this, SearchActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            String jsonString = gson.toJson(events);
            intent.putExtra(Constants.RETRIEVED_EVENTS, jsonString);
            intent.putExtra(Constants.PAGE_TITLE, "Найти все мероприятия");
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void retrieveEvents() {
        Call<ArrayList<Event>> call = api.getAllEvents();
        call.enqueue(new Callback<ArrayList<Event>>() {
            @Override
            public void onResponse(Call<ArrayList<Event>> call, Response<ArrayList<Event>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(FindEventsActivity.this, R.string.backend_error, Toast.LENGTH_LONG).show();
                } else {
                    boolean refreshCards = false;
//                    if (response.body().size() == 0) {
//                        noEvents.setVisibility(View.VISIBLE);
//
//                    }
                    ArrayList<String> eventIds = new ArrayList<>();
                    for (Event e: events) {
                        eventIds.add(e.getId());
                    }
                    for (Event e: response.body()) {
                        if (!eventIds.contains(e.getId())) {
                            refreshCards = true;
                        }
                    }
                    if (refreshCards) {
                        events = response.body();
                        fr_adapter.refreshSingleTypeEvents(events, ActivityType.СПОРТ);
                        it_adapter.refreshSingleTypeEvents(events, ActivityType.ЭКЗАМЕНЫ);
                        sl_adapter.refreshSingleTypeEvents(events, ActivityType.МЕРОПРИЯТИЯ);
                    }
                    Log.i(TAG, "find events " +events.toString());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Event>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(FindEventsActivity.this, R.string.connection_error, Toast.LENGTH_LONG).show();
            }
        });
    }
}
