package msku.ceng.madlab.readshare;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import msku.ceng.madlab.readshare.databinding.ActivityThankYouMessageBinding;

public class ThankYouMessageActivity extends AppCompatActivity {

    private ActivityThankYouMessageBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth auth; // Kimlik doğrulama aracı

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityThankYouMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance(); // Auth başlatıldı

        // Geri Tuşu
        binding.btnBack.setOnClickListener(v -> finish());

        // Gönder Butonu
        binding.btnSend.setOnClickListener(v -> {
            String messageContent = binding.etMessage.getText().toString();

            if (messageContent.isEmpty()) {
                Toast.makeText(this, "Please write a message first!", Toast.LENGTH_SHORT).show();
                return;
            }

            // --- DİNAMİK İSİM ALMA İŞLEMİ ---
            FirebaseUser currentUser = auth.getCurrentUser();

            if (currentUser != null) {
                // 1. Giriş yapan kullanıcının ID'sini al
                String userId = currentUser.getUid();

                // 2. Veritabanından bu kişinin ismini bul
                // NOT: Kullanıcı bilgilerinin "users" koleksiyonunda tutulduğunu varsayıyoruz.
                // Eğer "students" veya "teachers" diye ayrı tutuyorsan burayı güncellemelisin.
                db.collection("users").document(userId).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                // İsmi veritabanından çek
                                String currentUserName = documentSnapshot.getString("name");

                                // İsim boşsa (kaydedilmemişse) e-posta adresini veya varsayılanı kullan
                                if (currentUserName == null || currentUserName.isEmpty()) {
                                    currentUserName = currentUser.getEmail();
                                }

                                // 3. Mesajı bu dinamik isimle gönder
                                sendMessageToFirebase(currentUserName, messageContent);
                            } else {
                                // Kullanıcı veritabanında bulunamazsa (Fallback)
                                sendMessageToFirebase("Anonymous Student", messageContent);
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Failed to get user info: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });

            } else {
                Toast.makeText(this, "Error: User not logged in!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Mesajı Veritabanına Kaydetme Metodu
    private void sendMessageToFirebase(String sender, String content) {
        // Yeni Mesaj Nesnesi
        Message newMessage = new Message(sender, content, "Pending", Timestamp.now());

        // 'messages' koleksiyonuna ekle
        db.collection("messages")
                .add(newMessage)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Message sent successfully!", Toast.LENGTH_LONG).show();
                    binding.etMessage.setText(""); // Kutuyu temizle
                    finish(); // Ekranı kapat
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}