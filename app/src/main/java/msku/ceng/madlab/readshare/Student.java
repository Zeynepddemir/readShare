package msku.ceng.madlab.readshare;

import com.google.firebase.firestore.Exclude; // Bu import önemli!

public class Student {
    private String name;
    private String school;     // Okul Adı
    private String city;       // Şehir
    private String bookNeed;   // Kitap İhtiyacı (Veya İlgi Alanları)
    private String teacherId;  // Öğretmen ID'si
    private String status;     // Durum: "Waiting" veya "Donated"

    // Firebase ID'sini tutmak için (Veritabanında yazmaz ama biz kodda kullanırız)
    @Exclude
    private String documentId;

    // 1. BOŞ YAPICI (Firebase İçin Şart!)
    public Student() {
        // Boş kalsın
    }

    // 2. DOLU YAPICI (Veri eklerken kolaylık olsun diye)
    public Student(String name, String school, String city, String bookNeed, String teacherId, String status) {
        this.name = name;
        this.school = school;
        this.city = city;
        this.bookNeed = bookNeed;
        this.teacherId = teacherId;
        this.status = status;
    }

    // --- GETTER VE SETTER METOTLARI (Hepsini Ekledik) ---

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    // Eğer veritabanında "schoolName" diye kayıtlıysa bu metot adını değiştirebiliriz
    // Ama genelde Firebase alan adıyla uyumlu olmalı.
    // Biz kodlarda 'school' veya 'schoolName' karışık kullandık.
    // Garanti olsun diye ikisini de ekliyorum:
    public String getSchool() { return school; }
    public void setSchool(String school) { this.school = school; }

    // Eğer veritabanına "schoolName" diye kaydettiysen Firebase otomatik eşlesin diye:
    public String getSchoolName() { return school; }
    public void setSchoolName(String school) { this.school = school; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getBookNeed() { return bookNeed; }
    public void setBookNeed(String bookNeed) { this.bookNeed = bookNeed; }

    public String getTeacherId() { return teacherId; }
    public void setTeacherId(String teacherId) { this.teacherId = teacherId; }

    // 🔥 İŞTE KIRMIZI YANAN KISIM BURASIYDI!
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // --- ID YÖNETİMİ ---
    @Exclude
    public String getDocumentId() { return documentId; }

    @Exclude
    public void setDocumentId(String documentId) { this.documentId = documentId; }
}