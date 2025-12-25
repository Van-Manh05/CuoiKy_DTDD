package com.example.webmasterdotnetvn.quanlychitieu;

import com.google.firebase.firestore.Exclude;

public class ViTien {
    private String id;      // ID của document (Không lưu vào trong data)
    private String name;    // Tên ví
    private double balance; // Số dư
    private int type;       // Loại ví: 1=Cơ bản, 2=Tín dụng, 3=Vay nợ

    // 1. Constructor rỗng (BẮT BUỘC cho Firebase)
    public ViTien() {
        // Mặc định là ví cơ bản nếu không chọn
        this.type = 1;
    }

    // 2. Constructor cũ (Giữ lại để không bị lỗi ở TaisanActivity)
    public ViTien(String id, String name, double balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.type = 1; // Mặc định
    }

    // 3. Constructor đầy đủ (Dùng khi muốn set cả loại ví)
    public ViTien(String id, String name, double balance, int type) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.type = type;
    }

    // 4. Getter & Setter

    // @Exclude: Báo cho Firebase biết KHÔNG lưu field này vào database
    // (Vì ID đã nằm ở tên document rồi, lưu vào trong nữa sẽ bị dư thừa)
    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}