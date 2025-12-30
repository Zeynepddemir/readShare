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

        // Intent'ten öğrenci ID'sini al
        studentId = getIntent().getStringExtra("studentId");

        // --- GÖRÜNÜMLERİ BAĞLA ---
        tvDetailName = findViewById(R.id.tvDetailName);
        tvDetailSchool = findViewById(R.id.tvDetailSchool);
        tvProgressText = findViewById(R.id.tvProgressText);
        progressBarReading = findViewById(R.id.progressBarReading);

        // Rozetlerin içine dolacağı kutu
        layoutEarnedBadgesContainer = findViewById(R.id.layoutEarnedBadgesContainer);
        layoutBookListContainer = findViewById(R.id.layoutBookListContainer);

        btnAddDiary = findViewById(R.id.btnAddDiary);
        btnAddBook = findViewById(R.id.btnAddBook);
        btnMarkReceived = findViewById(R.id.btnMarkReceived); // XML'de eklemiştik

        btnBack = findViewById(R.id.btnBack);
        btnViewAllBadges = findViewById(R.id.btnViewAllBadges);

        // Verileri Yükle
        loadStudentDetails();

        // --- BUTON TIKLAMALARI ---
        btnBack.setOnClickListener(v -> finish());

        btnAddDiary.setOnClickListener(v -> {
            Intent intent = new Intent(this, ReadingDiaryActivity.class);
            intent.putExtra("studentId", studentId);
            startActivity(intent);
        });

        // "See All Collection" yazısına tıklayınca (İstersen boş bir sayfa açabilir veya Toast mesajı verebilirsin)
        btnViewAllBadges.setOnClickListener(v -> {
            Toast.makeText(this, "Badge Collection Page", Toast.LENGTH_SHORT).show();
        });

        btnAddBook.setOnClickListener(v -> showRequestDialog());

        // 🔥 TESLİM ALMA VE İYİLİK ROZETİ KAZANMA
        btnMarkReceived.setOnClickListener(v -> {
            db.collection("students").document(studentId)
                    .update("status", "Received")
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Book Received! Cycle Complete. 🎉", Toast.LENGTH_SHORT).show();

                        // "Kind Heart" Rozetini Ver
                        db.collection("students").document(studentId)
                                .update("badges", com.google.firebase.firestore.FieldValue.arrayUnion("Kind Heart"));
                    });
        });
    }

    private void loadStudentDetails() {
        if (studentId == null) return;

        // Anlık Dinleyici (SnapshotListener) sayesinde rozet gelince ekran hemen güncellenir
        db.collection("students").document(studentId).addSnapshotListener((doc, e) -> {
            if (e != null) return;

            if (doc != null && doc.exists()) {
                // 1. İsim ve Okul
                tvDetailName.setText(doc.getString("name"));
                tvDetailSchool.setText(doc.getString("schoolName"));

                // 2. İlerleme Çubuğu
                Long completed = doc.getLong("completedBooks");
                int completedCount = (completed != null) ? completed.intValue() : 0;
                progressBarReading.setProgress(completedCount);
                tvProgressText.setText(completedCount + " / 10 Books Read");

                // 3. 🔥 ROZETLERİ ÇEK VE GÖSTER (Sorun buradaydı, çözüldü)
                List<String> badges = new ArrayList<>();
                if (doc.get("badges") != null) {
                    badges = (List<String>) doc.get("badges");
                }
                updateBadgesUI(badges); // Listeyi UI metoduna gönder

                // 4. Kitap İhtiyacı Listesi
                loadLists(doc.getString("bookNeed"));

                // 5. Buton Yönetimi (Öğretmen mi Bağışçı mı?)
                String teacherId = doc.getString("teacherId");
                String status = doc.getString("status");
                manageTeacherButtons(teacherId, status);
            }
        });
    }

    // --- 🔥 ROZETLERİ GÖRSEL OLARAK OLUŞTURAN METOD ---
    private void updateBadgesUI(List<String> badges) {
        // Önce temizle ki üst üste binmesin
        layoutEarnedBadgesContainer.removeAllViews();

        if (badges == null || badges.isEmpty()) {
            // Hiç rozet yoksa boş geç
            return;
        }

        for (String badgeName : badges) {
            // 1. Ana Kart (Yatay)
            LinearLayout badgeCard = new LinearLayout(this);
            badgeCard.setOrientation(LinearLayout.HORIZONTAL);
            badgeCard.setPadding(16, 16, 16, 16);
            badgeCard.setBackgroundResource(R.drawable.input_border_red); // Kırmızı çerçeve

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 16); // Alt boşluk
            badgeCard.setLayoutParams(params);
            badgeCard.setGravity(android.view.Gravity.CENTER_VERTICAL);

            // 2. İKON SEÇİMİ VE AÇIKLAMA METNİ
            ImageView badgeIcon = new ImageView(this);
            int iconRes = android.R.drawable.btn_star_big_on; // Varsayılan
            String description = "";

            switch (badgeName) {
                case "Book Beginner":
                    iconRes = android.R.drawable.ic_menu_myplaces; // Kupa benzeri
                    description = "Congratulations on reading your very first book! Every great reader starts with one page.";
                    break;
                case "Reading Streak":
                    iconRes = android.R.drawable.ic_menu_agenda; // Takvim
                    description = "Awarded for consistent reading habits. You have demonstrated strong dedication!";
                    break;
                case "Diary Keeper":
                    iconRes = android.R.drawable.ic_menu_edit; // Defter
                    description = "You made your first entry! You are turning your reading into memories.";
                    break;
                case "Kind Heart":
                    iconRes = android.R.drawable.btn_star_big_on; // Kalp
                    description = "You sent your first Thank-You! Sharing joy makes someone's day brighter.";
                    break;
                case "Goal Achiever":
                    iconRes = android.R.drawable.ic_menu_compass; // Hedef
                    description = "You reached your reading goal! Proof of your focus and determination.";
                    break;
                case "Super Reader":
                    iconRes = android.R.drawable.ic_menu_view; // Süper
                    description = "You've read 10 books! Keep going, Super Reader!";
                    break;
            }

            badgeIcon.setImageResource(iconRes);

            // "Kind Heart" ise kırmızı yap
            if (badgeName.equals("Kind Heart")) {
                badgeIcon.setColorFilter(Color.RED);
            } else {
                badgeIcon.setColorFilter(null); // Diğerleri orijinal rengi
            }

            badgeIcon.setLayoutParams(new LinearLayout.LayoutParams(120, 120));

            // 3. METİNLER (Başlık ve Açıklama)
            LinearLayout textLayout = new LinearLayout(this);
            textLayout.setOrientation(LinearLayout.VERTICAL);
            textLayout.setPadding(24, 0, 0, 0); // İkonla yazı arası boşluk

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

            // 4. Parçaları Birleştir
            badgeCard.addView(badgeIcon);
            badgeCard.addView(textLayout);

            // 5. Ana Ekrana Ekle
            layoutEarnedBadgesContainer.addView(badgeCard);
        }
    }

    private void manageTeacherButtons(String teacherId, String status) {
        if (currentUserId != null && currentUserId.equals(teacherId)) {
            // Öğretmense butonları yönet
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
            // Bağışçı ise butonları gizle
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
}git