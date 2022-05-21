package com.example.inai.adapters;

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inai.R;
import com.example.inai.models.ActivityType;
import com.example.inai.models.Event;
import com.example.inai.myActivities.EventActivity;
import com.example.inai.utils.DateTimeUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.example.inai.utils.Constants.CALENDAR_CARD_CONTEXT;
import static com.example.inai.utils.Constants.SELECTED_EVENT_KEY;

public class CalendarAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Event> calendarEvents;
    List<Event> calendarEventsAll;
    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_CALENDAR_EVENT = 1;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView eventTitle, time, location;
        public ImageView indicator;
        public CardView calendarCard;
        public Button calToEventButton;

        public MyViewHolder(View view) {
            super(view);
            CALENDAR_CARD_CONTEXT = view.getContext();
            eventTitle = view.findViewById(R.id.calendar_title);
            time = view.findViewById(R.id.calendar_time);
            location = view.findViewById(R.id.calendar_location);
            indicator = view.findViewById(R.id.indicator);
            calendarCard = view.findViewById(R.id.calendar_card);
            calToEventButton = view.findViewById(R.id.calToEventButton);
        }

    }

    class EmptyViewHolder extends RecyclerView.ViewHolder {
        TextView placeholderText;
        ImageView placeholderImage;
        public EmptyViewHolder(@NonNull View itemView) {
            super(itemView);
            placeholderText = itemView.findViewById(R.id.placeholder_text);
            placeholderImage = itemView.findViewById(R.id.placeholder_image);
        }
    }


    public CalendarAdapter(List<Event> calendarEvents) {
        this.calendarEvents = calendarEvents;
        this.calendarEventsAll = new ArrayList<Event>(calendarEvents);

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        switch(viewType){
            case VIEW_TYPE_CALENDAR_EVENT:
                View calendarView = layoutInflater.from(parent.getContext()).inflate(R.layout.calendar_card, parent, false);
                viewHolder = new MyViewHolder(calendarView);
                break;
            default:
                View emptyView = layoutInflater.inflate(R.layout.search_placeholder, parent, false);
                viewHolder = new EmptyViewHolder(emptyView);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch(holder.getItemViewType()){
            case VIEW_TYPE_CALENDAR_EVENT:
                MyViewHolder calendarViewHolder = (MyViewHolder) holder;
                setCalendarDetails(calendarViewHolder, position);
                break;
            default:
                EmptyViewHolder emptyViewHolder = (EmptyViewHolder) holder;
                setEmptyEventDetails(emptyViewHolder, position);
                break;
        }


    }
    private void setCalendarDetails(CalendarAdapter.MyViewHolder vh, int position){
        final Event calendarEvent = calendarEvents.get(position);
        vh.eventTitle.setText(calendarEvent.getTitle());
        vh.time.setText(DateTimeUtils.formatTime24H(calendarEvent.getStartTime()) + " - " + DateTimeUtils.formatTime24H(calendarEvent.getEndTime()));
        vh.location.setText(calendarEvent.getLocation());


        if (calendarEvent.getType() == ActivityType.СПОРТ) {
            vh.indicator.setBackgroundColor(Color.parseColor("#EAD620"));
            vh.calendarCard.setBackgroundColor(Color.parseColor("#FFFCE3"));
        } else if (calendarEvent.getType() == ActivityType.ЭКЗАМЕНЫ) {
            vh.indicator.setBackgroundColor(Color.parseColor("#81D2AC"));
            vh.calendarCard.setBackgroundColor(Color.parseColor("#EDFFF7"));
        } else if (calendarEvent.getType() == ActivityType.МЕРОПРИЯТИЯ) {
            vh.indicator.setBackgroundColor(Color.parseColor("#81C3D2"));
            vh.calendarCard.setBackgroundColor(Color.parseColor("#EAFBFF"));
        }

        final String CAL_EVENT_CLICK = calendarEvents.get(position).getId();

        vh.calToEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Calendar Adapter", "Button Pressed");
                Intent intent = new Intent(CALENDAR_CARD_CONTEXT, EventActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                Log.d("Calendar Adapter", "onClick " + CAL_EVENT_CLICK);
                intent.putExtra(SELECTED_EVENT_KEY, CAL_EVENT_CLICK);
                CALENDAR_CARD_CONTEXT.startActivity(intent);
            }
        });
    }

    private void setEmptyEventDetails (CalendarAdapter.EmptyViewHolder vh, int position){
        vh.placeholderText.setText("Нет запланированных меропирятий или встреч!");
        vh.placeholderImage.setImageResource(R.drawable.placeholder_calendar);
    }

    @Override
    public int getItemCount() {
        if(calendarEvents.size() == 0){
            return 1;
        }
        return calendarEvents.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(calendarEvents.size() == 0){
            return VIEW_TYPE_EMPTY;
        }
        return VIEW_TYPE_CALENDAR_EVENT;
    }

    public void filterEvents(Calendar date) {
        calendarEvents.clear();
        LocalDate calendarDate = LocalDate.of(date.get(Calendar.YEAR), date.get(Calendar.MONTH) + 1, date.get(Calendar.DATE));

        for (Event e : calendarEventsAll) {
            if (e.getDate().isEqual(calendarDate)) {
                calendarEvents.add(e);
            }
        }
        notifyDataSetChanged();
    }

    public void updateEvents(ArrayList<Event> events) {
        this.calendarEvents = new ArrayList<Event>(events);
        this.calendarEventsAll = new ArrayList<Event>(events);
        notifyDataSetChanged();
    }

}
