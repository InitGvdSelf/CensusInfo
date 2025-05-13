package ru.mirea.censusinfo.ui;

import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.ArrayList;
import ru.mirea.censusinfo.R;
import ru.mirea.censusinfo.data.Citizen;

public class CitizenAdapter extends RecyclerView.Adapter<CitizenAdapter.VH>{
    public interface OnItemClick { void onClick(Citizen c); }

    private final List<Citizen> list = new ArrayList<>();
    private final List<Citizen> all = new ArrayList<>();
    private final OnItemClick   listener;

    public CitizenAdapter(OnItemClick l){ this.listener = l; }

    public void setData(List<Citizen> l){
        all.clear();          all.addAll(l);
        list.clear();         list.addAll(l);
        notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvStatus;
        VH(View v){
            super(v);
            tvName   = v.findViewById(R.id.tvName);
            tvStatus = v.findViewById(R.id.tvStatus);
        }
    }

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p,int v){
        return new VH(LayoutInflater.from(p.getContext())
                .inflate(R.layout.item_citizen,p,false));
    }


    @Override public void onBindViewHolder(@NonNull VH h,int i){
        Citizen c = list.get(i);
        boolean approved = "approved".equals(c.getStatus());

        h.tvName  .setText(c.getAddress() + " — " + c.getFullName());
        h.tvStatus.setText(approved ? "Одобрено" : "Не одобрено");

        h.itemView.setOnClickListener(v -> listener.onClick(c));
    }

    /* фильтр только одобренных / не одобренных */
    public void filterByApproval(boolean approvedOnly){
        list.clear();
        for (Citizen c : all){
            if ("approved".equals(c.getStatus()) == approvedOnly)
                list.add(c);
        }
        notifyDataSetChanged();
    }
    public void resetFilter() {
        list.clear();
        list.addAll(all);
        notifyDataSetChanged();
    }
    public void filter(String q){
        list.clear();
        if (q == null || q.isEmpty()){
            list.addAll(all);
        } else {
            String s = q.toLowerCase();
            for (Citizen c : all){
                if ((c.getFullName() != null && c.getFullName().toLowerCase().contains(s)) ||
                    (c.getAddress()  != null && c.getAddress() .toLowerCase().contains(s)))
                    list.add(c);
            }
        }
        notifyDataSetChanged();
    }

    @Override public int getItemCount(){ return list.size(); }
}
