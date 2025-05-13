package ru.mirea.censusinfo.data;

import com.google.firebase.Timestamp;

public class SupportRequest {
    private String id;          // uid_sec
    private String uid;
    private String message;
    private Timestamp createdAt;

    public SupportRequest() {}
    public SupportRequest(String uid, String msg){
        this.uid = uid;
        this.message = msg;
        this.createdAt = Timestamp.now();
        this.id = uid + "_" + createdAt.getSeconds();
    }

    public String getId() { return id; }
    public String getUid() { return uid; }
    public String getMessage() { return message; }
    public Timestamp getCreatedAt() { return createdAt; }
}
