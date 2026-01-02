package msku.ceng.madlab.readshare;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class StudentProfileActivity extends AppCompatActivity {

    private TextView tvDetailName, tvDetailSchool, tvProgressText;
    private ProgressBar progressBarReading;
    private LinearLayout layoutEarnedBadgesContainer, layoutBookListContainer;
    private Button btnAddDiary, btnAddBook, btnMarkReceived;
    private ImageView btnBack;
    private TextView btnViewAllBadges;

    private FirebaseFirestore db;
    private String studentId;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);

        db = FirebaseFirestore.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        studentId = getIntent().getStringExtra("studentId");

        tvDetailName = findViewById(R.id.tvDetailName);
        tvDetailSchool = findViewById(R.id.tvDetailSchool);
        tvProgressText = findViewById(R.id.tvProgressText);
        progressBarReading = findViewById(R.id.progressBarReading);

        layoutEarnedBadgesContainer = findViewById(R.id.layoutEarnedBadgesContainer);
        layoutBookListContainer = findViewById(R.id.layoutBookListContainer);

        btnAddDiary = findViewById(R.id.btnAddDiary);
        btnAddBook = findViewById(R.id.btnAddBook);
        btnMarkReceived = findViewById(R.id.btnMarkReceived);

        btnBack = findViewById(R.id.btnBack);
        btnViewAllBadges = findViewById(R.id.btnViewAllBadges);

        loadStudentDetails();

        btnBack.setOnClickListener(v -> finish());

        btnAddDiary.setOnClickListener(v -> {
            Intent intent = new Intent(this, ReadingDiaryActivity.class);
            intent.putExtra("studentId", studentId);
            startActivity(intent);
        });

        btnViewAllBadges.setOnClickListener(v -> {
            Intent intent = new Intent(StudentProfileActivity.this, ActivityBadgeBinding.class);
            intent.putExtra("studentId", studentId);
            startActivity(intent);
        });

        btnAddBook.setOnClickListener(v -> showRequestDialog());

        btnMarkReceived.setOnClickListener(v -> {
            db.collection("students").document(studentId)
                    .update("status", "Received")
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Book Received! Cycle Complete. 🎉", Toast.LENGTH_SHORT).show();

                        db.collection("students").document(studentId)
                                .update("badges", com.google.firebase.firestore.FieldValue.arrayUnion("Kind Heart"));
                    });
        });
    }

    private void loadStudentDetails() {
        if (studentId == null) return;

        db.collection("students").document(studentId).addSnapshotListener((doc, e) -> {
            if (e != null) return;

            if (doc != null && doc.exists()) {
                tvDetailName.setText(doc.getString("name"));
                tvDetailSchool.setText(doc.getString("schoolName"));

                Long completed = doc.getLong("completedBooks");
                int completedCount = (completed != null) ? completed.intValue() : 0;
                progressBarReading.setProgress(completedCount);
                tvProgressText.setText(completedCount + " / 10 Books Read");

                calculateStudentLevelInBackground(completedCount);

                List<String> badges = new ArrayList<>();
                if (doc.get("badges") != null) {
                    badges = (List<String>) doc.get("badges");
                }
                updateBadgesUI(badges);

                loadLists(doc.getString("bookNeed"));

                String teacherId = doc.getString("teacherId");
                String status = doc.getString("status");
                manageTeacherButtons(teacherId, status);
            }
        });
    }
//-----------------THREAD -------------------------
    private void calculateStudentLevelInBackground(int completedBooks) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Basit bir seviye mantığı
                final String levelName;
                if (completedBooks < 3) {
                    levelName = "Starter Reader 🥉";
                } else if (completedBooks < 7) {
                    levelName = "Skilled Reader 🥈";
                } else {
                    levelName = "Master Bookworm 🥇";
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!isFinishing()) {
                            Toast.makeText(StudentProfileActivity.this,
                                    "Level Analysis: " + levelName,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }).start();
    }

    private void updateBadgesUI(List<String> badges) {
        layoutEarnedBadgesContainer.removeAllViews();

        if (badges == null || badges.isEmpty()) {
            return;
        }

        for (String badgeName : badges) {
            LinearLayout badgeCard = new LinearLayout(this);
            badgeCard.setOrientation(LinearLayout.HORIZONTAL);
            badgeCard.setPadding(16, 16, 16, 16);
            badgeCard.setBackgroundResource(R.drawable.input_border_red);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 16);
            badgeCard.setLayoutParams(params);
            badgeCard.setGravity(android.view.Gravity.CENTER_VERTICAL);

            ImageView badgeIcon = new ImageView(this);
            int iconRes = android.R.drawable.btn_star_big_on;
            String description = "";

            switch (badgeName) {
                case "Book Beginner":
                    iconRes = android.R.drawable.ic_menu_myplaces;
                    description = "Congratulations on reading your very first book! Every great reader starts with one page.";
                    break;
                case "Reading Streak":
                    iconRes = android.R.drawable.ic_menu_agenda;
                    description = "Awarded for consistent reading habits. You have demonstrated strong dedication!";
                    break;
                case "Diary Keeper":
                    iconRes = android.R.drawable.ic_menu_edit;
                    description = "You made your first entry! You are turning your reading into memories.";
                    break;
                case "Kind Heart":
                    iconRes = android.R.drawable.btn_star_big_on;
                    description = "You sent your first Thank-You! Sharing joy makes someone's day brighter.";
                    break;
                case "Goal Achiever":
                    iconRes = android.R.drawable.ic_menu_compass;
                    description = "You reached your reading goal! Proof of your focus and determination.";
                    break;
                case "Super Reader":
                    iconRes = android.R.drawable.ic_menu_view;
                    description = "You've read 10 books! Keep going, Super Reader!";
                    break;
            }

            badgeIcon.setImageResource(iconRes);

            if (badgeName.equals("Kind Heart")) {
                badgeIcon.setColorFilter(Color.RED);
            } else {
                badgeIcon.setColorFilter(null);
            }

            badgeIcon.setLayoutParams(new LinearLayout.LayoutParams(120, 120));

            LinearLayout textLayout = new LinearLayout(this);
            textLayout.setOrientation(LinearLayout.VERTICAL);
            textLayout.setPadding(24, 0, 0, 0);

            TextView tvTitle = new TextView(this);
            tvTitle.setText(badgeName);
            tvTitle.setTextSize(16);
            tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
            tvTitle.setTextColor(Color.BLACK);

            TextView tvDesc = new TextView(this);
            tvDesc.setText(description);
            tvDesc.setTextSize(12);
            tvDesc.setTextColor(Color.DKGRAY);

            textLayout.addView(tvTitle);
            textLayout.addView(tvDesc);

            badgeCard.addView(badgeIcon);
            badgeCard.addView(textLayout);

            layoutEarnedBadgesContainer.addView(badgeCard);
        }
    }

    private void manageTeacherButtons(String teacherId, String status) {
        if (currentUserId != null && currentUserId.equals(teacherId)) {
            if ("Donated".equals(status)) {
                btnMarkReceived.setVisibility(View.VISIBLE);
                btnAddBook.setVisibility(View.GONE);
            } else if ("Received".equals(status)) {
                btnMarkReceived.setVisibility(View.GONE);
                btnAddBook.setVisibility(View.VISIBLE);
                btnAddBook.setText("Request Next Book");
            } else {
                btnMarkReceived.setVisibility(View.GONE);
                btnAddBook.setVisibility(View.VISIBLE);
                btnAddBook.setText("Add New Book Need");
            }
            btnAddDiary.setVisibility(View.VISIBLE);
        } else {
            btnMarkReceived.setVisibility(View.GONE);
            btnAddBook.setVisibility(View.GONE);
            btnAddDiary.setVisibility(View.GONE);
        }
    }

    private void loadLists(String currentNeed) {
        layoutBookListContainer.removeAllViews();
        TextView title = new TextView(this);
        title.setText("Current Need: " + (currentNeed != null ? currentNeed : "None"));
        title.setTextSize(16);
        title.setPadding(0, 20, 0, 20);
        title.setTextColor(Color.BLACK);
        layoutBookListContainer.addView(title);
    }

    private void showRequestDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Book Need");
        final EditText input = new EditText(this);
        input.setHint("Enter book name...");
        builder.setView(input);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String book = input.getText().toString().trim();
            if (!book.isEmpty()) {
                db.collection("students").document(studentId)
                        .update("bookNeed", book, "status", "Waiting")
                        .addOnSuccessListener(aVoid -> Toast.makeText(this, "Need Updated!", Toast.LENGTH_SHORT).show());
            }
        });
        builder.show();
    }
}