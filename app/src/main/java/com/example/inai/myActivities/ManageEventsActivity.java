package com.example.inai.myActivities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.inai.R;
import com.example.inai.models.Event;
import com.example.inai.models.User;
import com.example.inai.utils.Api;
import com.example.inai.utils.Constants;
import com.example.inai.adapters.EventAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.inai.models.ApiModel;
import com.google.gson.Gson;

public class ManageEventsActivity extends MenuActivity {

    private static final String TAG = "MANAGE EVENTS";
    FloatingActionButton fab_add;
    private ApiModel api;
    private SharedPreferences mPreferences;
    private User user;

    final List<Event> organisedEvents = new ArrayList<>();
    RecyclerView recyclerView;
    EventAdapter manageEventAdapter;
    LottieAnimationView progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_event_activity);
        api = Api.getInstance().apiModel;
        mPreferences = getSharedPreferences(Constants.SHARED_PREF_FILE, MODE_PRIVATE);
        user = getUserFromSharedPref();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        fab_add = findViewById(R.id.fab_add);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageEventsActivity.this, CreateEvents.class);
                startActivity(intent);
            }
        });

        progressBar = findViewById(R.id.progressBar);

        recyclerView = findViewById(R.id.manageEventRecyclerView);
        RecyclerView.LayoutManager pLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(pLayoutManager);
        manageEventAdapter = EventAdapter.manageEventAdapter(organisedEvents, "1004610", ManageEventsActivity.this);
        recyclerView.setAdapter(manageEventAdapter);
        recyclerView.setVisibility(View.INVISIBLE);


        Log.i("Event", "manage events " + String.valueOf(organisedEvents));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Call<ArrayList<Event>> call = api.getOrganisedEvents(user.getId());
        call.enqueue(new Callback<ArrayList<Event>>() {
            @Override
            public void onResponse(Call<ArrayList<Event>> call, Response<ArrayList<Event>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(ManageEventsActivity.this, R.string.backend_error, Toast.LENGTH_LONG).show();
                } else {
                    if (response.body() == null) {
                        progressBar.setVisibility(View.INVISIBLE);
                        redirectToFindEvents();
                        Toast.makeText(ManageEventsActivity.this, "Кажется, у вас нет разрешений", Toast.LENGTH_LONG).show();
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
                        manageEventAdapter.setShouldStopLoading(true);
                        if (response.body().size() == 0) {
                            manageEventAdapter.refreshEvents(new ArrayList<Event>());
                        } else {
                            boolean refreshCards = false;
                            ArrayList<String> eventIds = new ArrayList<>();
                            for (Event e: organisedEvents) {
                                eventIds.add(e.getId());
                            }
                            for (Event e: response.body()) {
                                if (!eventIds.contains(e.getId())) {
                                    refreshCards = true;
                                    organisedEvents.add(e);
                                }
                            }
                            if (!organisedEvents.isEmpty()) {
                                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(ManageEventsActivity.this, DividerItemDecoration.VERTICAL);
                                recyclerView.addItemDecoration(dividerItemDecoration);
                            }
                            if (refreshCards) {
                                manageEventAdapter.setShouldStopLoading(true);
                                manageEventAdapter.refreshEvents(organisedEvents);
                            }
                        }

                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Event>> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                t.printStackTrace();
                Toast.makeText(ManageEventsActivity.this, R.string.connection_error, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void redirectToFindEvents() {
        Intent intentToParticipantsActivity = new Intent(ManageEventsActivity.this, FindEventsActivity.class);
        intentToParticipantsActivity.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intentToParticipantsActivity);
        finish();
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