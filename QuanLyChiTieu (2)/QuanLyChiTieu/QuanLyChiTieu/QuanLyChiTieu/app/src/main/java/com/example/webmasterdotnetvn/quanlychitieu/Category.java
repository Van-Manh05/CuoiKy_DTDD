package com.example.webmasterdotnetvn.quanlychitieu;

import com.google.firebase.firestore.Exclude;

public class Category {
    private String id;
    private String name;
    private String type;

    
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