package ru.mirea.censusinfo.data;

import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ru.mirea.censusinfo.data.local.LocalDatabase;
import ru.mirea.censusinfo.data.local.PendingCitizen;
import ru.mirea.censusinfo.data.local.PendingHousehold;
import ru.mirea.censusinfo.util.ConnectivityUtil;

public class FirestoreRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final LocalDatabase     localDb;

    public FirestoreRepository(Context ctx){
        db.setFirestoreSettings(new FirebaseFirestoreSettings
                .Builder().setPersistenceEnabled(true).build());
        localDb = LocalDatabase.getInstance(ctx);
    }

    /*────────────────────── USERS ─────────────────────*/
    public Task<Void> addUser(User u){
        return db.collection("users").document(u.getUid()).set(u);
    }
    public Task<DocumentSnapshot> getUser(String uid){
        return db.collection("users").document(uid).get();
    }

    public Task<User> findUserByEmail(String email) {
        return db.collection("users")
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .continueWith(task -> {
                    if (!task.isSuccessful() || task.getResult().isEmpty())
                        return null;                    // юзера нет
                    return task.getResult()
                            .getDocuments()
                            .get(0)
                            .toObject(User.class);
                });
    }

    /*─────────────────── HOUSEHOLDS ───────────────────*/
    public Task<Void> addHousehold(Household h){
        return db.collection("households").document(h.getId()).set(h);
    }
    public Task<DocumentSnapshot> getHousehold(String id){
        return db.collection("households").document(id).get();
    }
    public Task<List<Household>> getHouseholdsByOwner(String uid){
        return db.collection("households")
                .whereEqualTo("censusTakerId", uid)
                .get()
                .continueWith(t->t.getResult().toObjects(Household.class));
    }
    /* offline-очередь ↓ */
    public Task<Void> addOrQueueHousehold(Household h){
        if (ConnectivityUtil.hasNetwork())
            return addHousehold(h)
                    .addOnFailureListener(e->
                            localDb.pendingDao().insert(PendingHousehold.from(h)));
        localDb.pendingDao().insert(PendingHousehold.from(h));
        return Tasks.forResult(null);
    }
    public List<Household> pendingHouseholds(){
        List<Household> out = new ArrayList<>();
        for (PendingHousehold p: localDb.pendingDao().getAll())
            out.add(p.toHousehold());
        return out;
    }

    /*──────────────────── CITIZENS ────────────────────*/
    public CollectionReference citizens(){ return db.collection("citizens"); }

    public Task<DocumentSnapshot> getCitizen(String id){
        return citizens().document(id).get();
    }
    public Task<Void> addCitizen(Citizen c){
        return citizens().document(c.getId()).set(c);
    }
    public Task<Void> addOrQueueCitizen(Citizen c){
        if (ConnectivityUtil.hasNetwork())
            return addCitizen(c)
                    .addOnFailureListener(e->
                            localDb.pendingCitizenDao().insert(PendingCitizen.from(c)));
        localDb.pendingCitizenDao().insert(PendingCitizen.from(c));
        return Tasks.forResult(null);
    }
    public List<Citizen> pendingCitizens(){
        List<Citizen> out = new ArrayList<>();
        for (PendingCitizen p: localDb.pendingCitizenDao().getAll())
            out.add(p.toCitizen());
        return out;
    }
    public Task<List<Citizen>> getCitizensByOwner(String uid){
        return citizens()
                .whereEqualTo("ownerUid", uid)
                .get()
                .continueWith(t->t.getResult().toObjects(Citizen.class));
    }
    public Task<List<Citizen>> getAllCitizens(){
        return citizens().get()
                .continueWith(t->t.getResult().toObjects(Citizen.class));
    }

    /*────────────────── SUPPORT TICKETS ─────────────────*/
    public CollectionReference supportRequests(){
        return db.collection("supportRequests");
    }
    public Task<Void> addSupportRequest(SupportRequest r){
        return supportRequests()
                .document(r.getId())
                .set(r);
    }
    public Task<List<SupportRequest>> getAllSupportRequests(){
        return supportRequests()
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .continueWith(t->t.getResult().toObjects(SupportRequest.class));
    }

    /*──────────────────── STATS ────────────────────*/
    public Task<List<Household>> getAllHouseholds(){
        return db.collection("households").get()
                .continueWith(t->t.getResult().toObjects(Household.class));
    }

    /*───────────────── HELPERS (ID) ─────────────────*/
    public Task<String> getOrCreateHouseholdId(String address){
        TaskCompletionSource<String> src = new TaskCompletionSource<>();
        db.collection("households").whereEqualTo("address", address).limit(1).get()
                .addOnSuccessListener(q->{
                    if (!q.isEmpty()){
                        src.setResult(q.getDocuments().get(0).getId()); return;
                    }
                    String id = UUID.randomUUID().toString();
                    Household h = new Household(id, address, "",
                            FirebaseAuth.getInstance().getUid(), Timestamp.now());
                    addOrQueueHousehold(h).addOnSuccessListener(v->src.setResult(id))
                            .addOnFailureListener(src::setException);
                })
                .addOnFailureListener(src::setException);
        return src.getTask();
    }
}