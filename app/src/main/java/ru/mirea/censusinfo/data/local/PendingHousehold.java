package ru.mirea.censusinfo.data.local;

import com.google.firebase.Timestamp;
import ru.mirea.censusinfo.data.Household;

public class PendingHousehold {
    private String id;

    public String getId()          { return id; }
    public void   setId(String id) { this.id = id; }

    /* преобразование туда-обратно ---------------------------------------- */
    public static PendingHousehold from(Household h) {
        PendingHousehold p = new PendingHousehold();
        p.id = h.getId();
        return p;
    }
    public Household toHousehold() {
        return new Household(id, null, null, null, (Timestamp) null);
    }
}
