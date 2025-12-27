package msku.ceng.madlab.readshare;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import msku.ceng.madlab.readshare.databinding.ActivityDonorDiscoveryBinding;

public class DonorDiscoveryActivity extends AppCompatActivity {

    private ActivityDonorDiscoveryBinding binding;
    private FirebaseFirestore db;

    // Verileri hafızada tutuyoruz
    private List<Student> allStudentsList = new ArrayList<>();

    // Mevcut durumlar
    private String currentTab = "Personal";
    private String currentSearchText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDonorDiscoveryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        // 1. Verileri Çek
        fetchStudentsFromFirebase();

        // --- ARAMA KUTUSU DİNLEYİCİSİ (YAZARKEN SÜZME) ---
        binding.etSearchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchText = s.toString().trim();
                filterList(); // Her harfte listeyi güncelle
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // --- KLAVYEDEKİ "ARA" TUŞUNA BASINCA KLAVYEYİ KAPAT ---
        binding.etSearchBox.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                // Klavyeyi gizle
                View view = this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                // Kutudan odağı kaldır (imleç yanıp sönmesin)
                binding.etSearchBox.clearFocus();
                return true;
            }
            return false;
        });

        // --- SEKME (TAB) TIKLAMALARI ---
        binding.btnTabPersonal.setOnClickListener(v -> {
            updateTabs("Personal");
        });

        binding.btnTabClassroom.setOnClickListener(v -> {
            updateTabs("Classroom");
        });

        // Alt Menü Navigasyonu
        setupBottomNav();
    }

    private void fetchStudentsFromFirebase() {
        db.collection("students")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    allStudentsList.clear();
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                            Student student = snapshot.toObject(Student.class);
                            if (student != null) {
                                allStudentsList.add(student);
                            }
                        }
                        // Veriler geldi, listeyi göster
                        filterList();
                    } else {
                        Toast.makeText(this, "No requests found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // --- AKILLI FİLTRELEME FONKSİYONU ---
    private void filterList() {
        binding.layoutCardsContainer.removeAllViews(); // Ekranı temizle

        if (allStudentsList == null || allStudentsList.isEmpty()) return;

        boolean foundAny = false;

        // Türkçe karakter uyumlu küçültme (İ -> i)
        Locale trLocale = Locale.forLanguageTag("tr");
        String searchTextLower = currentSearchText.toLowerCase(trLocale);

        for (Student student : allStudentsList) {
            // Null kontrolü ve String hazırlığı
            String name = (student.getName() != null) ? student.getName() : "";
            String city = (student.getCity() != null) ? student.getCity() : "";
            String need = (student.getBookNeed() != null) ? student.getBookNeed() : "";

            String nameLower = name.toLowerCase(trLocale);
            String cityLower = city.toLowerCase(trLocale);
            String needLower = need.toLowerCase(trLocale);

            // 1. SEKME KONTROLÜ
            boolean isClassroom = false;
            if (nameLower.contains("sınıf") || nameLower.contains("class") || nameLower.contains("grade") || nameLower.contains("şube")) {
                isClassroom = true;
            }

            boolean matchesTab = false;
            if (currentTab.equals("Personal") && !isClassroom) matchesTab = true;
            if (currentTab.equals("Classroom") && isClassroom) matchesTab = true;

            // 2. ARAMA KONTROLÜ
            boolean matchesSearch = false;
            if (searchTextLower.isEmpty()) {
                matchesSearch = true;
            } else {
                if (nameLower.contains(searchTextLower) ||
                        cityLower.contains(searchTextLower) ||
                        needLower.contains(searchTextLower)) {
                    matchesSearch = true;
                }
            }

            // GÖSTER
            if (matchesTab && matchesSearch) {
                createStudentCard(student);
                foundAny = true;
            }
        }

        // SONUÇ YOKSA
        if (!foundAny) {
            TextView emptyMsg = new TextView(this);
            emptyMsg.setText("No matches found for '" + currentSearchText + "'");
            emptyMsg.setPadding(40, 40, 40, 40);
            emptyMsg.setTextColor(Color.GRAY);
            emptyMsg.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            binding.layoutCardsContainer.addView(emptyMsg);
        }
    }

    private void updateTabs(String selectedTab) {
        currentTab = selectedTab;

        int activeBg = R.drawable.rounded_button_red;
        int activeText = Color.WHITE;
        int passiveBg = R.drawable.input_border_red;
        int passiveText = Color.parseColor("#FF6B6B");

        if (selectedTab.equals("Personal")) {
            binding.btnTabPersonal.setBackgroundResource(activeBg);
            binding.btnTabPersonal.setTextColor(activeText);
            binding.btnTabClassroom.setBackgroundResource(passiveBg);
            binding.btnTabClassroom.setTextColor(passiveText);
        } else {
            binding.btnTabClassroom.setBackgroundResource(activeBg);
            binding.btnTabClassroom.setTextColor(activeText);
            binding.btnTabPersonal.setBackgroundResource(passiveBg);
            binding.btnTabPersonal.setTextColor(passiveText);
        }
        filterList(); // Sekme değişince de listeyi yenile
    }

    private void createStudentCard(Student student) {
        View cardView = LayoutInflater.from(this).inflate(R.layout.item_discovery_book, binding.layoutCardsContainer, false);

        TextView tvBookNeeded = cardView.findViewById(R.id.tvBookNeeded);
        TextView tvStudentName = cardView.findViewById(R.id.tvStudentName);
        TextView tvStudentLocation = cardView.findViewById(R.id.tvStudentLocation);
        View btnProfile = cardView.findViewById(R.id.btnViewProfile);

        tvBookNeeded.setText("Needs: " + (student.getBookNeed() != null ? student.getBookNeed() : "General"));
        tvStudentName.setText("Student: " + student.getName());
        tvStudentLocation.setText("Location: " + (student.getCity() != null ? student.getCity() : "-"));

        View.OnClickListener goToProfile = v -> {
            Intent intent = new Intent(this, StudentProfileActivity.class);
            intent.putExtra("name", student.getName());
            intent.putExtra("school", student.getSchool());
            intent.putExtra("city", student.getCity());
            intent.putExtra("studentId", student.getName());
            startActivity(intent);
        };

        cardView.setOnClickListener(goToProfile);
        if (btnProfile != null) btnProfile.setOnClickListener(goToProfile);

        binding.layoutCardsContainer.addView(cardView);
    }

    private void setupBottomNav() {
        // 1. HOME
        binding.navHome.setOnClickListener(v -> {
            Toast.makeText(this, "Refreshing list...", Toast.LENGTH_SHORT).show();
            fetchStudentsFromFirebase();
        });

        // 2. SEARCH
        binding.navSearch.setOnClickListener(v -> {
            // İmleci yukarıdaki kutuya koy
            binding.etSearchBox.requestFocus();

            // (İsteğe bağlı) Klavyeyi de otomatik açtırabiliriz
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(binding.etSearchBox, InputMethodManager.SHOW_IMPLICIT);
        });

        // --- İŞTE BURASI: 3. TUŞ (KARGO TAKİBİ) ---
        binding.navList.setOnClickListener(v -> {
            // DeliveryTrackingActivity sayfasına git
            Intent intent = new Intent(DonorDiscoveryActivity.this, DeliveryTrackingActivity.class);
            startActivity(intent);
        });

        // 4. PROFILE
        binding.navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(DonorDiscoveryActivity.this, DonorProfileActivity.class);
            startActivity(intent);
        });
    }
}