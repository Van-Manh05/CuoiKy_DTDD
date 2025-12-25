// File: UserDatabase.java
package com.example.webmasterdotnetvn.quanlychitieu; // Giữ đúng package của bạn

// Lớp này dùng để "giả lập" một data base,
// Nó dùng biến static để giữ thông tin,
// thông tin này sẽ mất khi app bị tắt hẳn.
public class UserDatabase {

    public static String savedEmail = null;
    public static String savedPassword = null;

}