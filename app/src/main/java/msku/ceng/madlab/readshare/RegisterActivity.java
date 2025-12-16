package msku.ceng.madlab.readshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import msku.ceng.madlab.readshare.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private FirebaseAuth auth;       // Giriş yetkilisi
    private FirebaseFirestore db;    // Veritabanı yetkilisi

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Firebase araçlarını başlat
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // KAYIT OL BUTONU
        binding.btnSignUp.setOnClickListener(v -> {
            String email = binding.etRegisterEmail.getText().toString().trim();
            String password = binding.etRegisterPassword.getText().toString().trim();
            String name = binding.etName.getText().toString().trim();

            // 1. Alanlar boş mu kontrol et
            if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // 2. Hangi rol seçili?
            String selectedRole = "";
            if (binding.rbTeacher.isChecked()) {
                selectedRole = "Teacher";
            } else if (binding.rbDonor.isChecked()) {
                selectedRole = "Donor";
            } else {
                Toast.makeText(this, "Please select a role (Teacher or Donor)", Toast.LENGTH_SHORT).show();
                return;
            }

            // Bu değişkeni lambda içinde kullanabilmek için 'final' gibi davranmalı,
            // o yüzden string kopyası oluşturuyoruz.
            String finalRole = selectedRole;

            // 3. Firebase Authentication ile Kullanıcı Oluştur
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {
                        // Giriş başarılı, şimdi rolünü veritabanına kaydedelim
                        String userId = auth.getCurrentUser().getUid();

                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("name", name);
                        userMap.put("email", email);
                        userMap.put("role", finalRole); // "Teacher" veya "Donor"

                        db.collection("users").document(userId)
                                .set(userMap)
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                                    // Giriş ekranına gönder
                                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Error saving user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });

                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Registration Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        // Giriş sayfasına dön
        binding.tvLoginPrompt.setOnClickListener(v -> finish());
    }
}