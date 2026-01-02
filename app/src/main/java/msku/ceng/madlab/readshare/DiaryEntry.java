package msku.ceng.madlab.readshare;

public class DiaryEntry {
    private String date;
    private String title;
    private String pages;
    private String note;

    public DiaryEntry() { }

    public DiaryEntry(String date, String title, String pages, String note) {
        this.date = date;
        this.title = title;
        this.pages = pages;
        this.note = note;
    }

    public String getDate() { return date; }
    public String getTitle() { return title; }
    public String getPages() { return pages; }
    public String getNote() { return note; }
}