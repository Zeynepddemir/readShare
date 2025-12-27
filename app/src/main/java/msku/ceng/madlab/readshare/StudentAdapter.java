package msku.ceng.madlab.readshare;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    private List<Student> studentList;
    private Context context;

    public StudentAdapter(List<Student> studentList, Context context) {
        this.studentList = studentList;
        this.context = context;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = studentList.get(position);

        holder.tvName.setText(student.getName());
        holder.tvNeeds.setText("Needs: " + student.getBookNeed());

        // GÜNLÜK BUTONUNA TIKLANINCA
        holder.btnDiary.setOnClickListener(v -> {
            Intent intent = new Intent(context, ReadingDiaryActivity.class);
            // Öğretmen hangi öğrenciye tıkladıysa onun ID'sini gönderiyoruz
            // student.getDocumentId() metodunun Student.java içinde olduğundan emin ol!
            intent.putExtra("studentId", student.getDocumentId());
            intent.putExtra("studentName", student.getName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvNeeds;
        ImageView btnDiary;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvStudentName);
            tvNeeds = itemView.findViewById(R.id.tvStudentNeeds);
            btnDiary = itemView.findViewById(R.id.btnOpenDiary);
        }
    }
}