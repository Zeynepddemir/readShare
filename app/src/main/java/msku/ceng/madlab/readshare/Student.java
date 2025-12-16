package msku.ceng.madlab.readshare;

public class Student {
    private String name;
    private String school;
    private String city;
    private String age;
    private String bookNeed; // <-- 5. Değişkenimiz bu!

    // Firebase için boş yapıcı metod (Zorunlu!)
    public Student() {}

    // İŞTE BURASI: Parantez içinde 5 tane String olmalı
    public Student(String name, String school, String city, String age, String bookNeed) {
        this.name = name;
        this.school = school;
        this.city = city;
        this.age = age;
        this.bookNeed = bookNeed; // İhtiyacı buraya kaydediyoruz
    }

    // Getter Metotları
    public String getName() { return name; }
    public String getSchool() { return school; }
    public String getCity() { return city; }
    public String getAge() { return age; }
    public String getBookNeed() { return bookNeed; }
}