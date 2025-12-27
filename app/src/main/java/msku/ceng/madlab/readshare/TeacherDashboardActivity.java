package msku.ceng.madlab.readshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import msku.ceng.madlab.readshare.databinding.ActivityTeacherDashboardBinding;

public class TeacherDashboardActivity extends AppCompatActivity {

    private ActivityTeacherDashboardBinding binding;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTeacherDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        // 1. Öğrencileri Listele
        fetchMyStudents();

        // 2. İstatistikleri ve Bildirimleri Güncelle (CANLI VERİ)
        fetchDashboardStats();

        // --- BUTON İŞLEMLERİ ---

        // Yeni Öğrenci Kaydetme
        binding.btnRegisterNew.setOnClickListener(v -> {
            StudentRegisterFragment fragment = new StudentRegisterFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(android.R.id.content, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        // Çıkış Yap
        binding.tvLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(TeacherDashboardActivity.this, MainActivity.class));
            finish();
        });

        // Mesaj Kutusu (Sağ Üst)
        binding.btnMessages.setOnClickListener(v -> {
            startActivity(new Intent(TeacherDashboardActivity.this, ApproveMessagesActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Sayfaya geri dönüldüğünde verileri yenile
        fetchMyStudents();
        fetchDashboardStats();
    }

    // --- BİLDİRİM SAYILARINI GÜNCELLEYEN FONKSİYON ---
    private void fetchDashboardStats() {
        // Bekleyen (Pending) mesaj sayısını bul
        db.collection("messages")
                .whereEqualTo("status", "Pending")
                .get()
                .addOnSuccessListener(snapshots -> {
                    int pendingCount = snapshots.size();

                    // XML'deki tvNotifMessage alanına yaz
                    // "0 Thank-you messages waiting" yazısını güncelliyoruz
                    if (binding.tvNotifMessage != null) {
                        binding.tvNotifMessage.setText(pendingCount + " Thank-you messages waiting");
                    }
                })
                .addOnFailureListener(e -> {
                    // Hata olursa (örneğin internet yoksa) 0 varsayalım veya loglayalım
                });
    }

    private void fetchMyStudents() {
        binding.layoutStudentCardsContainer.removeAllViews();

        db.collection("students").get().addOnSuccessListener(snapshots -> {
            int total = snapshots.size();
            binding.tvTotalStudents.setText("Total Students: " + total);

            for (DocumentSnapshot snap : snapshots) {
                Student s = snap.toObject(Student.class);
                if (s != null) {
                    s.setStudentId(snap.getId());
                    addStudentCard(s);
                }
            }
        });
    }

    private void addStudentCard(Student student) {
        View card = LayoutInflater.from(this).inflate(R.layout.item_dashboard_student, binding.layoutStudentCardsContainer, false);

        TextView name = card.findViewById(R.id.tvCardName);
        TextView grade = card.findViewById(R.id.tvCardGrade);
        TextView school = card.findViewById(R.id.tvCardSchool);

        name.setText(student.getName() != null ? student.getName() : "No Name");
        grade.setText(student.getAge() != null ? student.getAge() : "-");
        school.setText(student.getSchool() != null ? student.getSchool() : "-");

        // Karta tıklayınca profile git
        card.setOnClickListener(v -> {
            Intent intent = new Intent(TeacherDashboardActivity.this, StudentDetailActivity.class);
            intent.putExtra("name", student.getName());
            intent.putExtra("school", student.getSchool());
            intent.putExtra("studentId", student.getStudentId());
            startActivity(intent);
        });

        binding.layoutStudentCardsContainer.addView(card);
    }
}