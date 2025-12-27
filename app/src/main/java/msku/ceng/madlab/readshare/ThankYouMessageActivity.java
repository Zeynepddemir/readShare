package msku.ceng.madlab.readshare;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class ThankYouMessageActivity extends AppCompatActivity {

    private EditText etMessage;
    private Button btnSend;
    private ImageView btnBack;
    private FirebaseFirestore db;

    // Normalde bu bilgiler giriş yapan kullanıcıdan gelir
    // Şimdilik test için sabit veriyoruz
    private String studentId = "Ali Kaya";
    private String studentName = "Ali Kaya";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank_you_message);

        db = FirebaseFirestore.getInstance();

        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        btnSend.setOnClickListener(v -> {
            String messageText = etMessage.getText().toString().trim();
            if (!messageText.isEmpty()) {
                sendMessage(messageText);
            } else {
                Toast.makeText(this, "Please write a message first!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage(String message) {
        // Mesaj Veri Paketi
        Map<String, Object> msgMap = new HashMap<>();
        msgMap.put("content", message);
        msgMap.put("studentId", studentId);
        msgMap.put("studentName", studentName);
        msgMap.put("status", "Pending"); // Öğretmen onayı bekliyor
        msgMap.put("date", Timestamp.now());

        // 'messages' koleksiyonuna ekle
        db.collection("messages")
                .add(msgMap)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Message sent to teacher for approval! 📝", Toast.LENGTH_LONG).show();
                    finish(); // Sayfayı kapat
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}