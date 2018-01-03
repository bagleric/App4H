package com.example.bagle.app4h;

/**
 * Created by bagle on 12/13/2017.
 */

class RecordProject {
    public String id;
    public String name;
    public long startDate;
    public long endDate;

    public RecordProject(String id, String name, long startDate, long endDate, String type) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type;
    }

    public String type;
}
