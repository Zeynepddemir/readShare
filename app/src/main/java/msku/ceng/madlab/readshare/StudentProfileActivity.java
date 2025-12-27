package msku.ceng.madlab.readshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class StudentProfileActivity extends AppCompatActivity {

    // XML'deki ID'lere karşılık gelen değişkenler
    private TextView tvName, tvSchool, tvClass, tvLocation, tvBookNeed;
    private ImageView btnBack;

    // Verileri taşıyacağımız değişkenler
    private String studentId, studentName, bookName, schoolName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile); // XML ismin

        // 1. GÖRÜNÜMLERİ BAĞLA
        btnBack = findViewById(R.id.btnBack);
        tvName = findViewById(R.id.tvProfileName);
        tvSchool = findViewById(R.id.tvProfileSchool);
        tvClass = findViewById(R.id.tvProfileClass);
        tvLocation = findViewById(R.id.tvProfileLocation);

        // Burası kritik: Tasarımda buton yok, kitaba tıklayınca bağış başlayacak
        tvBookNeed = findViewById(R.id.tvBookNeed1);

        // 2. ÖNCEKİ SAYFADAN GELEN VERİLERİ AL
        Intent intent = getIntent();
        studentId = intent.getStringExtra("studentId");
        studentName = intent.getStringExtra("studentName");
        bookName = intent.getStringExtra("bookName");
        schoolName = intent.getStringExtra("schoolName");

        // 3. EKRANA YAZDIR (Formatı koruyarak: "Name: ...")
        if (studentName != null) {
            tvName.setText("Name: " + studentName);
        }

        if (schoolName != null) {
            tvSchool.setText("School: " + schoolName);
            tvLocation.setText("Location: " + schoolName); // Konum olarak da okulu gösteriyoruz
        }

        if (bookName != null) {
            // Kitap isminin başındaki "Needs:" yazısını temizleyip sadece kitabı yazalım
            String cleanBookName = bookName.replace("Needs: ", "");
            tvBookNeed.setText(cleanBookName);
            // Güncel kitap ismini değişkende tutalım
            bookName = cleanBookName;
        }

        // Sınıf bilgisini statik bırakıyoruz veya varsa intent ile alabilirsin
        tvClass.setText("Class: 3/B");

        // 4. GERİ BUTONU
        btnBack.setOnClickListener(v -> finish());

        // 5. BAĞIŞI BAŞLAT (KİTABA TIKLAYINCA)
        tvBookNeed.setOnClickListener(v -> {
            if (studentId == null) {
                Toast.makeText(this, "Error: Student info missing!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Confirmation sayfasına git ve verileri taşı
            Intent confirmationIntent = new Intent(StudentProfileActivity.this, DonationConfirmationActivity.class);
            confirmationIntent.putExtra("studentId", studentId);
            confirmationIntent.putExtra("bookName", bookName);
            confirmationIntent.putExtra("schoolName", schoolName);

            startActivity(confirmationIntent);
        });
    }
}