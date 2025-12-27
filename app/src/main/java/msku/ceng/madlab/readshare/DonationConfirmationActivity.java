package msku.ceng.madlab.readshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import msku.ceng.madlab.readshare.databinding.ActivityDonationConfirmationBinding;

public class DonationConfirmationActivity extends AppCompatActivity {

    private ActivityDonationConfirmationBinding binding;
    private FirebaseFirestore db;
    private String studentId;
    private String bookName; // Kitap adını global yaptık

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDonationConfirmationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        // Verileri Al
        bookName = getIntent().getStringExtra("bookName");
        String price = getIntent().getStringExtra("price");
        String locationDisplay = getIntent().getStringExtra("location"); // Bu sadece ekranda göstermek için

        studentId = getIntent().getStringExtra("studentId");
        if (studentId == null) studentId = "demo_student_id";

        // Ekrana Yaz
        if (bookName != null) {
            binding.tvBasketBookName.setText(bookName);
            binding.tvSummaryBookName.setText(bookName);
        }
        if (price != null) binding.tvSummaryTotal.setText("Total: " + price);
        if (locationDisplay != null) binding.tvConfirmAddress.setText(locationDisplay);

        binding.btnBack.setOnClickListener(v -> finish());

        // --- BUTONA BASINCA ---
        binding.btnFinalConfirm.setOnClickListener(v -> {
            // Butonu kilitle
            binding.btnFinalConfirm.setEnabled(false);
            binding.btnFinalConfirm.setText("Verifying School...");

            // DİNAMİK HAMLE: Öğrencinin gerçek okulunu veritabanından çek!
            fetchStudentSchoolAndDonate();
        });
    }

    private void fetchStudentSchoolAndDonate() {
        // Öğrenci belgesine git
        db.collection("students").document(studentId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    String realSchoolName = "Unknown School";

                    if (documentSnapshot.exists()) {
                        // Veritabanındaki 'school' alanını al
                        String schoolFromDb = documentSnapshot.getString("school");
                        if (schoolFromDb != null && !schoolFromDb.isEmpty()) {
                            realSchoolName = schoolFromDb;
                        }
                    }

                    // Okul ismini bulduk, şimdi kaydet
                    saveBookToLibrary(bookName, realSchoolName);
                })
                .addOnFailureListener(e -> {
                    // Hata olursa varsayılan isimle devam et (İşlem yarıda kalmasın)
                    saveBookToLibrary(bookName, "Unknown School");
                });
    }

    private void saveBookToLibrary(String bookTitle, String verifiedSchoolName) {
        binding.btnFinalConfirm.setText("Processing Donation...");

        Map<String, Object> newBook = new HashMap<>();
        newBook.put("title", bookTitle);
        newBook.put("author", "Gifted Donor");
        newBook.put("pageCount", "300");
        newBook.put("status", "Not Started");
        newBook.put("addedDate", Timestamp.now());
        newBook.put("donorMessage", "Good luck!");

        String myDonorId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        newBook.put("donorId", myDonorId);

        // 1. Öğrenciye Ekle
        db.collection("students").document(studentId).collection("library")
                .add(newBook)
                .addOnSuccessListener(documentReference -> {

                    // 2. Bağışçının İstatistiklerini GÜNCEL OKUL İSMİYLE güncelle
                    updateDonorStats(myDonorId, verifiedSchoolName);
                })
                .addOnFailureListener(e -> {
                    binding.btnFinalConfirm.setEnabled(true);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateDonorStats(String donorId, String schoolName) {
        // 1. İstatistikleri Güncelle
        Map<String, Object> updates = new HashMap<>();
        updates.put("totalDonations", FieldValue.increment(1));
        updates.put("helpedSchools", FieldValue.arrayUnion(schoolName));

        db.collection("users").document(donorId)
                .update(updates);

        // 2. GEÇMİŞ KAYDI OLUŞTUR
        Map<String, Object> historyRecord = new HashMap<>();
        historyRecord.put("bookName", getIntent().getStringExtra("bookName"));
        historyRecord.put("schoolName", schoolName);
        historyRecord.put("date", com.google.firebase.Timestamp.now());

        // --- MANTIKLI OLAN ---
        // Kitap yeni bağışlandı, henüz öğrenci teslim almadı ve yazmadı.
        // O yüzden mesaj BOŞ.
        historyRecord.put("studentMessage", "");

        db.collection("users").document(donorId).collection("history")
                .add(historyRecord)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Donation Complete! 🎉", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(DonationConfirmationActivity.this, DonorDiscoveryActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                });
    }
}