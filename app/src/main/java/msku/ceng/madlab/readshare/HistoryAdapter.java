package msku.ceng.madlab.readshare;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<HistoryItem> historyList;

    public HistoryAdapter(List<HistoryItem> historyList) {
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // XML tasarımını bağla (item_donation_history.xml olmalı dosya adın)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_donation_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoryItem item = historyList.get(position);

        // Bilgileri Yaz
        holder.tvBookName.setText(item.getBookName());
        holder.tvSchool.setText(item.getSchoolName());

        // Tarihi Formatla (Örn: 27 Dec 2025)
        if (item.getDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            holder.tvDate.setText(sdf.format(item.getDate().toDate()));
        } else {
            holder.tvDate.setText("Unknown Date");
        }
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvBookName, tvSchool, tvDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // item_donation_history.xml içindeki ID'lerinle eşleşmeli
            tvBookName = itemView.findViewById(R.id.tvHistoryBookName);
            tvSchool = itemView.findViewById(R.id.tvHistorySchool);
            tvDate = itemView.findViewById(R.id.tvHistoryDate);
        }
    }
}