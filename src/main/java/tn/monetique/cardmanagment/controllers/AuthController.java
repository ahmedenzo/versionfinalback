package tn.monetique.cardmanagment.controllers;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import tn.monetique.cardmanagment.Utils.EmailService;
import tn.monetique.cardmanagment.exception.ResourceNotFoundException;
import tn.monetique.cardmanagment.Entities.Auth_User.*;
import tn.monetique.cardmanagment.payload.request.LoginRequest;
import tn.monetique.cardmanagment.payload.request.TokenRefreshRequest;
import tn.monetique.cardmanagment.payload.response.JwtResponse;
import tn.monetique.cardmanagment.payload.response.MessageResponse;
import tn.monetique.cardmanagment.payload.response.TokenRefreshResponse;
import tn.monetique.cardmanagment.payload.response.UserResponse;
import tn.monetique.cardmanagment.repository.userManagmentRepo.MonetiqueAdminRepo;
import tn.monetique.cardmanagment.repository.userManagmentRepo.RoleRepository;
import tn.monetique.cardmanagment.repository.userManagmentRepo.AdminBankRepository;
import tn.monetique.cardmanagment.security.jwt.JwtUtils;
import tn.monetique.cardmanagment.security.services.RefreshTokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import tn.monetique.cardmanagment.repository.userManagmentRepo.AgentBankRepository;
import tn.monetique.cardmanagment.exception.TokenRefreshException;
import tn.monetique.cardmanagment.security.services.UserDetailsImpl;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JavaMailSender javaMailSender;
    @Autowired
    EmailService emailService;
    @Autowired
    AdminBankRepository adminBankRepository;
    @Autowired
    AgentBankRepository AgentBankRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    MonetiqueAdminRepo monetiqueAdminRepo;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Autowired
    RefreshTokenService refreshTokenService;



    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            System.out.println("is "+authentication.isAuthenticated());

        } catch (BadCredentialsException ex) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Error: Invalid username or password"));

        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwt = jwtUtils.generateJwtToken(userDetails);

        List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
                .collect(Collectors.toList());
        RefreshToken refreshToken;
        System.out.println("role"+roles );
        if (roles.contains("Admin_Agence") || roles.contains("Simple_User")) {
            AgentBank agentBank = AgentBankRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User of agency Not Found with username: " + loginRequest.getUsername()));

            if (!agentBank.getConfirme()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new MessageResponse("Error: Email not confirmed for user: " + loginRequest.getUsername()));
            }

            refreshToken = refreshTokenService.createRefreshTokenForagent(agentBank.getId());
            System.out.println(jwt);
            System.out.println(refreshToken.getToken());
            System.out.println(roles);
            System.out.println(userDetails.getUsername());

            return ResponseEntity.ok(new JwtResponse(jwt,
                    refreshToken.getToken(),
                    agentBank.getId(),
                    agentBank.getUsername(),
                    agentBank.getEmail(),
                    roles

            ));
        } else if (roles.contains("Admin_SMT")) {
            MonetiqueAdmin monetiqueAdmin = monetiqueAdminRepo.findById(userDetails.getId())
                    .orElseThrow(() -> new UsernameNotFoundException("Monetique admin not found with id: " + userDetails.getId()));
            refreshToken = refreshTokenService.createRefreshTokenforMonetiqueadmin(monetiqueAdmin.getId());
            System.out.println("User ID for Monetique Admin: " + userDetails.getId());

            System.out.println(jwt);
            System.out.println(refreshToken.getToken());
            System.out.println(roles);
            System.out.println(userDetails.getUsername());

            return ResponseEntity.ok(new JwtResponse(jwt,
                    refreshToken.getToken(),
                    monetiqueAdmin.getId(),
                    monetiqueAdmin.getUsername(),
                    monetiqueAdmin.getEmail(),
                    roles));
        } else {
            BankAdmin bankAdmin = adminBankRepository.findById(userDetails.getId())
                    .orElseThrow(() -> new UsernameNotFoundException("BankAdmin not found with id: " + userDetails.getId()));

            if (!bankAdmin.getConfirme()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new MessageResponse("Error: Email not confirmed for bankAdmin: " + bankAdmin.getUsername()));
            }

            refreshToken = refreshTokenService.createRefreshTokenforadminbank(bankAdmin.getId());
            System.out.println(jwt);
            System.out.println(refreshToken.getToken());
            System.out.println(roles);
            System.out.println(userDetails.getUsername());

            return ResponseEntity.ok(new JwtResponse(jwt,
                    refreshToken.getToken(),
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    roles
            ));
        }
    }




    @GetMapping("/{username}")
    public ResponseEntity<UserResponse> getbyusername(@PathVariable("username") String username) {
        Optional<BankAdmin> optionalAdmin = adminBankRepository.findByUsername(username);
        Optional<AgentBank> optionalUser = AgentBankRepository.findByUsername(username);
        Optional<MonetiqueAdmin> optinalMonetique = monetiqueAdminRepo.findByUsername(username);

        if (optionalAdmin.isPresent()) {
            BankAdmin bankAdmin = optionalAdmin.get();
            UserResponse response = new UserResponse(bankAdmin.getId(),
                    bankAdmin.getUsername(), bankAdmin.getImage(), bankAdmin.getEmail(),bankAdmin.getFullname(),bankAdmin.getPhone());
            return ResponseEntity.ok(response);
        } else if (optionalUser.isPresent()) {
            AgentBank agentBank = optionalUser.get();
            UserResponse response = new UserResponse(agentBank.getId(),
                    agentBank.getUsername(), agentBank.getImage(), agentBank.getEmail(),agentBank.getFullname(),agentBank.getPhone());
            return ResponseEntity.ok(response);
        }
        else {
            throw new RuntimeException("Error: Bank Admin or Agent  not found Lotfi.");
        }
    }

    @GetMapping("/confirm")
    public ResponseEntity<?> confirmAccount(@RequestParam String confirmationToken) {
        BankAdmin bankAdmin = adminBankRepository.findByConfirmationToken(confirmationToken);
        AgentBank agentBank = AgentBankRepository.findByConfirmationToken(confirmationToken);
        if (bankAdmin != null) {
            bankAdmin.setConfirme(true);
            bankAdmin.setConfirmationToken(null);
            adminBankRepository.save(bankAdmin);
            return ResponseEntity.ok(new MessageResponse("BankAdmin confirmed"));
        } else if (agentBank != null) {
            agentBank.setConfirme(true);
            agentBank.setConfirmationToken(null);
            AgentBankRepository.save(agentBank);
            return ResponseEntity.ok(new MessageResponse("Customer confirmed"));
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("Invalid or expired token"));
        }
    }


    @GetMapping("/user")
    public ResponseEntity<?> getUserFromToken(@RequestHeader("Authorization") String tokenHeader) {
        try {
            String token = tokenHeader.substring(7);
            boolean isValid = jwtUtils.validateJwtToken(token);
            if (isValid) {
                String username = jwtUtils.getUserNameFromJwtToken(token);
                return ResponseEntity.ok(new MessageResponse("Username: " + username));
            } else {
                return ResponseEntity.badRequest().body(new MessageResponse("Invalid token"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
    @GetMapping("/{userId}/customers")
    public List<AgentBank> getAllCustomersForUser(@PathVariable Long userId, HttpServletResponse response) throws IOException, IOException {
        Optional<BankAdmin> user = adminBankRepository.findById(userId);
        if (!user.isPresent()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "BankAdmin not found");
            return null;
        }
        return AgentBankRepository.findByBankAdmin(user.get());
    }


    @GetMapping("/Admins")
    public List<BankAdmin> getAllBankadmis() throws IOException, IOException {
        List<BankAdmin> users = adminBankRepository.findAll();

        return users;
    }

    @GetMapping("/Admin/{username}")
    public ResponseEntity<?> getAllBankadmin(@PathVariable String username) throws IOException {
        BankAdmin user = adminBankRepository.findByUsername(username).orElse(null);
        AgentBank user2= AgentBankRepository.findByUsername(username).orElse(null);
        if (user != null) {
            String bankname = user.getBank().getBankName();
            Map<String, String> response = new HashMap<>();
            response.put("bankname", bankname);
            return ResponseEntity.ok(response);
        } else if(user2 !=null) {
            BankAdmin adminofuser = user2.getBankAdmin();
            String bankname = adminofuser.getBank().getBankName();
            Map<String, String> response = new HashMap<>();
            response.put("bankname", bankname);
            return ResponseEntity.ok(response);
        }
        else {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }
    @DeleteMapping("/{userId}/customers/{customerId}")
    public ResponseEntity<?> deleteCustomer(@PathVariable("userId") Long userId, @PathVariable("customerId") Long customerId) {
        BankAdmin bankAdmin = adminBankRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("BankAdmin not found"));
        AgentBank agentBank = bankAdmin.getAgentBanks().stream()
                .filter(c -> c.getId().equals(customerId))

                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        System.out.println(customerId);
        // Delete all refresh tokens associated with the customer
        refreshTokenService.deleteByCustomerId(customerId);


        bankAdmin.getAgentBanks().remove(agentBank);
        adminBankRepository.save(bankAdmin);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader(name = "Authorization") String authHeader) {
        String jwtToken = authHeader.substring(7); // remove "Bearer " prefix from token
        if (jwtUtils.validateJwtToken(jwtToken)) {
            String username = jwtUtils.getUserNameFromJwtToken(jwtToken);
            // fetch user profile using the username from token
            Optional<BankAdmin> userProfile = adminBankRepository.findByUsername(username);
            return ResponseEntity.ok(userProfile);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }



    private boolean verifyTwoFactorCode(UserDetailsImpl userDetails, String twoFactorCode) {
        // If 2FA is not enabled for the user, no need to verify the code
        if (!userDetails.getIsTwoFactorEnabled()) {
            return true;
        }

        String secretKey = userDetails.getTwoFactorCode();
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        int code = Integer.parseInt(twoFactorCode);
        return gAuth.authorize(secretKey, code);
    }



    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getBankAdmin)
                .map(user -> {
                    // Generate a new access token for the user
                    String accessToken = jwtUtils.generateTokenFromUsername(user.getUsername());

                    // Create a new refresh token for the user
                    RefreshToken newRefreshToken = refreshTokenService.createRefreshTokenforadminbank(user.getId());

                    // Return the new access token and refresh token in the response
                    return ResponseEntity.ok(new TokenRefreshResponse(accessToken, newRefreshToken.getToken()));
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                        "Refresh token is not in database or expired. Please sign in again."));
    }


    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getId();
        refreshTokenService.deleteByUserId(userId);
        return ResponseEntity.ok(new MessageResponse("Log out successful!"));
    }
    @GetMapping("/verifyToken")
    public ResponseEntity<String> verifyToken(@RequestParam("token") String token) {
        // Check if the token is valid
        if (jwtUtils.validateJwtToken(token)) {
            return ResponseEntity.ok("Token is valid");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token is not valid");
        }
    }

    //////////////////////ForgetPasse word /////////////////////


    @PostMapping("/forgot-password")

    public ResponseEntity<?> forgotPassword(@RequestParam String email) throws MessagingException {

        BankAdmin bankAdmin = adminBankRepository.findByEmail(email);

        AgentBank agentBank = AgentBankRepository.findByEmail(email);

        if (bankAdmin == null && agentBank == null) {

            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email not valid or not registered!"));

        }

        String passwordResetToken = UUID.randomUUID().toString();
        if (bankAdmin != null) {
            bankAdmin.setPasswordResetToken(passwordResetToken);
            adminBankRepository.save(bankAdmin);

        }

        if (agentBank != null) {
            agentBank.setPasswordResetToken(passwordResetToken);
            AgentBankRepository.save(agentBank);
        }
        String resetUrl = "http://localhost:4200/sessions/reset-pass?passwordResetToken=" + passwordResetToken;
        String message = "To reset your password, please click here: " + resetUrl;
        emailService.sendSimpleMessage(new Email(null, email, "Password Reset Request", message));
        return ResponseEntity.ok(new MessageResponse("A password reset email has been sent to your email address."));

    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String passwordResetToken, @RequestParam String password) {
        BankAdmin bankAdmin = adminBankRepository.findByPasswordResetToken(passwordResetToken);
        AgentBank agentBank = AgentBankRepository.findByPasswordResetToken(passwordResetToken);
        if (bankAdmin == null && agentBank == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Invalid or expired token"));
        }
        if (bankAdmin != null) {
            bankAdmin.setPassword(encoder.encode(password));
            bankAdmin.setPasswordResetToken(null);
            adminBankRepository.save(bankAdmin);
        }
        if (agentBank != null) {
            agentBank.setPassword(encoder.encode(password));
            agentBank.setPasswordResetToken(null);
            AgentBankRepository.save(agentBank);
        }
        return ResponseEntity.ok(new MessageResponse("Password reset successfully!"));
    }
    ///////////////////////////////////////////Change PassWord////////////////////////////////////////////////////////////
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            Authentication authentication) {

        // Get the authenticated user
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        System.out.println(userDetails.getUsername());
        System.out.println(userDetails.getId());

        // Check if the authenticated user is a BankAdmin
        BankAdmin bankAdmin = adminBankRepository.findById(userDetails.getId()).orElse(null);


        if (bankAdmin != null) {
            // Check if the old password is correct for the BankAdmin
            if (!passwordEncoder.matches(oldPassword, bankAdmin.getPassword())) {
                return ResponseEntity.badRequest().body(new MessageResponse("Old password is incorrect"));
            }

            // Validate new password and confirmation
            if (!newPassword.equals(confirmPassword)) {
                return ResponseEntity.badRequest().body(new MessageResponse("New password and confirmation do not match"));
            }

            // Hash the new password and update the BankAdmin's password
            String hashedPassword = passwordEncoder.encode(newPassword);
            bankAdmin.setPassword(hashedPassword);
            adminBankRepository.save(bankAdmin);
        } else {
            // If the authenticated user is not a BankAdmin, assume it's an AgentBank
            AgentBank agentbank = AgentBankRepository.findById(userDetails.getId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            // Check if the old password is correct for the AgentBank
            if (!passwordEncoder.matches(oldPassword, agentbank.getPassword())) {
                return ResponseEntity.badRequest().body(new MessageResponse("Old password is incorrect"));
            }

            // Validate new password and confirmation
            if (!newPassword.equals(confirmPassword)) {
                return ResponseEntity.badRequest().body(new MessageResponse("New password and confirmation do not match"));
            }

            // Hash the new password and update the AgentBank's password
            String hashedPassword = passwordEncoder.encode(newPassword);
            agentbank.setPassword(hashedPassword);
            AgentBankRepository.save(agentbank);
        }

        return ResponseEntity.ok(new MessageResponse("Password changed successfully"));
    }

}
