package msku.ceng.madlab.readshare;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import msku.ceng.madlab.readshare.databinding.ActivityDonorDiscoveryBinding;

public class DonorDiscoveryActivity extends AppCompatActivity {

    private ActivityDonorDiscoveryBinding binding;
    private FirebaseFirestore db;
    private List<Student> allStudentsList = new ArrayList<>();
    private List<Student> filteredList = new ArrayList<>();
    private StudentAdapter adapter;
    private String currentSearchText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDonorDiscoveryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        binding.rvStudentList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StudentAdapter(filteredList, this);
        binding.rvStudentList.setAdapter(adapter);

        fetchStudentsFromFirebase();

        binding.iconNotification.setOnClickListener(v -> {
            NotificationSheetFragment bottomSheet = new NotificationSheetFragment();
            bottomSheet.show(getSupportFragmentManager(), "donorNotificationTag");
        });

        binding.etSearchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchText = s.toString().trim();
                filterList();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        setupBottomNav();
    }

    private void fetchStudentsFromFirebase() {
        db.collection("students")
                .whereEqualTo("status", "Waiting")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    allStudentsList.clear();
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        Student student = snapshot.toObject(Student.class);
                        if (student != null) {
                            student.setDocumentId(snapshot.getId());
                            allStudentsList.add(student);
                        }
                    }
                    filterList();
                });
    }

    private void filterList() {
        filteredList.clear();
        Locale locale = Locale.forLanguageTag("tr");
        String searchLower = currentSearchText.toLowerCase(locale);

        for (Student student : allStudentsList) {
            String name = (student.getName() != null) ? student.getName().toLowerCase(locale) : "";
            String book = (student.getBookNeed() != null) ? student.getBookNeed().toLowerCase(locale) : "";

            boolean matchesSearch = searchLower.isEmpty() || name.contains(searchLower) || book.contains(searchLower);

            if (matchesSearch) {
                filteredList.add(student);
            }
        }

        if (filteredList.isEmpty()) {
            binding.layoutEmptyState.setVisibility(View.VISIBLE);
            binding.rvStudentList.setVisibility(View.GONE);
        } else {
            binding.layoutEmptyState.setVisibility(View.GONE);
            binding.rvStudentList.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    }

    private void setupBottomNav() {
        binding.navHome.setOnClickListener(v -> fetchStudentsFromFirebase());

        binding.navSearch.setOnClickListener(v -> {
            binding.etSearchBox.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.showSoftInput(binding.etSearchBox, InputMethodManager.SHOW_IMPLICIT);
        });

        binding.navList.setOnClickListener(v -> {
            startActivity(new Intent(this, DeliveryTrackingActivity.class));
        });

        binding.navProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, DonorProfileActivity.class));
        });
    }
}