package com.example.bagle.app4h;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by bagle on 12/1/2017.
 */

@IgnoreExtraProperties
public class RecordGoal {

    public String description;
    public String toDos;
    public long deadline;
    public String id;

    public RecordGoal(String description, String toDos, long deadline, String id) {
        this.description = description;
        this.toDos = toDos;
        this.deadline = deadline;
        this.id = id;
    }
}

