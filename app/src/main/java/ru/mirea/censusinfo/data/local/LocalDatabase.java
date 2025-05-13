package ru.mirea.censusinfo.data.local;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import ru.mirea.censusinfo.data.Citizen;
import ru.mirea.censusinfo.data.Household;

/** Простейшая in-memory «БД», удовлетворяющая уже использующимся методам. */
public final class LocalDatabase {

    /* ----- DAO-интерфейсы ------------------------------------------------- */
    public interface PendingHouseholdDao {
        void insert(PendingHousehold p);
        List<PendingHousehold> getAll();
        void deleteById(String id);
    }
    public interface PendingCitizenDao {
        void insert(PendingCitizen p);
        List<PendingCitizen> getAll();
        void deleteById(String id);
    }

    /* ----- thread-safe singleton ----------------------------------------- */
    private static volatile LocalDatabase INSTANCE;
    public static LocalDatabase getInstance(Context ctx) {
        if (INSTANCE == null) {
            synchronized (LocalDatabase.class) {
                if (INSTANCE == null) INSTANCE = new LocalDatabase();
            }
        }
        return INSTANCE;
    }
    private LocalDatabase() {}   // приватный

    /* ----- in-memory DAO реализации -------------------------------------- */
    private final List<PendingHousehold> hhMem = new CopyOnWriteArrayList<>();
    private final List<PendingCitizen>   ctMem = new CopyOnWriteArrayList<>();

    private final PendingHouseholdDao householdDao = new PendingHouseholdDao() {
        @Override public void insert(PendingHousehold p) { hhMem.add(p); }
        @Override public List<PendingHousehold> getAll() { return new ArrayList<>(hhMem); }
        @Override public void deleteById(String id)      { hhMem.removeIf(p -> id.equals(p.getId())); }
    };

    private final PendingCitizenDao citizenDao = new PendingCitizenDao() {
        @Override public void insert(PendingCitizen p) { ctMem.add(p); }
        @Override public List<PendingCitizen> getAll() { return new ArrayList<>(ctMem); }
        @Override public void deleteById(String id)    { ctMem.removeIf(p -> id.equals(p.getId())); }
    };

    /* методы, которые вызывает FirestoreRepository */
    public PendingHouseholdDao pendingDao()          { return householdDao; }
    public PendingCitizenDao  pendingCitizenDao()    { return citizenDao;  }
}
