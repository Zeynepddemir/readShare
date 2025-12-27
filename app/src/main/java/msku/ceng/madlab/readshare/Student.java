package msku.ceng.madlab.readshare;
import com.google.firebase.firestore.DocumentId;
public class Student {
    private String name;
    private String school;
    private String city;
    private String age;
    private String bookNeed; // Bu çok önemli!
    private String studentId;
    private String documentId;

    public Student() { } // Firebase için şart

    public Student(String name, String school, String city, String age, String bookNeed) {
        this.name = name;
        this.school = school;
        this.city = city;
        this.age = age;
        this.bookNeed = bookNeed;
    }

    // Getter ve Setter'lar
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSchool() { return school; }
    public void setSchool(String school) { this.school = school; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getAge() { return age; }
    public void setAge(String age) { this.age = age; }

    public String getBookNeed() { return bookNeed; }
    public void setBookNeed(String bookNeed) { this.bookNeed = bookNeed; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getDocumentId() { return documentId; }
    public void setDocumentId(String documentId) { this.documentId = documentId; }
}