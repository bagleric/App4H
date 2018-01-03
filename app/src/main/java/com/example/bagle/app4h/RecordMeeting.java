package com.example.bagle.app4h;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by bagle on 12/4/2017.
 */
@IgnoreExtraProperties
class RecordMeeting {
    public String description;
    public long meetingDate;
    public String id;
    public RecordMeeting(String description, long meetingDate, String id) {
        this.description = description;
        this.meetingDate = meetingDate;
        this.id = id;
    }
}
