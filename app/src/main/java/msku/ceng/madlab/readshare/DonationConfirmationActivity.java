package msku.ceng.madlab.readshare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class DonationConfirmationActivity extends AppCompatActivity {

    private TextView tvBookName, tvAuthor, tvAddress, tvSummaryBook, tvTotal;
    private Button btnConfirm;
    private ImageView btnBack;

    private FirebaseFirestore db;
    private String studentId, bookName, schoolName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_confirmation);

        db = FirebaseFirestore.getInstance();

        tvBookName = findViewById(R.id.tvBasketBookName);
        tvAuthor = findViewById(R.id.tvBasketAuthor);
        tvAddress = findViewById(R.id.tvConfirmAddress);
        tvSummaryBook = findViewById(R.id.tvSummaryBookName);
        tvTotal = findViewById(R.id.tvSummaryTotal);
        btnConfirm = findViewById(R.id.btnFinalConfirm);
        btnBack = findViewById(R.id.btnBack);

        Intent intent = getIntent();
        bookName = intent.getStringExtra("bookName");
        schoolName = intent.getStringExtra("schoolName");
        studentId = intent.getStringExtra("studentId");

        tvBookName.setText(bookName != null ? bookName : "Unknown Book");
        tvSummaryBook.setText(bookName != null ? bookName : "Unknown Book");
        tvAddress.setText(schoolName != null ? schoolName : "Unknown School");

        tvAuthor.setText("Requested Item"); // Yazar bilgisi kritik değil
        tvTotal.setText("Total: Free Donation"); // Bağış olduğu için

        btnBack.setOnClickListener(v -> finish());

        btnConfirm.setOnClickListener(v -> {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                processDonation(currentUserId, schoolName);
            } else {
                Toast.makeText(this, "Please Login First!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processDonation(String donorId, String schoolName) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("totalDonations", FieldValue.increment(1));
        updates.put("helpedSchools", FieldValue.arrayUnion(schoolName));

        db.collection("users").document(donorId).update(updates);

        HistoryItem historyItem = new HistoryItem(
                bookName,
                schoolName,
                com.google.firebase.Timestamp.now()
        );

        db.collection("users").document(donorId).collection("history")
                .add(historyItem);

        if (studentId != null) {
            db.collection("students").document(studentId)
                    .update("status", "Donated")
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Donation Successful! Thank you ❤️", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(DonationConfirmationActivity.this, DonorDiscoveryActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error updating student: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "Error: Student ID could not be retrieved!", Toast.LENGTH_LONG).show();
        }
    }
}