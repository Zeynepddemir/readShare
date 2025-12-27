package msku.ceng.madlab.readshare;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import msku.ceng.madlab.readshare.databinding.ActivityStudentProfileBinding;

public class StudentProfileActivity extends AppCompatActivity {
    private ActivityStudentProfileBinding binding;
    // Bu sayfaya gelen öğrenci ID'si
    private String currentStudentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudentProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String name = getIntent().getStringExtra("name");
        String school = getIntent().getStringExtra("school");
        String city = getIntent().getStringExtra("city");

        // Listeden gelen ID'yi alıyoruz (DonorDiscovery'den gelmişti)
        currentStudentId = getIntent().getStringExtra("studentId");
        // Eğer ID gelmediyse isimden ID uydur (Fallback)
        if(currentStudentId == null && name != null) currentStudentId = name;

        if(name != null) binding.tvProfileName.setText("Name: " + name);
        if(school != null) binding.tvProfileSchool.setText("School: " + school);
        if(city != null) binding.tvProfileLocation.setText("Location: " + city);

        // Kitap İhtiyacına Tıkla -> Öneri Ekranına Git
        binding.tvBookNeed1.setOnClickListener(v -> {
            Intent intent = new Intent(this, BookSuggestionActivity.class);
            intent.putExtra("category", "Animal Stories");
            intent.putExtra("location", (city != null ? city : "Muğla") + " School");
            intent.putExtra("target", "10 Years Old");

            // KRİTİK: ID'yi bir sonraki sayfaya taşıyoruz!
            intent.putExtra("studentId", currentStudentId);

            startActivity(intent);
        });

        binding.btnBack.setOnClickListener(v -> finish());
    }
}