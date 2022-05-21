package com.example.inai.models;

public enum ActivityType {
    СПОРТ,
    ЭКЗАМЕНЫ,
    МЕРОПРИЯТИЯ;

    public static String convertEnumToString(ActivityType activityType) {
        String out = "";
        if (activityType == СПОРТ) {
            out = "Спорт";
        } else if (activityType == ЭКЗАМЕНЫ) {
            out = "Экзамены";
        } else if (activityType == МЕРОПРИЯТИЯ) {
            out = "Мероприятия";
        }
        return out;
    }
}


