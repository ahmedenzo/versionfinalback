package tn.monetique.cardmanagment.security.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tn.monetique.cardmanagment.Entities.Auth_User.MonetiqueAdmin;
import tn.monetique.cardmanagment.repository.userManagmentRepo.MonetiqueAdminRepo;

@Service
public class MonetiqueadminDetailsServiceImpl implements UserDetailsService {

    @Autowired
    MonetiqueAdminRepo monetiqueAdminRepo;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MonetiqueAdmin monetiqueAdmin = monetiqueAdminRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Monetique admin compte not found with username: " + username));

        return UserDetailsImpl.build(monetiqueAdmin);
    }


}
