package com.minersglory.mgcore.com.minersglory.mgcore.database;

public interface DatabaseController {

    void initialize();

    void insertPlayer(String uuid, String username, String time);

    boolean hasTime(String uuid);

    void setTime(String time, String uuid);

    String getTime(String uuid);

}
