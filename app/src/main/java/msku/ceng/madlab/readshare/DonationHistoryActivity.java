package msku.ceng.madlab.readshare;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.List;

public class DonationHistoryActivity extends AppCompatActivity {

    private RecyclerView rvHistory;
    private TextView tvEmptyState;
    private ImageView btnBack;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private List<HistoryItem> historyList;
    private HistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_history);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        rvHistory = findViewById(R.id.rvHistory);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        btnBack = findViewById(R.id.btnBack);

        // RecyclerView Ayarları
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        historyList = new ArrayList<>();
        adapter = new HistoryAdapter(historyList);
        rvHistory.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());

        loadHistoryData();
    }

    private void loadHistoryData() {
        if (auth.getCurrentUser() == null) return;

        String userId = auth.getCurrentUser().getUid();

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

                        rvHistory.setVisibility(View.VISIBLE);
                        tvEmptyState.setVisibility(View.GONE);
                    } else {
                        rvHistory.setVisibility(View.GONE);
                        tvEmptyState.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}