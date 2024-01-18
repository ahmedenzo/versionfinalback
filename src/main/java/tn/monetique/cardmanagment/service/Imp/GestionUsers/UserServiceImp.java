package tn.monetique.cardmanagment.service.Imp.GestionUsers;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import jakarta.servlet.http.HttpServletRequest;
import tn.monetique.cardmanagment.Entities.Auth_User.BankAdmin;
import tn.monetique.cardmanagment.repository.userManagmentRepo.RefreshTokenRepository;
import tn.monetique.cardmanagment.repository.userManagmentRepo.AdminBankRepository;
import tn.monetique.cardmanagment.security.services.UserDetailsImpl;
import tn.monetique.cardmanagment.service.Interface.GestionUserInterface.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImp implements UserService {
    @Autowired
    private AdminBankRepository adminBankRepository;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private HttpServletRequest request;

    private String getTwoFactorCodeFromRequest() {
        return request.getHeader("Two-Factor-Code");
    }

    @Override
    public BankAdmin CreateUser(BankAdmin bankAdmin) {

        return adminBankRepository.save(bankAdmin);
    }

    @Override
    public BankAdmin UpdateUser(BankAdmin bankAdmin) {
        return adminBankRepository.save(bankAdmin);
    }

    @Override
    public List<BankAdmin> AllUser() {

        return adminBankRepository.findAll();
    }

    @Override
    public BankAdmin GetbyId(Long id) {
        return adminBankRepository.findById(id).orElse(null);
    }

    @Override
    public void DeleteUser(Long id) {
        adminBankRepository.deleteById(id);
    }


    @Override
    public BankAdmin getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String username = userDetails.getUsername();
        System.out.println("username");
        Optional<BankAdmin> optionalUser = adminBankRepository.findByUsername(username);

        // Check if the bankAdmin exists in the database
        if (optionalUser.isEmpty()) {
            return null;
        }

        BankAdmin bankAdmin = optionalUser.get();
        String twoFactorCode = getTwoFactorCodeFromRequest();

        // If 2FA code is provided, verify it
        if (twoFactorCode != null) {
            // Check if the bankAdmin has 2FA enabled
            if (bankAdmin.getTwoFactorCode() == null || bankAdmin.getTwoFactorCode().isEmpty()) {
                // BankAdmin does not have 2FA enabled
                return bankAdmin;
            }

            // Get the Google Authenticator secret key from the bankAdmin
            String secretKey = bankAdmin.getTwoFactorCode();

            // Verify the 2FA code using the Google Authenticator
            GoogleAuthenticator gAuth = new GoogleAuthenticator();
            int code = Integer.parseInt(twoFactorCode);
            boolean is2faCodeValid = gAuth.authorize(secretKey, code);

            // If 2FA code is not valid, return null
            if (!is2faCodeValid) {
                return null;
            }
        }
        return bankAdmin;
    }
    @Override
    public boolean isUserIdMatchingToken(Long bankadminId, String token) {
        Long tokenUserId = refreshTokenRepository.findBankAdminByToken(token);
        return tokenUserId != null && tokenUserId.equals(bankadminId);
    }
}

