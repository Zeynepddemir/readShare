package msku.ceng.madlab.readshare;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import msku.ceng.madlab.readshare.databinding.ActivityMyLibraryBinding;

public class MyLibraryActivity extends AppCompatActivity {

    private ActivityMyLibraryBinding binding;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyLibraryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance(); // Bağlantıyı kur

        binding.btnBack.setOnClickListener(v -> finish());

        // FIRESTORE'DAN KİTAPLARI ÇEK
        fetchBooksFromFirebase();
    }

    private void fetchBooksFromFirebase() {
        // "books" koleksiyonundaki her şeyi getir
        db.collection("books")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Eğer liste boş değilse
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                            // Gelen veriyi Book nesnesine çevir
                            Book book = snapshot.toObject(Book.class);
                            if (book != null) {
                                addBookCard(book);
                            }
                        }
                    } else {
                        Toast.makeText(this, "No books found in database.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading books: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void addBookCard(Book book) {
        // Şablonu şişir
        View bookView = LayoutInflater.from(this).inflate(R.layout.item_library_book, binding.layoutBookContainer, false);

        TextView tvTitle = bookView.findViewById(R.id.tvBookTitle);
        TextView tvPages = bookView.findViewById(R.id.tvBookPages);
        TextView btnNotStarted = bookView.findViewById(R.id.btnStatusNotStarted);
        TextView btnInProgress = bookView.findViewById(R.id.btnStatusInProgress);
        TextView btnFinished = bookView.findViewById(R.id.btnStatusFinished);

        tvTitle.setText("Title: " + book.getTitle());
        tvPages.setText("Page: " + book.getPageCount());

        // Renklendirme
        String status = book.getStatus();
        if (status != null && status.equals("Finished")) {
            btnFinished.setBackgroundColor(Color.parseColor("#FF6B6B"));
            btnFinished.setTextColor(Color.WHITE);
        } else if (status != null && status.equals("In Progress")) {
            btnInProgress.setBackgroundColor(Color.parseColor("#FF6B6B"));
            btnInProgress.setTextColor(Color.WHITE);
        } else {
            btnNotStarted.setBackgroundColor(Color.parseColor("#FF6B6B"));
            btnNotStarted.setTextColor(Color.WHITE);
        }

        binding.layoutBookContainer.addView(bookView);
    }
}