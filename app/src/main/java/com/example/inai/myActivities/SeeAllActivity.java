package com.example.inai.myActivities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inai.R;
import com.example.inai.models.ActivityType;
import com.example.inai.models.Event;
import com.example.inai.adapters.EventAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SeeAllActivity extends AppCompatActivity {

    private List<Event> eventList = new ArrayList<>();
    private RecyclerView recyclerView;
    private EventAdapter seeAllAdapter;
    TextView seeAllHeading;

    final static String placeholderImageUrl = "https://res.cloudinary.com/dyaxu5mb4/image/upload/v1606499824/plent/poster_placeholder1_jgh6vd.png";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.see_all_activity);

        recyclerView = findViewById(R.id.see_all_recycler_view);

        Bundle bundle = getIntent().getExtras();
        String jsonString = bundle.getString("EventList");

        Gson gson = new Gson();
        Type listOfEventType = new TypeToken<List<Event>>() {}.getType();
        eventList = gson.fromJson(jsonString, listOfEventType);

        ActivityType eventType = eventList.get(0).getType();


        seeAllAdapter = EventAdapter.singleTypeEventAdapter(eventList, eventType , this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(seeAllAdapter);

        seeAllHeading = findViewById(R.id.see_all_heading);
        seeAllHeading.setText(ActivityType.convertEnumToString(eventType));


    }

}
