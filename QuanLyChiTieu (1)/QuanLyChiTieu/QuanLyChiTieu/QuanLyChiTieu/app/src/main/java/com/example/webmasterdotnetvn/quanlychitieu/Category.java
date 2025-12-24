package com.example.webmasterdotnetvn.quanlychitieu;

import com.google.firebase.firestore.Exclude;

public class Category {
    private String id;        // ID của document (để Xóa/Sửa)
    private String name;      // Tên danh mục (Ăn uống, Lương...)
    private String type;      // Loại: "CHI" hoặc "THU"

    // Constructor rỗng (BẮT BUỘC để Firebase hoạt động)
    public Category() {
    }

    // Constructor đầy đủ để tạo đối tượng mới
    public Category(String id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    // --- Getter & Setter ---

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


}