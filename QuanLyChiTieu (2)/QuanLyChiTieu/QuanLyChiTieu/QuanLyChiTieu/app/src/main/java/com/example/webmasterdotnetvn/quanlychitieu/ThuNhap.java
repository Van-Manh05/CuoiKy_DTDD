
package com.example.webmasterdotnetvn.quanlychitieu;

public class ThuNhap {
    private int iconResId;
    private String tenNguon;
    private double soTien;

    // Constructor
    public ThuNhap(int iconResId, String tenNguon, double soTien) {
        this.iconResId = iconResId;
        this.tenNguon = tenNguon;
        this.soTien = soTien;
    }

    // Getters
    public int getIconResId() { return iconResId; }
    public String getTenNguon() { return tenNguon; }
    public double getSoTien() { return soTien; }
}