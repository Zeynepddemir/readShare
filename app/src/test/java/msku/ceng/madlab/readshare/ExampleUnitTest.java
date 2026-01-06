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

        Student student = new Student();


        student.setName("Ayşe");


        student.setBookNeed("Nutuk");


        assertEquals("Ayşe", student.getName());
        assertEquals("Nutuk", student.getBookNeed());
    }

    @Test
    public void donationStatus_isInitialValueWaiting() {

        Student student = new Student();
        student.setStatus("Waiting");

        assertEquals("Waiting", student.getStatus());
    }
}