package msku.ceng.madlab.readshare;

public class CartItem {
    private Book book;
    private int quantity;

    public CartItem(Book book) {
        this.book = book;
        this.quantity = 1; // İlk eklendiğinde adet 1 olsun
    }

    public Book getBook() { return book; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}