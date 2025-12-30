package msku.ceng.madlab.readshare;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DeliveryTrackingActivity extends AppCompatActivity {

    private RecyclerView rvHistory;
    private ImageView btnBack;
    private HistoryAdapter adapter;
    private List<HistoryItem> historyList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_tracking);

        db = FirebaseFirestore.getInstance();
        btnBack = findViewById(R.id.btnBack);
        rvHistory = findViewById(R.id.rvHistory);

        btnBack.setOnClickListener(v -> finish());

        // RecyclerView Kurulumu
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HistoryAdapter(historyList);
        rvHistory.setAdapter(adapter);

        loadHistory();
    }

    private void loadHistory() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Kullanıcının "history" koleksiyonunu tarihe göre sıralayıp çekiyoruz
        db.collection("users").document(userId).collection("history")
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    historyList.clear();
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            HistoryItem item = doc.toObject(HistoryItem.class);
                            historyList.add(item);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "No donations found yet.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // --- Basit İç Adapter Sınıfı ---
    private class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
        private List<HistoryItem> list;

        public HistoryAdapter(List<HistoryItem> list) { this.list = list; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_donation_history, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            HistoryItem item = list.get(position);
            holder.tvBook.setText(item.getBookName());
            holder.tvSchool.setText(item.getSchoolName());

            // Tarih Formatlama
            if (item.getDate() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
                holder.tvDate.setText(sdf.format(item.getDate().toDate()));
            }
        }

        @Override
        public int getItemCount() { return list.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvBook, tvSchool, tvDate;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvBook = itemView.findViewById(R.id.tvHistoryBookName);
                tvSchool = itemView.findViewById(R.id.tvHistorySchool);
                tvDate = itemView.findViewById(R.id.tvHistoryDate);
            }
        }
    }
}