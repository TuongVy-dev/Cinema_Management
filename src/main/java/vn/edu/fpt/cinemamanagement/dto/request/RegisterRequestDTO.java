package vn.edu.fpt.cinemamanagement.dto.request;

import java.time.LocalDate;

public class RegisterRequestDTO {
    private String username;
    private LocalDate dob;
    private Boolean sex;
    private String email;
    private String phone;
    private String password;
    private String confirmPassword;

    public RegisterRequestDTO() {}

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }
    public Boolean getSex() { return sex; }
    public void setSex(Boolean sex) { this.sex = sex; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}
