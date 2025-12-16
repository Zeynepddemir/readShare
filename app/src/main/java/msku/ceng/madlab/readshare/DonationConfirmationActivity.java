package msku.ceng.madlab.readshare; // Düz paket ismi

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import msku.ceng.madlab.readshare.databinding.ActivityDonationConfirmationBinding;

public class DonationConfirmationActivity extends AppCompatActivity {
    private ActivityDonationConfirmationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDonationConfirmationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 1. Gelen Verileri Yakala
        String bookName = getIntent().getStringExtra("bookName");
        String location = getIntent().getStringExtra("location");
        String price = getIntent().getStringExtra("price");

        // 2. Verileri Ekrana Yaz (Boş değilse)
        if (bookName != null) {
            binding.tvBasketBookName.setText(bookName);
            binding.tvSummaryBookName.setText(bookName);
            // Yazar adı şimdilik sabit kalsın veya onu da gönderebiliriz
        }

        if (location != null) {
            binding.tvConfirmAddress.setText(location);
        }

        if (price != null) {
            binding.tvSummaryTotal.setText("Total: " + price);
        }

        // Geri Tuşu
        binding.btnBack.setOnClickListener(v -> finish());

        // Onayla Butonu -> TEŞEKKÜR SAYFASINA GİDECEK
        binding.btnFinalConfirm.setOnClickListener(v -> {
            Toast.makeText(this, "Donation Successful!", Toast.LENGTH_SHORT).show();
            // Buradan sonra ThankYouActivity'ye geçeceğiz
        });
    }
}