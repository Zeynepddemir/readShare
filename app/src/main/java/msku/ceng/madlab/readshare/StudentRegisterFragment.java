package msku.ceng.madlab.readshare; // Düz paket ismi

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
import com.google.firebase.firestore.FirebaseFirestore;
import msku.ceng.madlab.readshare.databinding.FragmentStudentRegisterBinding;

public class StudentRegisterFragment extends Fragment {

    private FragmentStudentRegisterBinding binding;
    private FirebaseFirestore db;

    // 81 İLİMİZ
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

        // Spinner Ayarı
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, CITIES);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCity.setAdapter(adapter);
        binding.spinnerCity.setSelection(47); // Muğla varsayılan

        binding.btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        binding.btnSubmitStudent.setOnClickListener(v -> {
            String name = binding.etStudentName.getText().toString();
            String age = binding.etAgeGrade.getText().toString();
            String school = binding.etSchoolName.getText().toString();
            String city = binding.spinnerCity.getSelectedItem().toString();

            // İlgi Alanları
            StringBuilder interests = new StringBuilder();
            for (int i = 0; i < binding.chipGroupInterests.getChildCount(); i++) {
                Chip chip = (Chip) binding.chipGroupInterests.getChildAt(i);
                if (chip.isChecked()) interests.append(chip.getText()).append(", ");
            }
            String need = interests.toString().isEmpty() ? "General Stories" : interests.toString();

            // Seviye
            String level = "Medium";
            int selectedLevelId = binding.rgReadingLevel.getCheckedRadioButtonId();
            if (selectedLevelId != -1) {
                RadioButton rb = binding.getRoot().findViewById(selectedLevelId);
                level = rb.getText().toString();
            }

            if(name.isEmpty() || school.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all fields!", Toast.LENGTH_SHORT).show();
                return;
            }

            Student newStudent = new Student(name, school, city, age + " (" + level + ")", need);

            db.collection("students").add(newStudent)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(getContext(), "Student Saved!", Toast.LENGTH_SHORT).show();
                        getParentFragmentManager().popBackStack();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        return binding.getRoot();
    }
}