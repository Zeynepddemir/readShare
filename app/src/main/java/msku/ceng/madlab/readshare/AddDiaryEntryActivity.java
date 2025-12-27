package msku.ceng.madlab.readshare;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class AddDiaryEntryActivity extends AppCompatActivity {

    private ImageView btnBack;
    private EditText etDate, etBookName, etPage, etNote;
    private Button btnSaveEntry;
    private FirebaseFirestore db;
    private String currentStudentId = "demo_student_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_diary_entry); // XML İSMİNE DİKKAT

        db = FirebaseFirestore.getInstance();
        if(getIntent().getStringExtra("studentId") != null) {
            currentStudentId = getIntent().getStringExtra("studentId");
        }

        initViews();

        btnBack.setOnClickListener(v -> finish());
        btnSaveEntry.setOnClickListener(v -> saveEntry());
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        etDate = findViewById(R.id.etDate);
        etBookName = findViewById(R.id.etBookName);
        etPage = findViewById(R.id.etPage);
        etNote = findViewById(R.id.etNote);
        btnSaveEntry = findViewById(R.id.btnSaveEntry);
    }

    private void saveEntry() {
        String date = etDate.getText().toString().trim();
        String bookName = etBookName.getText().toString().trim();
        String page = etPage.getText().toString().trim();
        String note = etNote.getText().toString().trim();

        if (TextUtils.isEmpty(bookName)) {
            etBookName.setError("Book name is required!");
            return;
        }

        // --- MODEL KULLANARAK KAYIT ---
        // Veriyi modele çeviriyoruz, Firebase otomatik eşleştirecek
        DiaryEntry newEntry = new DiaryEntry(
                date.isEmpty() ? "No Date" : date,
                bookName,
                page,
                note
        );

        // Ekstra zaman damgası eklemek istersen Map kullanabilirsin ama şimdilik model yeterli.
        // Sıralama için 'addedDate' alanını manuel ekleyelim:
        Map<String, Object> data = new HashMap<>();
        data.put("date", newEntry.getDate());
        data.put("title", newEntry.getTitle());
        data.put("pages", newEntry.getPages());
        data.put("note", newEntry.getNote());
        data.put("addedDate", Timestamp.now()); // Sıralama için

        db.collection("students").document(currentStudentId).collection("diary")
                .add(data)
                .addOnSuccessListener(ref -> {
                    Toast.makeText(this, "Saved! 📔", Toast.LENGTH_SHORT).show();
                    finish(); // Listeye geri dön
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show());
    }
}