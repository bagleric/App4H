package com.example.bagle.app4h;

/**
 * Created by bagle on 12/8/2017.
 */

public class RecordHealth {
    public String id;
    public String species;
    public String condition;
    public String productUsed;
    public String comments;
    public long date;

    public RecordHealth(String species, String id, String condition, String productUsed, String comments, long date) {
        this.species = species;
        this.id = id;
        this.condition = condition;
        this.productUsed = productUsed;
        this.comments = comments;
        this.date = date;
    }
}
