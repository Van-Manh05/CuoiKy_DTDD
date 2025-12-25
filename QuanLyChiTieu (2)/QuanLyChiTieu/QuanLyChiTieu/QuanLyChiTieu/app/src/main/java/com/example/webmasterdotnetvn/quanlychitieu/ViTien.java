package com.example.webmasterdotnetvn.quanlychitieu;

import com.google.firebase.firestore.Exclude;

public class ViTien {
    private String id;      // ID của document trên Firebase (dùng để Sửa/Xóa)
    private String name;    // Tên ví (Khớp với field 'name' trong Firestore)
    private double balance;


    public ViTien() {
    }

<<<<<<< HEAD

=======
    // 2. Constructor đủ
>>>>>>> baa02ed9adda1fbbe63f5f8e79c20d823d990fa4
    public ViTien(String id, String name, double balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
    }

    // 3. Getter & Setter
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
}