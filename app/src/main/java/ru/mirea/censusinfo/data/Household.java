package ru.mirea.censusinfo.data;

import com.google.firebase.Timestamp;

public class Household {
    private String id;
    private String address;
    private String region;
    private String censusTakerId;
    private Timestamp createdAt;

    public Household() {}

    public Household(String id, String address, String region,
                     String censusTakerId, Timestamp createdAt) {
        this.id = id;
        this.address = address;
        this.region = region;
        this.censusTakerId = censusTakerId;
        this.createdAt = createdAt;
    }

    public String     getId()           { return id; }
    public String     getAddress()      { return address; }
    public String     getRegion()       { return region; }
    public String     getCensusTakerId(){ return censusTakerId; }
    public Timestamp  getCreatedAt()    { return createdAt; }
    /* getters / setters */
}
