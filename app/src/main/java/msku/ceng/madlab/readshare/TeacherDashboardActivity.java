package msku.ceng.madlab.readshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
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

        fetchMyStudents();

        // Butonlar
        binding.btnRegisterNew.setOnClickListener(v -> {
            StudentRegisterFragment fragment = new StudentRegisterFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(android.R.id.content, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        binding.tvLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(TeacherDashboardActivity.this, MainActivity.class));
            finish();
        });

        // Mesaj Onay Ekranı (Bildirime Tıklayınca)
        binding.tvNotifMessage.setOnClickListener(v -> {
            startActivity(new Intent(TeacherDashboardActivity.this, MessageApprovalActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchMyStudents();
    }

    private void fetchMyStudents() {
        binding.layoutStudentCardsContainer.removeAllViews();
        db.collection("students").get().addOnSuccessListener(snapshots -> {

            // İstatistikler (Basit Hesaplama)
            int total = snapshots.size();
            binding.tvTotalStudents.setText("Total Students: " + total);
            binding.tvTotalDonated.setText("Books Donated: " + (total * 3));
            binding.tvTotalMessages.setText("Messages Approved: " + (total * 2));
            if(total > 0) binding.tvNotifDiary.setText("📖 " + total + " Students active");

            // Kartları Ekle
            for (DocumentSnapshot snap : snapshots) {
                Student s = snap.toObject(Student.class);
                if (s != null) {
                    s.setStudentId(snap.getId()); // ID'yi kaydet
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

        name.setText(student.getName());
        grade.setText(student.getAge());
        school.setText(student.getSchool());

        // DETAY SAYFASINA GİT (ID ve İsim ile)
        card.setOnClickListener(v -> {
            Intent intent = new Intent(TeacherDashboardActivity.this, StudentDetailActivity.class);
            intent.putExtra("name", student.getName());
            intent.putExtra("school", student.getSchool());
            // ÖNEMLİ: ID'yi gönderiyoruz (Eğer null ise ismi kullanır Detail tarafı)
            intent.putExtra("studentId", student.getStudentId() != null ? student.getStudentId() : student.getName());
            startActivity(intent);
        });

        binding.layoutStudentCardsContainer.addView(card);
    }
}