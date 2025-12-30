package msku.ceng.madlab.readshare;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class NotificationSheetFragment extends BottomSheetDialogFragment {

    private RecyclerView rvNewRequest;
    private StudentAdapter adapter;
    private List<Student> list = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView tvTitle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // layout_notification_sheet.xml dosyasını kullanıyor
        View v = inflater.inflate(R.layout.fragment_notification_sheet, container, false);

        tvTitle = v.findViewById(R.id.tvSheetTitle); // Başlık (Varsa XML'de)
        rvNewRequest = v.findViewById(R.id.rvNewRequest);
        rvNewRequest.setLayoutManager(new LinearLayoutManager(getContext()));

        // Adapter kurulumu
        adapter = new StudentAdapter(list, getContext());
        rvNewRequest.setAdapter(adapter);

        loadLatestRequest();

        return v;
    }

    private void loadLatestRequest() {
        // SADECE FİLTRELEME YAPIYORUZ (Sıralamayı kaldırdık)
        db.collection("students")
                .whereEqualTo("status", "Waiting")
                // .orderBy("timestamp", Query.Direction.DESCENDING)  <-- BU SATIRI SİL VEYA YORUM YAP
                .limit(1) // Sadece 1 tane getir
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    list.clear();
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            Student s = doc.toObject(Student.class);
                            if (s != null) {
                                s.setDocumentId(doc.getId());
                                list.add(s);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        // Veri yoksa kullanıcıya bilgi ver
                        // Toast.makeText(getContext(), "Henüz bekleyen istek yok.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Hata: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}