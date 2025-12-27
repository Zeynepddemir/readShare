package msku.ceng.madlab.readshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

    // XML'deki yeni ID'lere göre tanımlamalar
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

        // 1. GÖRÜNÜMLERİ BAĞLA (Yeni Tasarıma Göre)
        tvBookName = findViewById(R.id.tvBasketBookName);
        tvAuthor = findViewById(R.id.tvBasketAuthor); // Genelde boş gelir ama kodda dursun
        tvAddress = findViewById(R.id.tvConfirmAddress);
        tvSummaryBook = findViewById(R.id.tvSummaryBookName);
        tvTotal = findViewById(R.id.tvSummaryTotal);
        btnConfirm = findViewById(R.id.btnFinalConfirm);
        btnBack = findViewById(R.id.btnBack);

        // 2. VERİLERİ AL (Profile sayfasından gelen paket)
        Intent intent = getIntent();
        bookName = intent.getStringExtra("bookName");
        schoolName = intent.getStringExtra("schoolName");
        studentId = intent.getStringExtra("studentId");

        // 3. EKRANA YAZDIR
        if (bookName != null) {
            tvBookName.setText(bookName);
            tvSummaryBook.setText(bookName);
        } else {
            tvBookName.setText("Unknown Book");
        }

        if (schoolName != null) {
            tvAddress.setText(schoolName);
        } else {
            tvAddress.setText("Unknown Address");
        }

        // Yazar bilgisi veri tabanında tutulmadığı için varsayılan bırakıyoruz
        tvAuthor.setText("Classic Book");

        // Bağış olduğu için ücret 0
        tvTotal.setText("Total: Free Donation");

        // 4. GERİ BUTONU
        btnBack.setOnClickListener(v -> finish());

        // 5. ONAY BUTONU
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
        // A) İstatistikleri Güncelle (Toplam bağış sayısı artar)
        Map<String, Object> updates = new HashMap<>();
        updates.put("totalDonations", FieldValue.increment(1));
        updates.put("helpedSchools", FieldValue.arrayUnion(schoolName));

        db.collection("users").document(donorId).update(updates);

        // B) Geçmişe (History) Ekle
        Map<String, Object> historyRecord = new HashMap<>();
        historyRecord.put("bookName", bookName);
        historyRecord.put("schoolName", schoolName);
        historyRecord.put("studentId", studentId);
        historyRecord.put("date", com.google.firebase.Timestamp.now());
        historyRecord.put("studentMessage", ""); // Mesaj şimdilik boş

        db.collection("users").document(donorId).collection("history")
                .add(historyRecord)
                .addOnSuccessListener(aVoid -> {
                    // C) Başarılı Olunca Yönlendir
                    Toast.makeText(this, "Donation Confirmed! Thank you! 🚀", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(DonationConfirmationActivity.this, DonorDiscoveryActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}