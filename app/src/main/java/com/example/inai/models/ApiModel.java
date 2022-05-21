package com.example.inai.models;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface ApiModel {

    @POST("user")
    Call<User> createUser(@Body User user);

    @GET("user")
    Call<User> getUserCred(@Query("email") String email );

    @GET("event")
    Call<Event> getEvent(@Query("event_id") String eventId, @Query("user_id") String userId);

    @POST("event")
    Call<Event> createEvent(@Body Event event);

    @PUT("event")
    Call<HashMap<String, Object>> signUp(@Body HashMap<String, Object> userAndEvent);

    @GET("events")
    Call<ArrayList<Event>> getAllEvents();

    @PUT("user")
    Call<User> editUser(@Body User user);

    @GET("calendar")
    Call<ArrayList<Event>> getCalendarEvents(@Query("user_id") String userId, @Query("start_date") String startDate, @Query("end_date") String endDate);

    @GET("organised")
    Call<ArrayList<Event>> getOrganisedEvents(@Query("user_id") String userId);

    @GET("creatorinfo")
    Call<User> getCreatorInfo(@Query("user_id") String userId);

    @GET("participants")
    Call<ArrayList<User>> getParticipants(@Query("event_id") String eventId);

}
