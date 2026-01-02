package msku.ceng.madlab.readshare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
    private TextView btnHistory;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_profile);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnBack = findViewById(R.id.btnBack);
        btnLogout = findViewById(R.id.btnLogout);
        tvDonorName = findViewById(R.id.tvDonorName);
        tvDonorLevel = findViewById(R.id.tvDonorLevel);
        tvDonationCount = findViewById(R.id.tvDonationCount);
        tvSchoolCount = findViewById(R.id.tvSchoolCount);
        btnHistory = findViewById(R.id.btnHistory);

        fetchUserData();


        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                Log.d("ProfileActivity", " ");
                finish();
            });
        }

        btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(DonorProfileActivity.this, DonationHistoryActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            auth.signOut();
            Toast.makeText(this, "Logged out successfully! 👋", Toast.LENGTH_SHORT).show();

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
                            String name = documentSnapshot.getString("name");
                            tvDonorName.setText(name != null ? name : "Donor");

                            Long donations = documentSnapshot.getLong("totalDonations");
                            if (donations == null) donations = 0L;
                            tvDonationCount.setText(String.valueOf(donations));

                            Object schoolsObj = documentSnapshot.get("helpedSchools");
                            long schoolCount = 0;
                            if (schoolsObj instanceof List) {
                                schoolCount = ((List<?>) schoolsObj).size();
                            }

                            if (tvSchoolCount != null) {
                                tvSchoolCount.setText(String.valueOf(schoolCount));
                            }

                            updateLevel(donations);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error loading profile", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void updateLevel(long count) {
        String level = "Beginner Donor";

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