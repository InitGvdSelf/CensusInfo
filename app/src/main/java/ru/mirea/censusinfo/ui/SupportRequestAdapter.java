package ru.mirea.censusinfo.ui;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import ru.mirea.censusinfo.R;
import ru.mirea.censusinfo.data.SupportRequest;

public class SupportRequestAdapter
        extends RecyclerView.Adapter<SupportRequestAdapter.VH> {

    private final List<SupportRequest> list = new ArrayList<>();
    private final SimpleDateFormat df =
            new SimpleDateFormat("dd.MM.yyyy  HH:mm", Locale.getDefault());

    public void setData(List<SupportRequest> data){
        list.clear(); list.addAll(data); notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder{
        TextView email,msg,date;
        VH(View v){
            super(v);
            email = v.findViewById(R.id.tvEmail);
            msg   = v.findViewById(R.id.tvMessage);
            date  = v.findViewById(R.id.tvDate);
        }
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p,int v){
        View item = LayoutInflater.from(p.getContext())
                .inflate(R.layout.item_ticket, p,false);
        return new VH(item);
    }

    @Override public int getItemCount(){ return list.size(); }

    @Override public void onBindViewHolder(@NonNull VH h,int i){
        SupportRequest r = list.get(i);
        h.email.setText(r.getUid());
        h.msg  .setText(r.getMessage());
        Timestamp ts = r.getCreatedAt();
        h.date .setText(ts==null ? "" : df.format(ts.toDate()));

        h.itemView.setOnClickListener(v ->
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Сообщение")
                        .setMessage(r.getMessage())
                        .setPositiveButton("OK",null)
                        .show());
    }
}