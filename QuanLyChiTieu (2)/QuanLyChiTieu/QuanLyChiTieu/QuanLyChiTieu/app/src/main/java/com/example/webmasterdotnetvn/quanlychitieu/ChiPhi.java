// File: ChiPhi.java
package com.example.webmasterdotnetvn.quanlychitieu;

public class ChiPhi {
    private int iconResId; // ID của icon (ví dụ: R.drawable.ic_home)
    private String tenNhom; // Tên (ví dụ: "Nhà cửa")
    private double soTien; // Số tiền (ví dụ: 6000000)
    private double phanTramThayDoi; // Thay đổi (ví dụ: 6000000)

    // Constructor (Hàm khởi tạo)
    public ChiPhi(int iconResId, String tenNhom, double soTien, double phanTramThayDoi) {
        this.iconResId = iconResId;
        this.tenNhom = tenNhom;
        this.soTien = soTien;
        this.phanTramThayDoi = phanTramThayDoi;
    }

    // Getters (Hàm để lấy thông tin ra)
    public int getIconResId() { return iconResId; }
    public String getTenNhom() { return tenNhom; }
    public double getSoTien() { return soTien; }
    public double getPhanTramThayDoi() { return phanTramThayDoi; }
}