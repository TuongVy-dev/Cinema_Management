package vn.edu.fpt.cinemamanagement.controller.api;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.cinemamanagement.dto.request.LoginRequestDTO;
import vn.edu.fpt.cinemamanagement.dto.request.RegisterRequestDTO;
import vn.edu.fpt.cinemamanagement.dto.response.UserInfoResponseDTO;
import vn.edu.fpt.cinemamanagement.entities.Customer;
import vn.edu.fpt.cinemamanagement.entities.Staff;
import vn.edu.fpt.cinemamanagement.repositories.CustomerRepository;
import vn.edu.fpt.cinemamanagement.repositories.StaffRepository;
import vn.edu.fpt.cinemamanagement.services.CustomerService;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    private final AuthenticationManager authenticationManager;
    private final CustomerService customerService;
    private final CustomerRepository customerRepository;
    private final StaffRepository staffRepository;

    public AuthRestController(AuthenticationManager authenticationManager,
                              CustomerService customerService,
                              CustomerRepository customerRepository,
                              StaffRepository staffRepository) {
        this.authenticationManager = authenticationManager;
        this.customerService = customerService;
        this.customerRepository = customerRepository;
        this.staffRepository = staffRepository;
    }

    // ========================== LOGIN ==========================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO dto, HttpServletRequest request) {
        try {
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword());
            Authentication authentication = authenticationManager.authenticate(authToken);

            // Set SecurityContext and persist to HttpSession
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);

            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", context);

            // Determine role
            String role = authentication.getAuthorities().stream()
                    .map(a -> a.getAuthority())
                    .findFirst()
                    .orElse("ROLE_USER");

            // Find user ID
            String userId = "";
            String username = authentication.getName();
            
            Customer c = customerRepository.findByUsername(username).orElse(null);
            if (c != null) {
                userId = c.getUser_id();
            } else {
                Staff s = staffRepository.findByUsername(username).orElse(null);
                if (s != null) {
                    userId = s.getStaffID();
                }
            }

            UserInfoResponseDTO userInfo = new UserInfoResponseDTO(userId, username, role);
            return ResponseEntity.ok(userInfo);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid username or password"));
        }
    }

    // ========================== GET CURRENT USER ==========================
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Not authenticated."));
        }

        String username = principal.getName();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String role = auth.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .findFirst()
                .orElse("ROLE_USER");

        String userId = "";
        Customer c = customerRepository.findByUsername(username).orElse(null);
        if (c != null) {
            userId = c.getUser_id();
        } else {
            Staff s = staffRepository.findByUsername(username).orElse(null);
            if (s != null) {
                userId = s.getStaffID();
            }
        }

        return ResponseEntity.ok(new UserInfoResponseDTO(userId, username, role));
    }

    // ========================== LOGOUT ==========================
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        // Invalidate session
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // Clear SecurityContext
        SecurityContextHolder.clearContext();

        // Delete JSESSIONID cookie
        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return ResponseEntity.ok(Map.of("message", "Logged out successfully."));
    }

    // ========================== REGISTER ==========================
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO dto) {
        // Map DTO to Customer entity
        Customer customer = new Customer();
        customer.setUsername(dto.getUsername());
        customer.setDob(dto.getDob());
        customer.setSex(dto.getSex());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setPassword(dto.getPassword());
        customer.setVerify("active");

        // Reuse existing CustomerService.registerCustomer() which does all validation
        Map<String, String> errors = customerService.registerCustomer(customer, dto.getConfirmPassword());

        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        return ResponseEntity.ok(Map.of("message", "Registration successful."));
    }
}
