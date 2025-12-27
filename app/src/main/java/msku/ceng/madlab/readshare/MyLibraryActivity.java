package msku.ceng.madlab.readshare;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import msku.ceng.madlab.readshare.databinding.ActivityMyLibraryBinding;

public class MyLibraryActivity extends AppCompatActivity {

    private ActivityMyLibraryBinding binding;
    private FirebaseFirestore db;
    // Test için sabit ID, giriş sistemi tamamsa Intent'ten almalısın
    private String currentStudentId = "demo_student_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyLibraryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        // Eğer bir önceki sayfadan ID geldiyse onu kullan
        if (getIntent().getStringExtra("studentId") != null) {
            currentStudentId = getIntent().getStringExtra("studentId");
        }

        binding.btnBack.setOnClickListener(v -> finish());

        // Kütüphaneyi Yükle
        fetchLibraryBooks();
    }

    private void fetchLibraryBooks() {
        binding.layoutBookContainer.removeAllViews(); // Listeyi temizle

        // "students -> ID -> library" koleksiyonundan çekiyoruz
        db.collection("students").document(currentStudentId).collection("library")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                            // Kitap verisini ve ID'sini al
                            Book book = snapshot.toObject(Book.class);
                            String docId = snapshot.getId(); // Güncelleme için lazım

                            if (book != null) {
                                addBookCard(book, docId);
                            }
                        }
                    } else {
                        // Kütüphane boşsa
                        TextView emptyText = new TextView(this);
                        emptyText.setText("No books in your library yet.");
                        emptyText.setPadding(32, 32, 32, 32);
                        binding.layoutBookContainer.addView(emptyText);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void addBookCard(Book book, String docId) {
        // item_library_book.xml tasarımını şişir
        View bookView = LayoutInflater.from(this).inflate(R.layout.item_library_book, binding.layoutBookContainer, false);

        TextView tvTitle = bookView.findViewById(R.id.tvBookTitle);
        TextView tvPages = bookView.findViewById(R.id.tvBookPages);

        // Durum Butonları
        TextView btnNotStarted = bookView.findViewById(R.id.btnStatusNotStarted);
        TextView btnInProgress = bookView.findViewById(R.id.btnStatusInProgress);
        TextView btnFinished = bookView.findViewById(R.id.btnStatusFinished);

        tvTitle.setText(book.getTitle());
        tvPages.setText("Pages: " + book.getPageCount());

        // --- MEVCUT DURUMA GÖRE RENKLENDİRME ---
        updateButtonColors(book.getStatus(), btnNotStarted, btnInProgress, btnFinished);

        // --- TIKLAMA OLAYLARI (GÜNCELLEME) ---

        // 1. Not Started
        btnNotStarted.setOnClickListener(v -> {
            updateBookStatus(docId, "Not Started", btnNotStarted, btnInProgress, btnFinished);
        });

        // 2. In Progress
        btnInProgress.setOnClickListener(v -> {
            updateBookStatus(docId, "In Progress", btnNotStarted, btnInProgress, btnFinished);
        });

        // 3. Finished
        btnFinished.setOnClickListener(v -> {
            updateBookStatus(docId, "Finished", btnNotStarted, btnInProgress, btnFinished);
        });

        binding.layoutBookContainer.addView(bookView);
    }

    // Firebase'de durumu güncelleyen fonksiyon
    private void updateBookStatus(String docId, String newStatus, TextView btn1, TextView btn2, TextView btn3) {
        db.collection("students").document(currentStudentId).collection("library")
                .document(docId)
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Status updated: " + newStatus, Toast.LENGTH_SHORT).show();
                    // Görünümü hemen güncelle (Tekrar yüklemeye gerek yok)
                    updateButtonColors(newStatus, btn1, btn2, btn3);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update", Toast.LENGTH_SHORT).show());
    }

    // Buton renklerini ayarlayan yardımcı fonksiyon
    private void updateButtonColors(String status, TextView btnNotStarted, TextView btnInProgress, TextView btnFinished) {
        // Önce hepsini gri yap (Pasif)
        int colorPassive = Color.parseColor("#EEEEEE");
        int colorTextPassive = Color.BLACK;

        btnNotStarted.setBackgroundColor(colorPassive);
        btnNotStarted.setTextColor(colorTextPassive);

        btnInProgress.setBackgroundColor(colorPassive);
        btnInProgress.setTextColor(colorTextPassive);

        btnFinished.setBackgroundColor(colorPassive);
        btnFinished.setTextColor(colorTextPassive);

        // Seçili olanı Renkli Yap (Aktif)
        int colorActive = Color.parseColor("#FF6B6B"); // ReadShare Kırmızısı
        int colorTextActive = Color.WHITE;

        if (status == null) return;

        switch (status) {
            case "Not Started":
                btnNotStarted.setBackgroundColor(colorActive);
                btnNotStarted.setTextColor(colorTextActive);
                break;
            case "In Progress":
                btnInProgress.setBackgroundColor(colorActive);
                btnInProgress.setTextColor(colorTextActive);
                break;
            case "Finished":
                btnFinished.setBackgroundColor(colorActive);
                btnFinished.setTextColor(colorTextActive);
                break;
        }
    }
}