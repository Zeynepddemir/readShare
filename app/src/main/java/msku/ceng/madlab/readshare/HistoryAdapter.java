package msku.ceng.madlab.readshare;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
        // XML tasarımını bağla
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_donation_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoryItem item = historyList.get(position);

        // 1. Temel Bilgileri Yaz
        holder.tvBookName.setText(item.getBookName());
        holder.tvSchool.setText(item.getSchoolName());

        // 2. Tarihi Formatla (Örn: 27 Dec 2025)
        if (item.getDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
            String formattedDate = sdf.format(item.getDate().toDate());
            holder.tvDate.setText(formattedDate);
        } else {
            holder.tvDate.setText("Date Unknown");
        }

        // 3. MESAJ KONTROLÜ (DİNAMİK GİZLEME/GÖSTERME) 🔍
        String message = item.getStudentMessage();

        if (message != null && !message.trim().isEmpty()) {
            // Mesaj varsa kutuyu GÖSTER
            holder.layoutMessage.setVisibility(View.VISIBLE);
            holder.tvMessage.setText("\"" + message + "\""); // Tırnak içine alarak yaz
        } else {
            // Mesaj yoksa kutuyu GİZLE (Yer kaplamasın)
            holder.layoutMessage.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    // --- ViewHolder Sınıfı ---
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvBookName, tvSchool, tvDate, tvMessage;
        LinearLayout layoutMessage; // Mesaj kutusunun kendisi

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // XML'deki ID'leri eşleştir
            tvBookName = itemView.findViewById(R.id.tvHistoryBookName);
            tvSchool = itemView.findViewById(R.id.tvHistorySchool);
            tvDate = itemView.findViewById(R.id.tvHistoryDate);

            // Yeni Eklenen Mesaj Alanları
            tvMessage = itemView.findViewById(R.id.tvStudentMessage);
            layoutMessage = itemView.findViewById(R.id.layoutMessage);
        }
    }
}