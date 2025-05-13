package ru.mirea.censusinfo.ui;

import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.*;
import ru.mirea.censusinfo.R;
import com.google.firebase.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class MyFormsAdapter extends RecyclerView.Adapter<MyFormsAdapter.VH>{
    public interface OnItemClick{ void onClick(MyFormsFragment.Item it); }
    private final List<MyFormsFragment.Item> list = new ArrayList<>();
    private final OnItemClick listener;
    public MyFormsAdapter(OnItemClick l){ listener=l; }

    public void setData(List<MyFormsFragment.Item> l){
        list.clear(); list.addAll(l); notifyDataSetChanged();
    }
    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p,int v){
        View view = LayoutInflater.from(p.getContext())
                     .inflate(R.layout.item_my_form, p,false);
        return new VH(view);
    }
    @Override public int getItemCount(){ return list.size(); }

    static class VH extends RecyclerView.ViewHolder{
        TextView tvFio, tvAddr, tvDate, tvStatus;
        VH(View v){ super(v);
            tvFio   = v.findViewById(R.id.tvFio);
            tvAddr  = v.findViewById(R.id.tvAddress);
            tvDate  = v.findViewById(R.id.tvDate);
            tvStatus= v.findViewById(R.id.tvStatus);
        }
    }

    @Override public void onBindViewHolder(@NonNull VH h,int i){
        MyFormsFragment.Item it = list.get(i);

        String addr = it.hh != null ? it.hh.getAddress()
                       : it.ct != null ? it.ct.getAddress() : "";
        h.tvAddr.setText(addr);

        if (it.ct != null) {
            h.tvFio.setText(it.ct.getFullName());
            h.tvDate.setText(fmt(it.ct.getUpdatedAt()));
            String st = it.ct != null ? it.ct.getStatus() : "";
            if      ("pending" .equalsIgnoreCase(st)) st = "На рассмотрении";
            else if ("approved".equalsIgnoreCase(st)) st = "Одобрено";
            h.tvStatus.setText(st);
        } else {
            h.tvFio.setText("(не заполнено)");
            h.tvDate.setText("");
            h.tvStatus.setText("");
        }
        h.itemView.setOnClickListener(v->listener.onClick(it));
    }

    private String fmt(Timestamp ts){
        return ts == null ? "" :
               new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                       .format(ts.toDate());
    }
}
