package msku.ceng.madlab.readshare; // Düz paket ismi

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import msku.ceng.madlab.readshare.databinding.ActivityStudentProfileBinding;

public class StudentProfileActivity extends AppCompatActivity {
    private ActivityStudentProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudentProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String name = getIntent().getStringExtra("name");
        String school = getIntent().getStringExtra("school");
        String city = getIntent().getStringExtra("city");

        if(name != null) binding.tvProfileName.setText("Name: " + name);
        if(school != null) binding.tvProfileSchool.setText("School: " + school);
        if(city != null) binding.tvProfileLocation.setText("Location: " + city);

        // Kitap İhtiyacına Tıkla -> Öneri Ekranına Git
        binding.tvBookNeed1.setOnClickListener(v -> {
            Intent intent = new Intent(this, BookSuggestionActivity.class);
            intent.putExtra("category", "Animal Stories");
            intent.putExtra("location", (city != null ? city : "Muğla") + " School");
            intent.putExtra("target", "10 Years Old");
            startActivity(intent);
        });

        binding.btnBack.setOnClickListener(v -> finish());
    }
}