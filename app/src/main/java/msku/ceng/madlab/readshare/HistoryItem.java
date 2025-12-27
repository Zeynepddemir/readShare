package msku.ceng.madlab.readshare;

import com.google.firebase.Timestamp;

public class HistoryItem {
    private String bookName;
    private String schoolName;
    private Timestamp date;
    private String studentMessage; // YENİ EKLENEN

    public HistoryItem() { }

    public HistoryItem(String bookName, String schoolName, Timestamp date, String studentMessage) {
        this.bookName = bookName;
        this.schoolName = schoolName;
        this.date = date;
        this.studentMessage = studentMessage;
    }

    public String getBookName() { return bookName; }
    public String getSchoolName() { return schoolName; }
    public Timestamp getDate() { return date; }
    public String getStudentMessage() { return studentMessage; } // Getter
}