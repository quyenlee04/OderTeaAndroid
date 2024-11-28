package com.example.oderteaandroid;

public class UserData {
    private String email;
    private String phone;
    private String address;
    private String avatar;

    public UserData(String email, String phone, String address, String avatar) {
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
