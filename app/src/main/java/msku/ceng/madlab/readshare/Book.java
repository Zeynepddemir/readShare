package msku.ceng.madlab.readshare;

public class Book {
    private String title;
    private String author;
    private String pageCount;
    private String status; // "Finished", "In Progress" vs.

    public Book() {} // Boş yapıcı metod şart

    public Book(String title, String author, String pageCount, String status) {
        this.title = title;
        this.author = author;
        this.pageCount = pageCount;
        this.status = status;
    }

    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getPageCount() { return pageCount; }
    public String getStatus() { return status; }
}