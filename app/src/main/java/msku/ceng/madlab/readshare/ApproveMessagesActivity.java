package msku.ceng.madlab.readshare; // Düz paket ismi

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import msku.ceng.madlab.readshare.databinding.ActivityApproveMessagesBinding;

public class ApproveMessagesActivity extends AppCompatActivity {

    private ActivityApproveMessagesBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityApproveMessagesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        // --- KART 1 İŞLEMLERİ ---
        // Onayla Butonu
        binding.btnApprove1.setOnClickListener(v -> {
            Toast.makeText(this, "Message Approved! Sent to donor.", Toast.LENGTH_SHORT).show();
            // Kartı gizle (İşlendi gibi görünsün)
            binding.cardMessage1.setVisibility(View.GONE);
        });

        // Reddet Butonu
        binding.btnReject1.setOnClickListener(v -> {
            Toast.makeText(this, "Sent back to student for editing.", Toast.LENGTH_SHORT).show();
            binding.cardMessage1.setVisibility(View.GONE);
        });


        // --- KART 2 İŞLEMLERİ ---
        binding.btnApprove2.setOnClickListener(v -> {
            Toast.makeText(this, "Message Approved!", Toast.LENGTH_SHORT).show();
            binding.cardMessage2.setVisibility(View.GONE);
        });

        binding.btnReject2.setOnClickListener(v -> {
            Toast.makeText(this, "Requesting edit...", Toast.LENGTH_SHORT).show();
            binding.cardMessage2.setVisibility(View.GONE);
        });
    }
}