package msku.ceng.madlab.readshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class BookSuggestionActivity extends AppCompatActivity {

    private RecyclerView rvSuggestions, rvBasket;
    private TextView tvTotalPrice;
    private Button btnConfirm;

    private TextView tvDeliveryLocation;

    private List<Book> suggestionList = new ArrayList<>();
    private List<Book> basketList = new ArrayList<>();
    private SuggestionAdapter suggestionAdapter;
    private BasketAdapter basketAdapter;

    private FirebaseFirestore db;
    private String studentId;
    private String studentName;


    private String schoolName = "Unknown School";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_suggestion);

        db = FirebaseFirestore.getInstance();
        studentId = getIntent().getStringExtra("studentId");
        studentName = getIntent().getStringExtra("studentName");

        rvSuggestions = findViewById(R.id.rvSuggestions);
        rvBasket = findViewById(R.id.rvBasket);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnConfirm = findViewById(R.id.btnConfirmDonation);


        tvDeliveryLocation = findViewById(R.id.tvDeliveryLocation);

        setupLists();

        loadTeacherRequestFromFirebase();

        btnConfirm.setOnClickListener(v -> {
            if (basketList.isEmpty()) {
                Toast.makeText(this, "Basket is empty!", Toast.LENGTH_SHORT).show();
            } else {
                goToConfirmationPage();
            }
        });
    }

    private void setupLists() {
        rvSuggestions.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        suggestionAdapter = new SuggestionAdapter();
        rvSuggestions.setAdapter(suggestionAdapter);

        rvBasket.setLayoutManager(new LinearLayoutManager(this));
        basketAdapter = new BasketAdapter();
        rvBasket.setAdapter(basketAdapter);
    }

    private void goToConfirmationPage() {
        Book selectedBook = basketList.get(0);

        Intent intent = new Intent(BookSuggestionActivity.this, DonationConfirmationActivity.class);
        intent.putExtra("studentId", studentId);
        intent.putExtra("bookName", selectedBook.getTitle());
        intent.putExtra("schoolName", schoolName);

        startActivity(intent);
    }

    private void loadTeacherRequestFromFirebase() {
        if (studentId == null) return;

        if (tvDeliveryLocation != null) {
            tvDeliveryLocation.setText("Delivery Location: Loading...");
        }

        db.collection("students").document(studentId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String requestedBookName = documentSnapshot.getString("bookNeed");

                        String fetchedSchoolName = documentSnapshot.getString("schoolName");

                        if (fetchedSchoolName != null && !fetchedSchoolName.isEmpty()) {
                            schoolName = fetchedSchoolName;
                            if (tvDeliveryLocation != null) {
                                tvDeliveryLocation.setText("Delivery Location: " + schoolName);
                            }
                        }

                        String author = "Requested Book";
                        double donationAmount = 0.0;

                        if (requestedBookName != null) {
                            suggestionList.clear();
                            suggestionList.add(new Book(studentId, requestedBookName, author, donationAmount));
                            suggestionAdapter.notifyDataSetChanged();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    if (tvDeliveryLocation != null) {
                        tvDeliveryLocation.setText("Delivery Location: Info Unavailable");
                    }
                    Toast.makeText(this, "Error loading data", Toast.LENGTH_SHORT).show();
                });
    }

    private void addToBasket(Book book) {
        if (!basketList.isEmpty()) {
            Toast.makeText(this, "You can donate one book at a time.", Toast.LENGTH_SHORT).show();
            return;
        }
        basketList.add(book);
        basketAdapter.notifyDataSetChanged();
        updateTotal();
    }

    private void removeFromBasket(int position) {
        basketList.remove(position);
        basketAdapter.notifyDataSetChanged();
        updateTotal();
    }

    private void updateTotal() {
        tvTotalPrice.setText("Total: Free Donation");
    }

    class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.ViewHolder> {
        @NonNull @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_suggestion, parent, false);
            return new ViewHolder(v);
        }
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Book book = suggestionList.get(position);
            holder.tvTitle.setText(book.getTitle());
            holder.tvPrice.setText("Free");
            holder.btnAdd.setOnClickListener(v -> addToBasket(book));
        }
        @Override
        public int getItemCount() { return suggestionList.size(); }
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvPrice; Button btnAdd;
            public ViewHolder(View itemView) { super(itemView); tvTitle=itemView.findViewById(R.id.tvBookTitle); tvPrice=itemView.findViewById(R.id.tvBookPrice); btnAdd=itemView.findViewById(R.id.btnAdd); }
        }
    }

    class BasketAdapter extends RecyclerView.Adapter<BasketAdapter.ViewHolder> {
        @NonNull @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_book, parent, false);
            return new ViewHolder(v);
        }
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Book book = basketList.get(position);
            holder.tvTitle.setText(book.getTitle());
            holder.tvPrice.setText("Free");
            holder.btnRemove.setOnClickListener(v -> removeFromBasket(position));
        }
        @Override
        public int getItemCount() { return basketList.size(); }
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvPrice; View btnRemove;
            public ViewHolder(View itemView) { super(itemView); tvTitle=itemView.findViewById(R.id.tvCartTitle); tvPrice=itemView.findViewById(R.id.tvCartPrice); btnRemove=itemView.findViewById(R.id.btnRemove); }
        }
    }
}