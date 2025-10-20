package vn.edu.fpt.cinemamanagement.services;

import org.springframework.stereotype.Service;
import vn.edu.fpt.cinemamanagement.entities.Customer;
import vn.edu.fpt.cinemamanagement.entities.Staff;
import vn.edu.fpt.cinemamanagement.repositories.CustomerRepository;
import vn.edu.fpt.cinemamanagement.repositories.StaffRepository;
import vn.edu.fpt.cinemamanagement.utils.HashUtil;

@Service
public class AuthService {
    private final CustomerRepository customerRepo;
    private final StaffRepository staffRepo;

    public AuthService(CustomerRepository customerRepo, StaffRepository staffRepo) {
        this.customerRepo = customerRepo;
        this.staffRepo = staffRepo;
    }

    public Staff checkStaffLogin(String username, String rawPassword) {
        System.out.println(">>> Username input: " + username);
        Staff s = staffRepo.findByUsername(username);
        System.out.println(">>> StaffRepo result: " + (s != null ? s.getUsername() : "NULL"));
        if (s != null && s.getPassword() != null) {
            String hashedInput = HashUtil.hashPassword(rawPassword);
            System.out.println(">>> Hashed input: " + hashedInput);
            System.out.println(">>> Password in DB: " + s.getPassword());
            if (hashedInput.equalsIgnoreCase(s.getPassword())) {
                System.out.println("✅ Login success for: " + username);
                return s;
            } else {
                System.out.println("Password mismatch");
            }
        } else {
            System.out.println(" Staff not found for username: " + username);
        }
        return null;
    }

    public Customer checkCustomerLogin(String username, String rawPassword) {
        Customer c = customerRepo.findByUsername(username);
        if (c != null && c.getPassword() != null) {
            if (HashUtil.checkPassword(rawPassword, c.getPassword())
                    || rawPassword.equals(c.getPassword())) {
                return c;
            }
        }
        return null;
    }
}
