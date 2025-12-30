package msku.ceng.madlab.readshare;

import com.google.firebase.Timestamp;

public class HistoryItem {
    private String bookName;
    private String schoolName;
    private Timestamp date;

    // Boş kurucu şart
    public HistoryItem() { }

    public HistoryItem(String bookName, String schoolName, Timestamp date) {
        this.bookName = bookName;
        this.schoolName = schoolName;
        this.date = date;
    }

    public String getBookName() { return bookName; }
    public String getSchoolName() { return schoolName; }
    public Timestamp getDate() { return date; }
}