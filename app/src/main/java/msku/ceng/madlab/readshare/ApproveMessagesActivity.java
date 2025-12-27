package msku.ceng.madlab.readshare;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
// Binding kullanmıyoruz, manuel bağlıyoruz daha garanti olsun diye
// import msku.ceng.madlab.readshare.databinding.ActivityApproveMessagesBinding;

public class ApproveMessagesActivity extends AppCompatActivity {

    private LinearLayout layoutContainer;
    private FirebaseFirestore db;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_messages);

        db = FirebaseFirestore.getInstance();
        layoutContainer = findViewById(R.id.layoutMessagesContainer);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        // Bekleyen mesajları veritabanından çek
        fetchPendingMessages();
    }

    private void fetchPendingMessages() {
        layoutContainer.removeAllViews(); // Önce temizle

        db.collection("messages")
                .whereEqualTo("status", "Pending") // Sadece onaysızları getir
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(this, "No pending messages.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        String msgId = snapshot.getId();
                        String studentName = snapshot.getString("studentName");
                        String content = snapshot.getString("content");

                        // Dinamik olarak kart oluştur ve ekle
                        addMessageCard(msgId, studentName, content);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void addMessageCard(String msgId, String name, String content) {
        // Kart Tasarımını (daha önce oluşturduğumuz item_approval_message) kullan
        View card = LayoutInflater.from(this).inflate(R.layout.item_approval_message, layoutContainer, false);

        TextView tvName = card.findViewById(R.id.tvStudentName);
        TextView tvContent = card.findViewById(R.id.tvMessageContent);
        Button btnApprove = card.findViewById(R.id.btnApprove);
        Button btnReject = card.findViewById(R.id.btnReject);

        tvName.setText(name);
        tvContent.setText(content);

        // ONAYLA
        btnApprove.setOnClickListener(v -> {
            updateMessageStatus(msgId, "Approved");
        });

        // REDDET
        btnReject.setOnClickListener(v -> {
            updateMessageStatus(msgId, "Rejected");
        });

        layoutContainer.addView(card);
    }

    private void updateMessageStatus(String msgId, String newStatus) {
        db.collection("messages").document(msgId)
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Message " + newStatus + "!", Toast.LENGTH_SHORT).show();
                    fetchPendingMessages(); // Listeyi yenile ki işlem yapılan mesaj gitsin
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error updating status", Toast.LENGTH_SHORT).show());
    }
}