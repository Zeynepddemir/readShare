package msku.ceng.madlab.readshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class TeacherDashboardActivity extends AppCompatActivity {

    private TextView tvTotalStudents, tvDeliveryStatus;
    private ImageView btnLogout;
    private Button btnRegisterNew;
    private LinearLayout layoutEmptyState;

    private RecyclerView rvStudents;
    private StudentAdapter adapter;
    private List<Student> studentList;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String currentTeacherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_dashboard);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }
        currentTeacherId = auth.getCurrentUser().getUid();

        tvTotalStudents = findViewById(R.id.tvTotalStudents);
        tvDeliveryStatus = findViewById(R.id.tvDeliveryStatus);
        btnLogout = findViewById(R.id.tvLogout); // XML'de ID'si tvLogout olan ImageView
        btnRegisterNew = findViewById(R.id.btnRegisterNew);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);

        rvStudents = findViewById(R.id.rvStudents);
        rvStudents.setLayoutManager(new LinearLayoutManager(this));
        studentList = new ArrayList<>();
        adapter = new StudentAdapter(studentList, this);
        rvStudents.setAdapter(adapter);

        btnLogout.setOnClickListener(v -> {
            auth.signOut();
            Intent intent = new Intent(TeacherDashboardActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        btnRegisterNew.setOnClickListener(v -> {
            StudentRegisterFragment fragment = new StudentRegisterFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        loadMyStudents();
        checkIncomingDeliveries();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMyStudents();
        checkIncomingDeliveries();
    }

    private void checkIncomingDeliveries() {
        db.collection("students")
                .whereEqualTo("teacherId", currentTeacherId)
                .whereEqualTo("status", "Donated") // Bağışlanmış ama henüz teslim alınmamış
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int incomingCount = queryDocumentSnapshots.size();
                    if (incomingCount > 0) {
                        tvDeliveryStatus.setText(incomingCount + " Book(s) are on the way! 🚀");
                        tvDeliveryStatus.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
                    } else {
                        tvDeliveryStatus.setText("No pending deliveries.");
                        tvDeliveryStatus.setTextColor(getResources().getColor(android.R.color.darker_gray));
                    }
                });
    }

    public void loadMyStudents() {
        db.collection("students")
                .whereEqualTo("teacherId", currentTeacherId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    studentList.clear();

                    if (queryDocumentSnapshots.isEmpty()) {
                        // Liste boşsa "Empty State" göster
                        layoutEmptyState.setVisibility(View.VISIBLE);
                        rvStudents.setVisibility(View.GONE);
                        tvTotalStudents.setText("Total Students: 0");
                    } else {
                        layoutEmptyState.setVisibility(View.GONE);
                        rvStudents.setVisibility(View.VISIBLE);

                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            Student student = doc.toObject(Student.class);
                            if (student != null) {
                                student.setDocumentId(doc.getId());

                                studentList.add(student);
                            }
                        }
                        tvTotalStudents.setText("Total Students: " + studentList.size());
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error loading list: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}