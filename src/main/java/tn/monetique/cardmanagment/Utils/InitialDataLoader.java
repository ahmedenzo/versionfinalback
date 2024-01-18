package tn.monetique.cardmanagment.Utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tn.monetique.cardmanagment.Entities.Auth_User.ERole;
import tn.monetique.cardmanagment.Entities.Auth_User.MonetiqueAdmin;
import tn.monetique.cardmanagment.Entities.Auth_User.Role;
import tn.monetique.cardmanagment.controllers.UserMangementController;
import tn.monetique.cardmanagment.payload.request.SignupRequest;
import tn.monetique.cardmanagment.repository.userManagmentRepo.MonetiqueAdminRepo;
import tn.monetique.cardmanagment.repository.userManagmentRepo.RoleRepository;

@Component
public class InitialDataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;
    @Autowired
    private MonetiqueAdminRepo monetiqueAdminRepo;
    @Autowired
    UserMangementController userMangementController;

    @Autowired
    public InitialDataLoader(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Check if roles already exist

        if (roleRepository.findByName(ERole.Admin_SMT).isEmpty()) {
            // Role doesn't exist, create and save it
            Role adminRole = new Role();
            adminRole.setName(ERole.Admin_SMT);
            roleRepository.save(adminRole);
        }

        if (roleRepository.findByName(ERole.Admin_Bank).isEmpty()) {
            // Role doesn't exist, create and save it
            Role userRole = new Role();
            userRole.setName(ERole.Admin_Bank);
            roleRepository.save(userRole);
        }
        if (roleRepository.findByName(ERole.Simple_User).isEmpty()) {
            // Role doesn't exist, create and save it
            Role userRole = new Role();
            userRole.setName(ERole.Simple_User);
            roleRepository.save(userRole);
        }
        if (roleRepository.findByName(ERole.Admin_Agence).isEmpty()) {
            // Role doesn't exist, create and save it
            Role userRole = new Role();
            userRole.setName(ERole.Admin_Agence);
            roleRepository.save(userRole);
        }




        if (monetiqueAdminRepo.findByUsername("AdminSMT").isEmpty()) {
            SignupRequest signupRequest = new SignupRequest();
            signupRequest.setUsername("AdminSMT");
            signupRequest.setEmail("SMT@monetique.com");
            signupRequest.setPassword("SMT2024**++");
            userMangementController.registerAdminmonetique(signupRequest);
            System.out.println("Admin registered successfully!");
        } else {
            System.out.println("Admin user already exists. Skipping registration.");
        }
}
}

