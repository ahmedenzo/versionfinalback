package tn.monetique.cardmanagment.security.services;


import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tn.monetique.cardmanagment.Entities.Auth_User.AgentBank;
import tn.monetique.cardmanagment.repository.userManagmentRepo.AgentBankRepository;


@Service
public class CustomerDetailsServiceImpl implements UserDetailsService {

    @Autowired
    AgentBankRepository agentBankRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AgentBank agentBank = agentBankRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Agent not found with username: " + username));
        return UserDetailsImpl.build(agentBank);
    }


}