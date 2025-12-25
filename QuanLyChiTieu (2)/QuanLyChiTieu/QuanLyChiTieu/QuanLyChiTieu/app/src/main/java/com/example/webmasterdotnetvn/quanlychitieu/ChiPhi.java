// File: ChiPhi.java
package com.example.webmasterdotnetvn.quanlychitieu;

public class ChiPhi {
    private int iconResId;
    private String tenNhom;
    private double soTien;
    private double phanTramThayDoi;

    // Constructor (Hàm khởi tạo)
    public ChiPhi(int iconResId, String tenNhom, double soTien, double phanTramThayDoi) {
        this.iconResId = iconResId;
        this.tenNhom = tenNhom;
        this.soTien = soTien;
        this.phanTramThayDoi = phanTramThayDoi;
    }

    
    public int getIconResId() { return iconResId; }
    public String getTenNhom() { return tenNhom; }
    public double getSoTien() { return soTien; }
    public double getPhanTramThayDoi() { return phanTramThayDoi; }
}