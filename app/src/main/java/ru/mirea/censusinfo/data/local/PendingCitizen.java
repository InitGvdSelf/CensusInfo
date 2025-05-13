package ru.mirea.censusinfo.data.local;

import ru.mirea.censusinfo.data.Citizen;

public class PendingCitizen {
    private String id;
    public String status;

    public String getId()          { return id; }
    public void   setId(String id) { this.id = id; }

    public static PendingCitizen from(Citizen c) {
        PendingCitizen p = new PendingCitizen();
        p.id = c.getId();
        p.status = c.getStatus();
        return p;
    }
    public Citizen toCitizen() {
        Citizen c = new Citizen();
        c.setId(id);
        c.setStatus(status);
        return c;
    }
}
