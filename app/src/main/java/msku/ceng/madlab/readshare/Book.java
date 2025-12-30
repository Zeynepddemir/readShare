package msku.ceng.madlab.readshare;

import java.io.Serializable;

public class Book implements Serializable {
    private String id;          // Firebase ID
    private String title;       // Kitap Adı
    private String author;      // Yazar
    private String pageCount;   // Kütüphane için sayfa sayısı
    private String status;      // Kütüphane için durum (Started, Finished)
    private double price;       // Bağış için fiyat

    // 1. Boş Yapıcı (Firebase İçin Şart)
    public Book() {}

    // 2. KÜTÜPHANE İÇİN KURUCU (Library)
    public Book(String title, String author, String pageCount, String status) {
        this.title = title;
        this.author = author;
        this.pageCount = pageCount;
        this.status = status;
        this.price = 0.0;
    }

    // 3. BAĞIŞ İÇİN KURUCU (Donation)
    public Book(String id, String title, String author, double price) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.price = price;
        this.status = "Available";
        this.pageCount = "0";
    }

    // --- Getter ve Setter Metodları ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getPageCount() { return pageCount; }
    public void setPageCount(String pageCount) { this.pageCount = pageCount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}