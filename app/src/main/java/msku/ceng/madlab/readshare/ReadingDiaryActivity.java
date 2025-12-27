package msku.ceng.madlab.readshare;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
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

    private FirebaseFirestore db;
    private String studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading_diary);

        db = FirebaseFirestore.getInstance();

        // Eğer ID gelmezse varsayılan bir ID kullan (Demo Modu)
        studentId = getIntent().getStringExtra("studentId");
        if (studentId == null) {
            studentId = "demo_student_user";
        }

        // Görünümleri Bağla
        etDate = findViewById(R.id.etDate);
        etBookName = findViewById(R.id.etBookName);
        etPage = findViewById(R.id.etPage);
        etNote = findViewById(R.id.etNote);
        btnSave = findViewById(R.id.btnSaveEntry);
        btnBack = findViewById(R.id.btnBack);
        tvTodayDate = findViewById(R.id.tvTodayDate);

        // Bugünü Tarihini Göster
        String today = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        tvTodayDate.setText(today);
        etDate.setText(today);

        btnBack.setOnClickListener(v -> finish());

        // KAYDET BUTONU
        btnSave.setOnClickListener(v -> saveDiaryEntry());
    }

    private void saveDiaryEntry() {
        String date = etDate.getText().toString();
        String book = etBookName.getText().toString();
        String page = etPage.getText().toString();
        String note = etNote.getText().toString();

        if (book.isEmpty() || page.isEmpty()) {
            Toast.makeText(this, "Please fill Book Name and Page!", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> entry = new HashMap<>();
        entry.put("date", date);
        entry.put("bookName", book);
        entry.put("pageCount", page);
        entry.put("note", note);
        entry.put("timestamp", com.google.firebase.Timestamp.now());

        // Veriyi 'reading_diaries' diye genel bir yere veya öğrencinin altına kaydedelim
        // Giriş sistemi olmadığı için doğrudan bir koleksiyona atıyoruz ki görünsün
        db.collection("students").document(studentId).collection("diary")
                .add(entry)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Diary Entry Saved! 📝", Toast.LENGTH_SHORT).show();

                    // Alanları Temizle
                    etPage.setText("");
                    etNote.setText("");
                    etBookName.setText("");
                    etBookName.requestFocus();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}