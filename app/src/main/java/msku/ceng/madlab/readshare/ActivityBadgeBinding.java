package msku.ceng.madlab.readshare;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import msku.ceng.madlab.readshare.databinding.ActivityBadgeCollectionBinding;

public class ActivityBadgeBinding extends AppCompatActivity {
    private ActivityBadgeCollectionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBadgeCollectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Geri Tuşu
        binding.btnBack.setOnClickListener(v -> finish());
    }
}