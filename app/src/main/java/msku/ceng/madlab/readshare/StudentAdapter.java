package msku.ceng.madlab.readshare;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth; // Eklendi
import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    private List<Student> studentList;
    private Context context;
    private String currentUserId; // Aktif kullanıcı ID'si

    public StudentAdapter(List<Student> studentList, Context context) {
        this.studentList = studentList;
        this.context = context;
        // Giriş yapan kullanıcının ID'sini al
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = studentList.get(position);

        // Verileri Tasarıma Bağla
        holder.tvBookName.setText(student.getBookNeed() != null ? student.getBookNeed() : "No book requested");
        holder.tvLocation.setText("Location: " + (student.getCity() != null ? student.getCity() : "Unknown"));

        // --- ROL KONTROLÜ (GİZLEME MANTIĞI) ---
        // Eğer bakan kişi bu öğrencinin öğretmeni ise "Donate" butonunu gizle
        if (currentUserId != null && currentUserId.equals(student.getTeacherId())) {
            holder.btnDonateBook.setVisibility(View.GONE);
        } else {
            // Değilse (Bağışçıysa) göster
            holder.btnDonateBook.setVisibility(View.VISIBLE);
        }

        // 1. BUTON: Profile Git
        holder.btnStudentProfile.setOnClickListener(v -> {
            Intent intent = new Intent(context, StudentProfileActivity.class);
            intent.putExtra("studentId", student.getDocumentId());
            intent.putExtra("studentName", student.getName());
            context.startActivity(intent);
        });

        // 2. BUTON: Bağış Sayfasına Git


        // StudentAdapter.java içinde...

        holder.btnDonateBook.setOnClickListener(v -> {
            // KONTROL: ID Dolu mu?
            if (student.getDocumentId() != null && !student.getDocumentId().isEmpty()) {
                Intent intent = new Intent(context, BookSuggestionActivity.class);
                intent.putExtra("studentId", student.getDocumentId());
                intent.putExtra("studentName", student.getName());
                context.startActivity(intent);
            } else {
                // ID BOŞSA ÇÖKME, UYARI VER
                android.widget.Toast.makeText(context, "Hata: Öğrenci ID'si alınamadı!", android.widget.Toast.LENGTH_SHORT).show();

                // Logcat'e yazdıralım ki görelim
                android.util.Log.e("StudentAdapter", "ID NULL GELDİ! Öğrenci: " + student.getName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView tvBookName, tvLocation;
        Button btnStudentProfile, btnDonateBook;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBookName = itemView.findViewById(R.id.tvBookName);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            btnStudentProfile = itemView.findViewById(R.id.btnStudentProfile);
            btnDonateBook = itemView.findViewById(R.id.btnDonateBook);
        }
    }
}