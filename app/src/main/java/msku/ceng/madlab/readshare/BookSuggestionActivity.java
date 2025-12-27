package msku.ceng.madlab.readshare;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import msku.ceng.madlab.readshare.databinding.ActivityBookSuggestionBinding;

public class BookSuggestionActivity extends AppCompatActivity {

    private ActivityBookSuggestionBinding binding;

    // Şu an seçili olan kitabın bilgileri (Varsayılan değerler)
    private String selectedBookTitle = "Select a Book";
    private String selectedBookPrice = "$0";
    private String studentId; // Bağış yapılacak öğrenci

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookSuggestionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 1. Gelen Verileri Al
        String category = getIntent().getStringExtra("category");
        String location = getIntent().getStringExtra("location");
        String target = getIntent().getStringExtra("target");
        // Profil sayfasından studentId gelmeli! Gelmiyorsa demo kullanırız.
        studentId = getIntent().getStringExtra("studentId");
        if(studentId == null) studentId = "demo_student_id";

        // Başlıkları Doldur
        if (category != null) binding.tvPageTitle.setText(category + " Donation");
        if (location != null) binding.tvDeliveryLocation.setText("Location: " + location);
        if (target != null) binding.tvTargetGroup.setText("Target: " + target);

        binding.btnBack.setOnClickListener(v -> finish());

        // 2. Sistem Önerilerini (Kartları) Oluştur
        createSuggestionCards();

        // 3. Onay Sayfasına Git
        binding.btnConfirmDonation.setOnClickListener(v -> {
            if (selectedBookPrice.equals("$0")) {
                Toast.makeText(this, "Please select a book first!", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(BookSuggestionActivity.this, DonationConfirmationActivity.class);
            intent.putExtra("bookName", selectedBookTitle);
            intent.putExtra("price", selectedBookPrice);
            intent.putExtra("location", location);
            intent.putExtra("studentId", studentId); // ÖNEMLİ: Kime gideceğini taşıyoruz
            startActivity(intent);
        });
    }

    private void createSuggestionCards() {
        // XML'de ID verdiğimiz kutuyu temizle
        binding.layoutSuggestionsContainer.removeAllViews();

        // Örnek Kitaplar (Normalde veritabanından gelebilir)
        addBookCard("Harry Potter", "$12");
        addBookCard("Little Prince", "$8");
        addBookCard("Animal Farm", "$10");
        addBookCard("Science 101", "$15");
    }

    private void addBookCard(String title, String price) {
        // Dinamik olarak kart (LinearLayout) oluşturuyoruz
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        // Arkaplan: Kırmızı çerçeve
        card.setBackgroundResource(R.drawable.input_border_red);
        card.setPadding(24, 24, 24, 24);

        // Boyutlar (Genişlik 100dp, Yükseklik 130dp gibi)
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(300, 350); // Pixel cinsinden
        params.setMargins(0, 0, 32, 0); // Sağ tarafa boşluk
        card.setLayoutParams(params);
        card.setGravity(Gravity.CENTER);

        // Kitap İsmi
        TextView tvTitle = new TextView(this);
        tvTitle.setText(title);
        tvTitle.setTextColor(Color.BLACK);
        tvTitle.setTextSize(14);
        tvTitle.setGravity(Gravity.CENTER);

        // Fiyat
        TextView tvPrice = new TextView(this);
        tvPrice.setText(price);
        tvPrice.setTextColor(Color.parseColor("#FF6B6B")); // Kırmızı
        tvPrice.setTypeface(null, Typeface.BOLD);
        tvPrice.setGravity(Gravity.CENTER);
        tvPrice.setPadding(0, 16, 0, 0);

        card.addView(tvTitle);
        card.addView(tvPrice);

        // TIKLAMA OLAYI: Sepeti Güncelle
        card.setOnClickListener(v -> {
            selectedBookTitle = title;
            selectedBookPrice = price;
            updateBasketUI();

            Toast.makeText(this, title + " added to basket!", Toast.LENGTH_SHORT).show();
        });

        // Kartı listeye ekle
        binding.layoutSuggestionsContainer.addView(card);
    }

    private void updateBasketUI() {
        binding.tvBasketTitle.setText(selectedBookTitle);
        // Fiyatı güncelle (XML'de ID vermemiş olabiliriz, basket içindeki TextView'i bulmamız lazım)
        // Eğer basket fiyatına ID vermediysen şimdilik sadece ismi güncelliyoruz.
        // İstersen XML'e gidip fiyata id verip (tvBasketPrice) buraya binding.tvBasketPrice.setText(...) ekleyebilirsin.
    }
}