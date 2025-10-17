package vn.edu.fpt.cinemamanagement.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Staff")
public class Staff {
    @Id
    @Column(name = "staff_id")
    private String staffID;

    @Column(name = "full_name")
    private String fullName;

    private String position;

    private String username;

    private String password;

    private String phone;

    private String email;

    public Staff() {}
    // constructor
    public Staff(String staffID, String fullName, String position, String username, String password, String phone, String email) {
        this.staffID = staffID;
        this.fullName = fullName;
        this.position = position;
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.email = email;
    }
    // getter and setter
    public String getStaffID() {
        return staffID;
    }

    public void setStaffID(String staffID) {
        this.staffID = staffID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}