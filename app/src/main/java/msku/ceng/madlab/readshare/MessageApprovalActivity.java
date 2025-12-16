package msku.ceng.madlab.readshare;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import msku.ceng.madlab.readshare.databinding.ActivityMessageApprovalBinding;

public class MessageApprovalActivity extends AppCompatActivity {

    private ActivityMessageApprovalBinding binding;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMessageApprovalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        binding.btnBack.setOnClickListener(v -> finish());

        fetchPendingMessages();
    }

    private void fetchPendingMessages() {
        binding.layoutMessageContainer.removeAllViews(); // Temizle

        // Sadece durumu 'Pending' olanları getir
        db.collection("messages")
                .whereEqualTo("status", "Pending")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(this, "No pending messages.", Toast.LENGTH_SHORT).show();
                    } else {
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                            Message msg = snapshot.toObject(Message.class);
                            if (msg != null) {
                                msg.setDocumentId(snapshot.getId()); // ID'yi kaydet
                                addMessageCard(msg);
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void addMessageCard(Message msg) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_approval_message, binding.layoutMessageContainer, false);

        TextView tvSender = view.findViewById(R.id.tvSender);
        TextView tvContent = view.findViewById(R.id.tvContent);
        Button btnApprove = view.findViewById(R.id.btnApprove);
        Button btnReject = view.findViewById(R.id.btnReject);

        tvSender.setText("From: " + msg.getSenderName());
        tvContent.setText(msg.getContent());

        // ONAYLA BUTONU
        btnApprove.setOnClickListener(v -> {
            updateMessageStatus(msg.getDocumentId(), "Approved", view);
        });

        // REDDET BUTONU
        btnReject.setOnClickListener(v -> {
            updateMessageStatus(msg.getDocumentId(), "Rejected", view);
        });

        binding.layoutMessageContainer.addView(view);
    }

    private void updateMessageStatus(String docId, String newStatus, View cardView) {
        db.collection("messages").document(docId)
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Message " + newStatus + "!", Toast.LENGTH_SHORT).show();
                    // Kartı ekrandan kaldır (artık beklemede değil çünkü)
                    binding.layoutMessageContainer.removeView(cardView);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}