package com.example.webmasterdotnetvn.quanlychitieu;

public class NganSach {
    private String id;
    private String category; // Tên danh mục (Vd: Ăn uống)
    private double limitAmount; // Hạn mức (Vd: 5.000.000)
    private double spentAmount; // Đã chi (Tính từ giao dịch)

    public NganSach() { }

    public NganSach(String id, String category, double limitAmount, double spentAmount) {
        this.id = id;
        this.category = category;
        this.limitAmount = limitAmount;
        this.spentAmount = spentAmount;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public double getLimitAmount() { return limitAmount; }
    public void setLimitAmount(double limitAmount) { this.limitAmount = limitAmount; }
    public double getSpentAmount() { return spentAmount; }
    public void setSpentAmount(double spentAmount) { this.spentAmount = spentAmount; }
}