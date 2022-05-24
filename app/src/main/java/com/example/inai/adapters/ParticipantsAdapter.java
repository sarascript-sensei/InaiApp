package com.example.inai.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inai.R;
import com.example.inai.models.User;

import java.util.List;

public class ParticipantsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<User> participantsList;
    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_PARTICIPANT = 1;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, studentId, email;

        public MyViewHolder(View view){
            super(view);
            name = view.findViewById(R.id.participant_name);
            studentId = view.findViewById(R.id.participant_id);
            email = view.findViewById(R.id.participant_email);
        }
    }

    public class EmptyViewHolder extends RecyclerView.ViewHolder {
        TextView placeholderText;
        ImageView placeholderImage;
        public EmptyViewHolder(@NonNull View itemView) {
            super(itemView);
            placeholderText = itemView.findViewById(R.id.placeholder_text);
            placeholderImage = itemView.findViewById(R.id.placeholder_image);
        }
    }

    public ParticipantsAdapter(List<User> participantsList) {
        this.participantsList = participantsList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        switch(viewType){
            case VIEW_TYPE_PARTICIPANT:
                View calendarView = layoutInflater.inflate(R.layout.participants_card, parent, false);
                viewHolder = new ParticipantsAdapter.MyViewHolder(calendarView);
                break;
            default:
                View emptyView = layoutInflater.inflate(R.layout.search_placeholder, parent, false);
                viewHolder = new ParticipantsAdapter.EmptyViewHolder(emptyView);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch(holder.getItemViewType()){
            case VIEW_TYPE_PARTICIPANT:
                ParticipantsAdapter.MyViewHolder participantViewholder = (ParticipantsAdapter.MyViewHolder) holder;
                setParticipantDetails(participantViewholder, position);
                break;
            default:
                ParticipantsAdapter.EmptyViewHolder emptyViewHolder = (ParticipantsAdapter.EmptyViewHolder) holder;
                setEmptyEventDetails(emptyViewHolder, position);
                break;
        }

    }

    private void setParticipantDetails(ParticipantsAdapter.MyViewHolder vh, int position) {
        User participant = participantsList.get(position);
        vh.name.setText(participant.getName());
        vh.studentId.setText(participant.getStudentId());
        vh.email.setText(participant.getEmail());
    }

    private void setEmptyEventDetails (ParticipantsAdapter.EmptyViewHolder vh, int position){
        vh.placeholderText.setText("Из участников только вы (пока что)!");
        vh.placeholderImage.setImageResource(R.drawable.placeholder_participants);
    }

    //возвращает общее количество элементов в списке.
    @Override
    public int getItemCount() {
        if(participantsList.size() == 0){
            return 1;
        }
        return participantsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(participantsList.size() == 0){
            return VIEW_TYPE_EMPTY;
        }
        return VIEW_TYPE_PARTICIPANT;
    }
}
