package msku.ceng.madlab.readshare;

import com.google.firebase.Timestamp;

public class Message {
    private String senderName;
    private String content;
    private String status; // "Pending", "Approved", "Rejected"
    private Timestamp timestamp;
    private String documentId; // Silmek veya güncellemek için lazım

    public Message() {} // Firebase için boş kurucu şart!

    public Message(String senderName, String content, String status, Timestamp timestamp) {
        this.senderName = senderName;
        this.content = content;
        this.status = status;
        this.timestamp = timestamp;
    }

    // Getter ve Setter Metotları
    public String getSenderName() { return senderName; }
    public String getContent() { return content; }
    public String getStatus() { return status; }
    public Timestamp getTimestamp() { return timestamp; }

    public String getDocumentId() { return documentId; }
    public void setDocumentId(String documentId) { this.documentId = documentId; }
}