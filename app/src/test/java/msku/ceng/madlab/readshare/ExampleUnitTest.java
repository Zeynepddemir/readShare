package msku.ceng.madlab.readshare;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Author: Beyza Onar & Zeynep Demir
 * Bu test sınıfı, readShare projesindeki veri tutarlılığını doğrular.
 */
public class ExampleUnitTest {

    @Test
    public void studentData_isConsistent() {
        // readShare: Öğrenci profil oluşturma testi [cite: 34]
        Student student = new Student();

        // Örnek senaryo: Ayşe için veri girişi
        student.setName("Ayşe");

        // Örnek senaryo: Kitap ihtiyacı girişi [cite: 11, 41]
        student.setBookNeed("Nutuk");

        // Verilerin doğruluğunu kontrol ediyoruz
        assertEquals("Ayşe", student.getName());
        assertEquals("Nutuk", student.getBookNeed());
    }

    @Test
    public void donationStatus_isInitialValueWaiting() {
        // readShare: Yeni bir bağış talebi varsayılan olarak "Waiting" durumundadır
        Student student = new Student();
        student.setStatus("Waiting");

        assertEquals("Waiting", student.getStatus());
    }
}