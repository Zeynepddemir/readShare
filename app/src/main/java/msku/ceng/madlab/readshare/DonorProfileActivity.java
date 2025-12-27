package msku.ceng.madlab.readshare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class DonorProfileActivity extends AppCompatActivity {

    private ImageView btnBack;
    private Button btnLogout;
    private TextView tvDonorName, tvDonorLevel, tvDonationCount, tvSchoolCount;
    private TextView btnHistory; // History Butonu

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_profile);

        // Firebase Başlat
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Görünüm Elemanlarını Bağla
        btnBack = findViewById(R.id.btnBack);
        btnLogout = findViewById(R.id.btnLogout);
        tvDonorName = findViewById(R.id.tvDonorName);
        tvDonorLevel = findViewById(R.id.tvDonorLevel);
        tvDonationCount = findViewById(R.id.tvDonationCount);
        tvSchoolCount = findViewById(R.id.tvSchoolCount);
        btnHistory = findViewById(R.id.btnHistory); // Yeni buton

        // Verileri Çek
        fetchUserData();

        // --- BUTON İŞLEMLERİ ---

        // 1. Geri Dön
        btnBack.setOnClickListener(v -> finish());

        // 2. Geçmiş Sayfasına Git
        btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(DonorProfileActivity.this, DonationHistoryActivity.class);
            startActivity(intent);
        });

        // 3. Çıkış Yap (Log Out)
        btnLogout.setOnClickListener(v -> {
            auth.signOut();
            Toast.makeText(this, "Logged out successfully! 👋", Toast.LENGTH_SHORT).show();

            // Giriş Ekranına Dön
            Intent intent = new Intent(DonorProfileActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void fetchUserData() {
        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();

            db.collection("users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // A) İSİM
                            String name = documentSnapshot.getString("name");
                            if (name != null) tvDonorName.setText(name);

                            // B) TOPLAM BAĞIŞ SAYISI
                            Long donations = documentSnapshot.getLong("totalDonations");
                            if (donations == null) donations = 0L;
                            tvDonationCount.setText(String.valueOf(donations));

                            // C) OKUL SAYISI (Listenin Uzunluğu)
                            List<String> schools = (List<String>) documentSnapshot.get("helpedSchools");
                            long schoolCount = 0;
                            if (schools != null) {
                                schoolCount = schools.size();
                            }
                            if (tvSchoolCount != null) {
                                tvSchoolCount.setText(String.valueOf(schoolCount));
                            }

                            // D) LEVEL HESAPLA
                            updateLevel(donations);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error loading profile", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void updateLevel(long count) {
        String level = "Beginner Donor"; // Başlangıç

        if (count >= 1 && count < 5) {
            level = "Bronze Donor 🥉";
        } else if (count >= 5 && count < 10) {
            level = "Silver Donor 🥈";
        } else if (count >= 10) {
            level = "Gold Donor 🥇";
        }

        tvDonorLevel.setText(level);
    }
}