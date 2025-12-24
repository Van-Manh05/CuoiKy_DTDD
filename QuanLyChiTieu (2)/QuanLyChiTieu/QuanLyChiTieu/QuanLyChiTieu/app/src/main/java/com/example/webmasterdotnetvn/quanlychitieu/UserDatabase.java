// File: UserDatabase.java
package com.example.webmasterdotnetvn.quanlychitieu; // Giữ đúng package của bạn

// Lớp này dùng để "giả lập" một database
// Nó dùng biến static để giữ thông tin,
// thông tin này sẽ mất khi app bị tắt hẳn.
public class UserDatabase {

    // Mình tạm thời chỉ cho lưu 1 tài khoản thôi nhé
    public static String savedEmail = null;
    public static String savedPassword = null;

}