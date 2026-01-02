package msku.ceng.madlab.readshare;

import com.google.firebase.firestore.Exclude; // Bu import önemli!

public class Student {
    private String name;
    private String school;
    private String city;
    private String bookNeed;
    private String teacherId;
    private String status;

    @Exclude
    private String documentId;

    public Student() {
    }

    public Student(String name, String school, String city, String bookNeed, String teacherId, String status) {
        this.name = name;
        this.school = school;
        this.city = city;
        this.bookNeed = bookNeed;
        this.teacherId = teacherId;
        this.status = status;
    }


    public String getName() { return name; }
    public void setName(String name) { this.name = name; }


    public String getSchool() { return school; }
    public void setSchool(String school) { this.school = school; }

    public String getSchoolName() { return school; }
    public void setSchoolName(String school) { this.school = school; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getBookNeed() { return bookNeed; }
    public void setBookNeed(String bookNeed) { this.bookNeed = bookNeed; }

    public String getTeacherId() { return teacherId; }
    public void setTeacherId(String teacherId) { this.teacherId = teacherId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Exclude
    public String getDocumentId() { return documentId; }

    @Exclude
    public void setDocumentId(String documentId) { this.documentId = documentId; }
}