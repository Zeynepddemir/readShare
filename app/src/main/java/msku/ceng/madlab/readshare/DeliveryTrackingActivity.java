package msku.ceng.madlab.readshare;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class DeliveryTrackingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_tracking);

        // Geri Tuşu
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Haritaya tıklayınca uyarı ver (Opsiyonel)
        findViewById(R.id.cardMap).setOnClickListener(v ->
                Toast.makeText(this, "Map view is expanding...", Toast.LENGTH_SHORT).show()
        );
    }
}