// File: ViTien.java
package com.example.webmasterdotnetvn.quanlychitieu;

public class ViTien {
    private int iconResId;
    private String tenVi;
    private double soTien;

    // Constructor
    public ViTien(int iconResId, String tenVi, double soTien) {
        this.iconResId = iconResId;
        this.tenVi = tenVi;
        this.soTien = soTien;
    }

    // Getters
    public int getIconResId() { return iconResId; }
    public String getTenVi() { return tenVi; }
    public double getSoTien() { return soTien; }
}