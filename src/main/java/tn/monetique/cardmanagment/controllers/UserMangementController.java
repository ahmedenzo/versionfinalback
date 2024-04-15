package tn.monetique.cardmanagment.controllers;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.monetique.cardmanagment.Utils.EmailService;
import tn.monetique.cardmanagment.Entities.Auth_User.*;
import tn.monetique.cardmanagment.Entities.ConfigBank.Bank;
import tn.monetique.cardmanagment.payload.request.CompletProfile;
import tn.monetique.cardmanagment.payload.request.SignupRequest;
import tn.monetique.cardmanagment.payload.response.MessageResponse;
import tn.monetique.cardmanagment.repository.Bank.AgencyRepository;
import tn.monetique.cardmanagment.repository.Bank.BankRepository;
import tn.monetique.cardmanagment.repository.userManagmentRepo.AdminBankRepository;
import tn.monetique.cardmanagment.repository.userManagmentRepo.MonetiqueAdminRepo;
import tn.monetique.cardmanagment.repository.userManagmentRepo.RoleRepository;
import tn.monetique.cardmanagment.security.jwt.JwtUtils;
import tn.monetique.cardmanagment.security.services.RefreshTokenService;
import tn.monetique.cardmanagment.security.services.UserDetailsImpl;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class UserMangementController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JavaMailSender javaMailSender;
    @Autowired
    EmailService emailService;
    @Autowired
    AdminBankRepository adminBankRepository;
    @Autowired
    tn.monetique.cardmanagment.repository.userManagmentRepo.AgentBankRepository AgentBankRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Autowired
    RefreshTokenService refreshTokenService;
    @Autowired
    private BankRepository bankRepository;
    @Autowired
    private AgencyRepository agencyRepository;
    @Autowired
    private MonetiqueAdminRepo monetiqueAdminRepo;

    @PostMapping("/signup/Monetique")
    public ResponseEntity<?> registerAdminmonetique(@RequestBody SignupRequest signupRequest) {
        if (monetiqueAdminRepo.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }
        if (monetiqueAdminRepo.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already in use!"));
        }
        if (AgentBankRepository.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already in use!"));
        }
        if (adminBankRepository.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already in use!"));
        }
        if (adminBankRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }
        if (AgentBankRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        MonetiqueAdmin monetiqueAdmin = new MonetiqueAdmin(signupRequest.getUsername(),
                signupRequest.getEmail(), passwordEncoder.encode(signupRequest.getPassword()));
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.Admin_SMT)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(userRole);
        monetiqueAdmin.setRoles(roles);

        monetiqueAdminRepo.save(monetiqueAdmin);

        return ResponseEntity.ok(new MessageResponse("Monetique admin added succesfuly"));
    }

    @PostMapping("/signup/adminbank")
    public ResponseEntity<?> registerAdminBank(@RequestBody SignupRequest signupRequest, @RequestParam String bankname) {
        if (adminBankRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }
        if (AgentBankRepository.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already in use!"));
        }
        if (adminBankRepository.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already in use!"));
        }

        BankAdmin bankAdmin = new BankAdmin(signupRequest.getUsername(),
                signupRequest.getEmail(), passwordEncoder.encode(signupRequest.getPassword()));
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.Admin_Bank)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(userRole);
        bankAdmin.setRoles(roles);
        bankAdmin.setConfirmationToken(UUID.randomUUID().toString());
        Bank bank = bankRepository.findByBankName(bankname).orElse(null);
        bankAdmin.setBank(bank);
        adminBankRepository.save(bankAdmin);

        String confirmationUrl = "http://localhost:8085/api/auth/confirm?confirmationToken=" + bankAdmin.getConfirmationToken();
        String message = "To confirm your account, please click here: " + confirmationUrl;

        emailService.sendSimpleMessage(new Email("hamza.melki@monetiquetunisie.com", signupRequest.getEmail(), "Account Confirmation", message));

        return ResponseEntity.ok(new MessageResponse("A confirmation email has been sent to your email address."));
    }

    @PostMapping("/signup/{agenceName}/Agentbank")
    public ResponseEntity<?> registerAgentUser(@RequestBody SignupRequest signupRequest, @RequestParam Long Adminid,
                                               @PathVariable String agenceName) {
        if (adminBankRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }
        if (adminBankRepository.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already in use!"));
        }
        if (AgentBankRepository.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already in use!"));
        }

        BankAdmin bankAdmin = adminBankRepository.findById(Adminid)
                .orElseThrow(() -> new RuntimeException("Error: Customer not found."));

        // Create new customer bankAdmin's account
        AgentBank agentBank = new AgentBank(signupRequest.getUsername(),
                signupRequest.getEmail(),
                passwordEncoder.encode(signupRequest.getPassword()));
        Set<Role> roles = new HashSet<>();
        Role AgentUserRole = roleRepository.findByName(ERole.Simple_User)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(AgentUserRole);
        agentBank.setRoles(roles);

        agentBank.setBankAdmin(bankAdmin);
        agentBank.setConfirmationToken(UUID.randomUUID().toString());
        agentBank.setAgence(agencyRepository.findByAgenceName(agenceName));
        AgentBankRepository.save(agentBank);

        String confirmationUrl = "http://localhost:8085/api/auth/confirm?confirmationToken=" + agentBank.getConfirmationToken();
        String message = "Your BankAdmin " + agentBank.getUsername() + "Your Email is :" + agentBank.getEmail() + "--Yor Password is :" + signupRequest.getPassword() + "your Provider is" + agentBank.getBankAdmin().getUsername() + "To confirm your account, please click here: " + confirmationUrl;

        emailService.sendSimpleMessage(new Email("hamza.melki@monetiquetunisie.com", signupRequest.getEmail(), "Account Confirmation", message));

        return ResponseEntity.ok(new MessageResponse("A confirmation email has been sent to your email address."));
    }

    @PutMapping("/Updateid/{agentbankid}/role")
    public ResponseEntity<?> updateAgentBankRole(@PathVariable Long agentbankid, @RequestParam ERole roleName, Authentication authentication) {
        // Get the authenticated user's ID
        Long userId = ((UserDetailsImpl) authentication.getPrincipal()).getId();

        // Find the customer by ID
        AgentBank agentBank = AgentBankRepository.findById(agentbankid)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        // Check if the authenticated user is the owner of the customer
        if (!agentBank.getBankAdmin().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse(false, "You are not authorized to update this customer"));
        }

        // Find the role by name
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));

        // Update the customer's roles
        agentBank.getRoles().clear();
        agentBank.getRoles().add(role);
        AgentBankRepository.save(agentBank);

        return ResponseEntity.ok(new ApiResponse(true, "Customer role updated successfully"));
    }

    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = roleRepository.findAll();

        // Filter out roles that should be excluded
        roles = roles.stream()
                .filter(role -> !role.getName().equals(ERole.Admin_SMT) && !role.getName().equals(ERole.Admin_Bank))
                .collect(Collectors.toList());

        return ResponseEntity.ok(roles);
    }

    /////////////////////////////Complete Profile /////////////////////////////////////////
    @PostMapping("/completeProfile")
    public ResponseEntity<?> completeProfile(@Valid @RequestBody CompletProfile completProfileRequest) {
        // Get the user object by username from the database
        Optional<BankAdmin> optionalbankAdmin = adminBankRepository.findByUsername(completProfileRequest.getUsername());
        Optional<AgentBank> optionalagentBank = AgentBankRepository.findByUsername(completProfileRequest.getUsername());


        // Update the user object with the new profile information
        if (optionalbankAdmin.isPresent()) {
            BankAdmin bankAdmin = optionalbankAdmin.get();
            bankAdmin.setFullname(completProfileRequest.getFullname());
            bankAdmin.setPhone(completProfileRequest.getPhone());
            bankAdmin.setAdresse(completProfileRequest.getAdresse());

            adminBankRepository.save(bankAdmin);
        } else if (optionalagentBank.isPresent()) {
            AgentBank agentBank = optionalagentBank.get();

            agentBank.setFullname(completProfileRequest.getFullname());
            agentBank.setPhone(completProfileRequest.getPhone());
            agentBank.setAdresse(completProfileRequest.getAdresse());
            AgentBankRepository.save(agentBank);
        }
        return ResponseEntity.ok(new MessageResponse("Your profile has been updated successfully."));
    }

    //////////////////////////////Upload Image//////////////////////
    @PostMapping("/{userId}/image")
    public ResponseEntity<?> uploadImage(@PathVariable Long userId, @RequestParam("file") MultipartFile file) {
        try {
            // Get the bankAdmin or customer object by ID from the database
            BankAdmin bankAdmin = adminBankRepository.findById(userId).orElse(null);
            AgentBank agentBank = AgentBankRepository.findById(userId).orElse(null);

            // Check if the bankAdmin or customer exists
            if (bankAdmin == null && agentBank == null) {
                return ResponseEntity.notFound().build();
            }

            // Check if the file is not empty and is an image
            if (file.isEmpty() || !file.getContentType().startsWith("image/")) {
                return ResponseEntity.badRequest().build();
            }

            // Save the image to a file on the server
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            String uploaddir = "bankAdmin-images/" + userId;
            String fileExtension = fileName.substring(fileName.lastIndexOf("."));
            String newFileName = UUID.randomUUID().toString() + fileExtension;
            Path uploadPath = Paths.get(uploaddir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            try (InputStream inputStream = file.getInputStream()) {
                Path filePath = uploadPath.resolve(newFileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }

            // Update the bankAdmin or customer object with the new image file name
            if (bankAdmin != null) {
                bankAdmin.setImage(newFileName);
                adminBankRepository.save(bankAdmin);
            }
            if (agentBank != null) {
                agentBank.setImage(newFileName);
                AgentBankRepository.save(agentBank);
            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{userId}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable Long userId) {
        // Get the bankAdmin or customer object by ID from the database
        BankAdmin bankAdmin = adminBankRepository.findById(userId).orElse(null);
        AgentBank agentBank = AgentBankRepository.findById(userId).orElse(null);

        // Check if the bankAdmin or customer exists and has an image
        if ((bankAdmin == null || bankAdmin.getImage() == null) && (agentBank == null || agentBank.getImage() == null)) {
            return ResponseEntity.notFound().build();
        }

        try {
            // Read the image file from disk
            String imageName;
            if (bankAdmin != null) {
                imageName = bankAdmin.getImage();
            } else {
                imageName = agentBank.getImage();
            }
            Path imagePath = Paths.get("bankAdmin-images/" + userId + "/" + imageName);
            byte[] imageBytes = Files.readAllBytes(imagePath);

            // Set the response headers to indicate the image content type
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);

            // Return the image data as a ResponseEntity with the appropriate headers
            return new ResponseEntity<byte[]>(imageBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{bankAdminId}/toggle-activeadmin")
    public ResponseEntity<?> toggleBankAdminActive(@PathVariable Long bankAdminId, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
                .collect(Collectors.toList());

        BankAdmin bankAdmin = adminBankRepository.findById(bankAdminId)
                .orElseThrow(() -> new IllegalArgumentException("BankAdmin not found"));

        if (roles.contains("Admin_SMT")) {
            bankAdmin.setActive(!bankAdmin.getActive());
            adminBankRepository.save(bankAdmin);

            if (!bankAdmin.getActive()) {
                for (AgentBank agentBank : bankAdmin.getAgentBanks()) {
                    agentBank.setActive(bankAdmin.getActive());
                    AgentBankRepository.save(agentBank);
                }
            }
            return ResponseEntity.ok().build(); // Return 200 OK
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Return 401 Unauthorized
        }
    }
    @PutMapping("/{bankAgentId}/toggle-activeAgent")
    public ResponseEntity<?>toggleagentBankActive(@PathVariable Long bankAgentId,Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
                .collect(Collectors.toList());
        AgentBank bankAgent = AgentBankRepository.findById(bankAgentId)
                .orElseThrow(() -> new IllegalArgumentException("AgentBank not found"));
        if (roles.contains("Admin_Bank")) {
            // Toggle the active attribute
            bankAgent.setActive(!bankAgent.getActive());
            AgentBankRepository.save(bankAgent);

            return ResponseEntity.ok().build(); // Return 200 OK
        }else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Return 401 Unauthorized
        }
    }

    @PutMapping("/{bankAdminId}/toggleactivesadmin")
    public void testtes(@PathVariable Long bankAdminId) {
        BankAdmin bankAdmin = adminBankRepository.findById(bankAdminId)
                .orElseThrow(() -> new IllegalArgumentException("BankAdmin not found"));
        System.out.println("userbank" +bankAdmin);

            // Toggle the active attribute
            bankAdmin.setActive(!bankAdmin.getActive());
            adminBankRepository.save(bankAdmin);

            // Toggle the active attribute of associated AgentBanks
            if (!bankAdmin.getActive()) {
                for (AgentBank agentBank : bankAdmin.getAgentBanks()) {
                    agentBank.setActive(bankAdmin.getActive());
                    AgentBankRepository.save(agentBank);
                }
            }


        }

}
