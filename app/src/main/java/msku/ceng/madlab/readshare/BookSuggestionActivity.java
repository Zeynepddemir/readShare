package msku.ceng.madlab.readshare; // Paket ismini kontrol et

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import msku.ceng.madlab.readshare.databinding.ActivityBookSuggestionBinding;

public class BookSuggestionActivity extends AppCompatActivity {

    private ActivityBookSuggestionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookSuggestionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Gelen verileri al
        String incomingCategory = getIntent().getStringExtra("category");
        String incomingLocation = getIntent().getStringExtra("location");
        String incomingTarget = getIntent().getStringExtra("target");

        // Başlığı güncelle
        if (incomingCategory != null) {
            binding.tvPageTitle.setText(incomingCategory + " Donation");

            // BURASI ARTIK HATA VERMEYECEK ÇÜNKÜ XML'DE ID VAR
            binding.tvBasketTitle.setText(incomingCategory + " Book");
        }

        // Konumu güncelle
        if (incomingLocation != null) {
            binding.tvDeliveryLocation.setText("Location: " + incomingLocation);
        }

        // Hedef grubu güncelle
        if (incomingTarget != null) {
            binding.tvTargetGroup.setText("Target: " + incomingTarget);
        }

        binding.btnBack.setOnClickListener(v -> finish());

        // Onay sayfasına git
        binding.btnConfirmDonation.setOnClickListener(v -> {
            Intent intent = new Intent(BookSuggestionActivity.this, DonationConfirmationActivity.class);

            // Verileri taşı
            String currentBookName = binding.tvBasketTitle.getText().toString();
            intent.putExtra("bookName", currentBookName);

            String rawLocation = "";
            if(binding.tvDeliveryLocation.getText() != null) {
                rawLocation = binding.tvDeliveryLocation.getText().toString().replace("Location: ", "");
            }
            intent.putExtra("location", rawLocation);
            intent.putExtra("price", "$12");

            startActivity(intent);
        });
    }
}