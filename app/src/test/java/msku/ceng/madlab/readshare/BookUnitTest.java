package msku.ceng.madlab.readshare;

import org.junit.Test;
import static org.junit.Assert.*;

public class BookUnitTest {

    @Test
    public void book_object_isCreatedCorrectly() {
        String id = "123";
        String title = "Sefiller";
        String author = "Victor Hugo";
        double price = 0.0;

        Book book = new Book(id, title, author, price);

        assertEquals("Kitap ismi yanlış kaydedildi", "Sefiller", book.getTitle());
        assertEquals("Yazar ismi yanlış kaydedildi", "Victor Hugo", book.getAuthor());
        assertEquals("Fiyat hatalı", 0.0, book.getPrice(), 0.001);
    }
}