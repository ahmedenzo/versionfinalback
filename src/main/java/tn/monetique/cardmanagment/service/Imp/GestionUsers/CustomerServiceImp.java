package tn.monetique.cardmanagment.service.Imp.GestionUsers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.monetique.cardmanagment.Entities.Auth_User.AgentBank;
import tn.monetique.cardmanagment.repository.userManagmentRepo.AgentBankRepository;
import tn.monetique.cardmanagment.service.Interface.GestionUserInterface.CustomerService;

@Service
public class CustomerServiceImp implements CustomerService {
    @Autowired
    private AgentBankRepository agentBankRepository;

    @Override
    public AgentBank CreateCustomer(AgentBank agentBank) {
        return agentBankRepository.save(agentBank);
    }


}