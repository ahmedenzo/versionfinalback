package tn.monetique.cardmanagment.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import tn.monetique.cardmanagment.Entities.Auth_User.BankAdmin;
import tn.monetique.cardmanagment.repository.userManagmentRepo.AdminBankRepository;

@Component
public class AdminDetailsServiceImpl implements UserDetailsService {
  @Autowired
  AdminBankRepository adminBankRepository;


  @Transactional(readOnly = true) // Mark this method as read-only to avoid unintentional updates
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    BankAdmin bankAdmin = adminBankRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("BankAdmin Not Found with username: " + username));
      return UserDetailsImpl.build(bankAdmin);
    }


}
