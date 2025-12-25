package com.example.webmasterdotnetvn.quanlychitieu;

import java.io.Serializable; // Để truyền object giữa các màn hình (Serializable)
import java.util.Date;

public class GiaoDich implements Serializable {
    private String id;
    private double amount;
    private String category;
    private String note;
    private Date date;
    private String type;


    public GiaoDich() { }


    public GiaoDich(double amount, String category, String note, Date date, String type) {
        this.amount = amount;
        this.category = category;
        this.note = note;
        this.date = date;
        this.type = type;
    }

    // --- GETTER & SETTER ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    // --- BỔ SUNG: Hàm toString để in log kiểm tra lỗi dễ hơn ---
    @Override
    public String toString() {
        return "GiaoDich{" +
                "soTien=" + amount +
                ", danhMuc='" + category + '\'' +
                ", loai='" + type + '\'' +
                ", ngay=" + date +
                '}';
    }
}