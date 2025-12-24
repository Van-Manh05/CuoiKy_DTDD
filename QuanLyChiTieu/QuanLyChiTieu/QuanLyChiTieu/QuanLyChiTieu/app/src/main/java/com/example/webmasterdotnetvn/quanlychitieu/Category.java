package com.example.webmasterdotnetvn.quanlychitieu;

public class Category {
    private String name;
    private int iconResId; // ID của ảnh (R.drawable.ic_...)
    private String colorHex; // Mã màu nền (ví dụ: "#E0F7FA")

    public Category(String name, int iconResId, String colorHex) {
        this.name = name;
        this.iconResId = iconResId;
        this.colorHex = colorHex;
    }

    public String getName() { return name; }
    public int getIconResId() { return iconResId; }
    public String getColorHex() { return colorHex; }
}