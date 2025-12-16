package msku.ceng.madlab.readshare;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import msku.ceng.madlab.readshare.databinding.ActivityStudentDetailBinding;

public class StudentDetailActivity extends AppCompatActivity {

    private ActivityStudentDetailBinding binding;
    private FirebaseFirestore db;
    private String studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudentDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        // 1. DASHBOARD'DAN GELEN VERİLER
        String name = getIntent().getStringExtra("name");
        String school = getIntent().getStringExtra("school");

        // ID kontrolü (Dashboard'dan gelen ID veya İsim)
        String idFromIntent = getIntent().getStringExtra("studentId");
        studentId = (idFromIntent != null) ? idFromIntent : name;

        // Ekrana Yaz
        binding.tvDetailName.setText(name != null ? name : "Student");
        binding.tvDetailSchool.setText(school != null ? school : "School");

        // 2. LİSTEYİ GETİR
        fetchReadingDiary();

        // 3. BUTONLAR
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnAddBook.setOnClickListener(v -> showAddBookDialog());

        // "What are these?" Linki -> Katalog Sayfasına Gider
        binding.btnViewAllBadges.setOnClickListener(v -> {
            startActivity(new Intent(StudentDetailActivity.this, BadgeActivity.class));
        });
    }

    // --- KİTAPLARI ÇEK VE ROZETLERİ GÜNCELLE ---
    private void fetchReadingDiary() {
        binding.layoutBookListContainer.removeAllViews();

        db.collection("students").document(studentId).collection("diary")
                .orderBy("addedDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    int totalBooks = queryDocumentSnapshots.size();
                    binding.progressBarReading.setProgress(totalBooks);
                    binding.tvProgressText.setText(totalBooks + " / 10 Books Read");

                    // *** KAZANILAN ROZETLERİ GÖSTER ***
                    updateEarnedBadgesArea(totalBooks);

                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                            addBookRow(snapshot.getString("title"), snapshot.getString("pages"), snapshot.getString("status"));
                        }
                    } else {
                        Toast.makeText(this, "No books yet. Add one!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // --- KAZANILAN ROZETLERİ EKRANA BASAN METOD ---
    private void updateEarnedBadgesArea(int bookCount) {
        binding.layoutEarnedBadgesContainer.removeAllViews();

        if (bookCount == 0) {
            TextView t = new TextView(this);
            t.setText("Start reading to earn badges!");
            t.setTextSize(12);
            binding.layoutEarnedBadgesContainer.addView(t);
            return;
        }

        // Kural: 1+ Kitap
        if (bookCount >= 1) addBadgeIcon(android.R.drawable.btn_star_big_on, "#FFC107", "Beginner");
        // Kural: 5+ Kitap
        if (bookCount >= 5) addBadgeIcon(android.R.drawable.ic_menu_my_calendar, "#4CAF50", "Streak");
        // Kural: 10+ Kitap
        if (bookCount >= 10) addBadgeIcon(android.R.drawable.btn_star_big_on, "#FF9800", "Super Reader");
        // Kural: 20+ Kitap
        if (bookCount >= 20) addBadgeIcon(android.R.drawable.ic_menu_compass, "#9C27B0", "Master");
    }

    private void addBadgeIcon(int iconResId, String colorHex, String label) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(android.view.Gravity.CENTER);
        layout.setPadding(0, 0, 32, 0);

        ImageView icon = new ImageView(this);
        icon.setImageResource(iconResId);
        icon.setColorFilter(Color.parseColor(colorHex));
        icon.setLayoutParams(new LinearLayout.LayoutParams(100, 100));

        TextView text = new TextView(this);
        text.setText(label);
        text.setTextSize(10);
        text.setTextColor(Color.BLACK);
        text.setGravity(android.view.Gravity.CENTER);

        layout.addView(icon);
        layout.addView(text);
        binding.layoutEarnedBadgesContainer.addView(layout);
    }

    // --- KİTAP SATIRI EKLEME ---
    private void addBookRow(String title, String pages, String status) {
        View bookView = LayoutInflater.from(this).inflate(R.layout.item_library_book, binding.layoutBookListContainer, false);

        TextView tvTitle = bookView.findViewById(R.id.tvBookTitle);
        TextView tvPages = bookView.findViewById(R.id.tvBookPages);
        TextView btnFinished = bookView.findViewById(R.id.btnStatusFinished);
        TextView btnInProgress = bookView.findViewById(R.id.btnStatusInProgress);
        TextView btnNotStarted = bookView.findViewById(R.id.btnStatusNotStarted);

        tvTitle.setText(title);
        tvPages.setText("Page: " + pages);

        if (status != null) {
            if (status.equals("Finished")) {
                btnFinished.setBackgroundColor(Color.parseColor("#4CAF50"));
                btnFinished.setTextColor(Color.WHITE);
            } else if (status.equals("In Progress")) {
                btnInProgress.setBackgroundColor(Color.parseColor("#FFC107"));
                btnInProgress.setTextColor(Color.BLACK);
            } else {
                btnNotStarted.setBackgroundColor(Color.LTGRAY);
            }
        }
        binding.layoutBookListContainer.addView(bookView);
    }

    // --- KİTAP EKLEME PENCERESİ ---
    private void showAddBookDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Book");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        final EditText inputTitle = new EditText(this);
        inputTitle.setHint("Book Title");
        layout.addView(inputTitle);

        final EditText inputPages = new EditText(this);
        inputPages.setHint("Total Pages");
        inputPages.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        layout.addView(inputPages);

        builder.setView(layout);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String title = inputTitle.getText().toString();
            String pages = inputPages.getText().toString();
            if (!title.isEmpty() && !pages.isEmpty()) {
                saveBookToFirebase(title, pages);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void saveBookToFirebase(String title, String pages) {
        Map<String, Object> bookMap = new HashMap<>();
        bookMap.put("title", title);
        bookMap.put("pages", pages);
        bookMap.put("status", "Finished");
        bookMap.put("addedDate", Timestamp.now());

        db.collection("students").document(studentId).collection("diary")
                .add(bookMap)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Book Added!", Toast.LENGTH_SHORT).show();
                    fetchReadingDiary();
                });
    }
}