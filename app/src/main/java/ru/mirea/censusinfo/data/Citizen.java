package ru.mirea.censusinfo.data;

import com.google.firebase.Timestamp;

public class Citizen {

    private String id;
    private String householdId;
    private String fullName;
    private String birthDate;
    private String gender;
    private String education;
    private String employment;
    private java.util.List<String> children;
    private String ownerUid;
    private String address;
    private String maritalStatus;
    private String spouse;
    private String status;
    private Timestamp updatedAt;

    // ───────────────────── геттеры/сеттеры ─────────────────────

    public String getId()                { return id; }
    public void   setId(String id)       { this.id = id; }

    public String getHouseholdId()       { return householdId; }
    public void   setHouseholdId(String householdId) { this.householdId = householdId; }

    private String email;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName()          { return fullName; }
    public void   setFullName(String fullName)       { this.fullName = fullName; }

    public String getBirthDate()         { return birthDate; }
    public void   setBirthDate(String birthDate)     { this.birthDate = birthDate; }

    public String getGender()            { return gender; }
    public void   setGender(String gender)           { this.gender = gender; }

    public String getEducation()         { return education; }
    public void   setEducation(String education)     { this.education = education; }

    public String getEmployment()        { return employment; }
    public void   setEmployment(String employment)   { this.employment = employment; }

    public java.util.List<String> getChildren()      { return children; }
    public void   setChildren(java.util.List<String> children) { this.children = children; }

    public String getOwnerUid()          { return ownerUid; }
    public void   setOwnerUid(String uid){ this.ownerUid = uid; }

    public String getAddress()           { return address; }
    public void   setAddress(String a)   { this.address = a; }

    /* ↓↓↓  НОВЫЕ ГЕТТЕРЫ/СЕТТЕРЫ  ↓↓↓ */
    public String getMaritalStatus()           { return maritalStatus; }
    public void   setMaritalStatus(String s)   { this.maritalStatus = s; }

    public String getSpouse()                 { return spouse; }
    public void   setSpouse(String s)         { this.spouse = s; }

    public String getStatus()        { return status; }
    public void   setStatus(String s){ this.status = s; }

    public Timestamp getUpdatedAt()           { return updatedAt; }
    public void      setUpdatedAt(Timestamp t){ this.updatedAt = t; }

    public Citizen() {}
}
