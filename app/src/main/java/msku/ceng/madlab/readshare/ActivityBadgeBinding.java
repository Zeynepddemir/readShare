package msku.ceng.madlab.readshare;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;
import msku.ceng.madlab.readshare.databinding.ActivityBadgeCollectionBinding;

public class ActivityBadgeBinding extends AppCompatActivity {

    private ActivityBadgeCollectionBinding binding;
    private FirebaseFirestore db;
    private String studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Layout bağlama
        binding = ActivityBadgeCollectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        studentId = getIntent().getStringExtra("studentId");

        binding.btnBack.setOnClickListener(v -> finish());

        loadBadges();
    }

    private void loadBadges() {
        if (studentId == null) return;

        db.collection("students").document(studentId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> earnedBadges = (List<String>) documentSnapshot.get("badges");

                        if (earnedBadges != null) {
                            checkAndUnlock(earnedBadges, "Book Beginner", binding.badgeBookBeginner);
                            checkAndUnlock(earnedBadges, "Reading Streak", binding.badgeReadingStreak);
                            checkAndUnlock(earnedBadges, "Diary Keeper", binding.badgeDiaryKeeper);
                            checkAndUnlock(earnedBadges, "Kind Heart", binding.badgeKindHeart);
                            checkAndUnlock(earnedBadges, "Goal Achiever", binding.badgeGoalAchiever);
                            checkAndUnlock(earnedBadges, "Super Reader", binding.badgeSuperReader);
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error loading badges", Toast.LENGTH_SHORT).show()
                );
    }

    private void checkAndUnlock(List<String> list, String badgeName, View viewLayout) {
        if (list.contains(badgeName)) {
            viewLayout.setAlpha(1.0f);
        } else {
            viewLayout.setAlpha(0.3f);
        }
    }
}