package msku.ceng.madlab.readshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import msku.ceng.madlab.readshare.databinding.ActivityDonorDiscoveryBinding;

public class DonorDiscoveryActivity extends AppCompatActivity {

    private ActivityDonorDiscoveryBinding binding;
    // HATA 1 ÇÖZÜMÜ: Firebase değişkenini tanımla
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDonorDiscoveryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Firestore bağlantısını başlat
        db = FirebaseFirestore.getInstance();

        // HATA 2 ÇÖZÜMÜ: Dinamik veriyi çekme metodunu çağır
        fetchStudentsFromFirebase();

        // Geri Tuşu
        binding.btnBack.setOnClickListener(v -> finish());

        // NOT: onCreate içinde imgBook1/imgBook2 gibi sabit tıklama kodları SİLİNDİ
    }

    // Rol Kontrolünden sonra çağrılan ana metot
    private void fetchStudentsFromFirebase() {
        // Hata 1'den sonra db artık tanımlı
        db.collection("students")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                            Student student = snapshot.toObject(Student.class);

                            if (student != null) {
                                // Öğrenci modelinden kitap ihtiyacını al
                                String bookNeeded = student.getBookNeed();

                                // Kartı oluşturma fonksiyonunu çağır
                                createStudentCard(student, bookNeeded != null ? bookNeeded : "General Reading");
                            }
                        }
                    } else {
                        Toast.makeText(this, "No students registered yet.", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading students: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void createStudentCard(Student student, String bookNeeded) {
        // item_discovery_book.xml şablonunu kullan
        View cardView = LayoutInflater.from(this).inflate(R.layout.item_discovery_book, binding.layoutBookCardsContainer, false);

        // Şablondaki elemanları bul (ID'ler item_discovery_book.xml dosyasından gelmeli!)
        TextView tvBookNeeded = cardView.findViewById(R.id.tvBookNeeded);
        TextView tvStudentName = cardView.findViewById(R.id.tvStudentName);
        TextView tvStudentLocation = cardView.findViewById(R.id.tvStudentLocation);

        // Dinamik Veriyi Yaz
        tvBookNeeded.setText("Needs: " + bookNeeded);
        tvStudentName.setText("Student: " + student.getName());
        tvStudentLocation.setText("Location: " + student.getCity());

        // Tıklama Olayı: Karta tıklanınca Profile sayfasına git
        cardView.setOnClickListener(v -> openProfile(student));

        // Kartı ana konteynere ekle (activity_donor_discovery.xml içindeki layoutBookCardsContainer)
        binding.layoutBookCardsContainer.addView(cardView);
    }

    // openProfile metodu artık Student objesini alıyor
    private void openProfile(Student student) {
        Intent intent = new Intent(this, StudentProfileActivity.class);

        // Öğrenci bilgilerini StudentProfileActivity'ye gönder
        intent.putExtra("name", student.getName());
        intent.putExtra("school", student.getSchool());
        intent.putExtra("city", student.getCity());
        // Öğrencinin ihtiyacını da gönderelim
        intent.putExtra("bookNeeded", student.getBookNeed());

        startActivity(intent);
    }
}