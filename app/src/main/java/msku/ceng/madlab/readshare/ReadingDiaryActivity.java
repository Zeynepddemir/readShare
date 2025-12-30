package msku.ceng.madlab.readshare;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ReadingDiaryActivity extends AppCompatActivity {

    private EditText etDate, etBookName, etPage, etNote;
    private Button btnSave;
    private ImageView btnBack;
    private TextView tvTodayDate;
    private TableLayout tableDiary;

    private FirebaseFirestore db;
    private String studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading_diary);

        db = FirebaseFirestore.getInstance();

        // Öğrenci ID Kontrolü
        studentId = getIntent().getStringExtra("studentId");
        if (studentId == null) {
            Toast.makeText(this, "Student ID not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Görünümleri Bağla
        etDate = findViewById(R.id.etDate);
        etBookName = findViewById(R.id.etBookName);
        etPage = findViewById(R.id.etPage);
        etNote = findViewById(R.id.etNote);
        btnSave = findViewById(R.id.btnSaveEntry);
        btnBack = findViewById(R.id.btnBack);
        tvTodayDate = findViewById(R.id.tvTodayDate);
        tableDiary = findViewById(R.id.tableDiary);

        // Tarih Ayarı
        String today = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        if (tvTodayDate != null) tvTodayDate.setText(today);
        if (etDate != null) etDate.setText(today);

        btnBack.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveDiaryEntry());

        loadDiaryEntries();
    }

    private void saveDiaryEntry() {
        String book = etBookName.getText().toString().trim();
        String page = etPage.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String note = etNote.getText().toString().trim();

        if (book.isEmpty() || page.isEmpty()) {
            Toast.makeText(this, "Please enter book name and pages!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Günlük Verisi Hazırla
        Map<String, Object> entry = new HashMap<>();
        entry.put("date", date);
        entry.put("bookName", book);
        entry.put("pageCount", page);
        entry.put("note", note);
        entry.put("timestamp", Timestamp.now());

        // 2. Günlüğe Ekle
        db.collection("students").document(studentId).collection("diary")
                .add(entry)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Diary Entry Saved! 📝", Toast.LENGTH_SHORT).show();

                    // 3. İstatistikleri Güncelle ve Rozet Kontrolü Yap
                    updateStatsAndCheckBadges();

                    // Formu Temizle
                    etBookName.setText("");
                    etPage.setText("");
                    etNote.setText("");
                    loadDiaryEntries();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // ReadingDiaryActivity.java içinde bu metodu güncelle:

    private void updateStatsAndCheckBadges() {
        // 1. Kitap sayısını artır
        db.collection("students").document(studentId)
                .update("completedBooks", FieldValue.increment(1));

        // 2. Rozet Kontrolü
        db.collection("students").document(studentId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Long currentCountLong = documentSnapshot.getLong("completedBooks");
                        int currentCount = (currentCountLong != null) ? currentCountLong.intValue() : 0;

                        // Yeni toplam sayı
                        int newTotal = currentCount + 1;

                        // --- GÖRSELE GÖRE YENİ ROZET KURALLARI ---

                        // 1. Kitap: Hem "Başlangıç" hem "Günlük Tutucu" rozeti
                        if (newTotal == 1) {
                            awardBadge("Book Beginner");
                            awardBadge("Diary Keeper");
                        }

                        // 3. Kitap: İstikrar Rozeti (Reading Streak)
                        if (newTotal == 3) {
                            awardBadge("Reading Streak");
                        }

                        // 5. Kitap: Hedef Rozeti (Goal Achiever)
                        if (newTotal == 5) {
                            awardBadge("Goal Achiever");
                        }

                        // 10. Kitap: Süper Okuyucu (Super Reader)
                        if (newTotal == 10) {
                            awardBadge("Super Reader");
                        }
                    }
                });
    }

    private void awardBadge(String badgeName) {
        // Rozeti listeye ekle (arrayUnion: aynısından varsa tekrar eklemez)
        db.collection("students").document(studentId)
                .update("badges", FieldValue.arrayUnion(badgeName))
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "🏆 NEW BADGE UNLOCKED: " + badgeName, Toast.LENGTH_LONG).show();
                });
    }

    private void loadDiaryEntries() {
        db.collection("students").document(studentId).collection("diary")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (tableDiary != null) {
                        int childCount = tableDiary.getChildCount();
                        if (childCount > 1) {
                            tableDiary.removeViews(1, childCount - 1);
                        }
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            addTableRow(
                                    doc.getString("date"),
                                    doc.getString("bookName"),
                                    doc.getString("pageCount"),
                                    doc.getString("note")
                            );
                        }
                    }
                });
    }

    private void addTableRow(String date, String book, String page, String note) {
        TableRow row = new TableRow(this);
        row.setPadding(0, 8, 0, 8);

        row.addView(createTextView(date));
        row.addView(createTextView(book));
        row.addView(createTextView(page));
        row.addView(createTextView(note));

        tableDiary.addView(row);
    }

    private TextView createTextView(String text) {
        TextView tv = new TextView(this);
        tv.setText(text != null ? text : "-");
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(8, 8, 8, 8);
        tv.setTextColor(Color.BLACK);
        // Arka plan çizgisi (varsa)
        // tv.setBackgroundResource(R.drawable.cell_border);
        return tv;
    }
}