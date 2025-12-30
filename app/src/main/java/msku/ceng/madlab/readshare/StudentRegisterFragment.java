package msku.ceng.madlab.readshare;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import msku.ceng.madlab.readshare.databinding.FragmentStudentRegisterBinding;

public class StudentRegisterFragment extends Fragment {

    private FragmentStudentRegisterBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private static final String[] CITIES = {
            "Adana", "Adıyaman", "Afyonkarahisar", "Ağrı", "Amasya", "Ankara", "Antalya", "Artvin", "Aydın", "Balıkesir",
            "Bilecik", "Bingöl", "Bitlis", "Bolu", "Burdur", "Bursa", "Çanakkale", "Çankırı", "Çorum", "Denizli",
            "Diyarbakır", "Edirne", "Elazığ", "Erzincan", "Erzurum", "Eskişehir", "Gaziantep", "Giresun", "Gümüşhane", "Hakkari",
            "Hatay", "Isparta", "Mersin", "İstanbul", "İzmir", "Kars", "Kastamonu", "Kayseri", "Kırklareli", "Kırşehir",
            "Kocaeli", "Konya", "Kütahya", "Malatya", "Manisa", "Kahramanmaraş", "Mardin", "Muğla", "Muş", "Nevşehir",
            "Niğde", "Ordu", "Rize", "Sakarya", "Samsun", "Siirt", "Sinop", "Sivas", "Tekirdağ", "Tokat",
            "Trabzon", "Tunceli", "Şanlıurfa", "Uşak", "Van", "Yozgat", "Zonguldak", "Aksaray", "Bayburt", "Karaman",
            "Kırıkkale", "Batman", "Şırnak", "Bartın", "Ardahan", "Iğdır", "Yalova", "Karabük", "Kilis", "Osmaniye", "Düzce"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentStudentRegisterBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Spinner Ayarı
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, CITIES);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCity.setAdapter(adapter);
        binding.spinnerCity.setSelection(47); // Muğla varsayılan

        // Geri Butonu
        binding.btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        // Kaydet Butonu
        binding.btnSubmitStudent.setOnClickListener(v -> {
            String name = binding.etStudentName.getText().toString().trim();
            String age = binding.etAgeGrade.getText().toString().trim();
            String school = binding.etSchoolName.getText().toString().trim();
            String city = binding.spinnerCity.getSelectedItem().toString();

            if (name.isEmpty() || school.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all fields!", Toast.LENGTH_SHORT).show();
                return;
            }

            // İlgi Alanları (Chip) toplama
            StringBuilder interests = new StringBuilder();
            for (int i = 0; i < binding.chipGroupInterests.getChildCount(); i++) {
                Chip chip = (Chip) binding.chipGroupInterests.getChildAt(i);
                if (chip.isChecked()) {
                    interests.append(chip.getText()).append(", ");
                }
            }

            String interestsStr = interests.toString();
            if (interestsStr.endsWith(", ")) {
                interestsStr = interestsStr.substring(0, interestsStr.length() - 2);
            }
            String finalInterests = interestsStr.isEmpty() ? "General Stories" : interestsStr;

            // Seviye (RadioButton) bulma
            String level = "Medium";
            int selectedLevelId = binding.rgReadingLevel.getCheckedRadioButtonId();
            if (selectedLevelId != -1) {
                RadioButton rb = binding.getRoot().findViewById(selectedLevelId);
                level = rb.getText().toString();
            }

            // Öğretmen ID'si ve Veri Hazırlama
            if (auth.getCurrentUser() == null) return;
            String teacherId = auth.getCurrentUser().getUid();
            String bookNeedSummary = finalInterests + " (" + level + " Level)";

            Map<String, Object> studentMap = new HashMap<>();
            studentMap.put("name", name);
            studentMap.put("schoolName", school);
            studentMap.put("city", city);
            studentMap.put("age", age);
            studentMap.put("level", level);
            studentMap.put("bookNeed", bookNeedSummary);
            studentMap.put("teacherId", teacherId);
            studentMap.put("status", "Waiting");
            studentMap.put("timestamp", com.google.firebase.Timestamp.now());

            // Firebase'e Ekleme
            db.collection("students").add(studentMap)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(getContext(), "Student Saved!", Toast.LENGTH_SHORT).show();

                        // Dashboard listesini anlık yenileme
                        if (getActivity() instanceof TeacherDashboardActivity) {
                            ((TeacherDashboardActivity) getActivity()).loadMyStudents();
                        }

                        // Kayıt sonrası Fragment'ı kapat
                        getParentFragmentManager().popBackStack();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Bellek sızıntısını önlemek için Binding'i temizle
    }
}