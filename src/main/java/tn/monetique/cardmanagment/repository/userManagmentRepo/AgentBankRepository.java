package tn.monetique.cardmanagment.repository.userManagmentRepo;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.monetique.cardmanagment.Entities.Auth_User.BankAdmin;
import tn.monetique.cardmanagment.Entities.Auth_User.AgentBank;

import java.util.List;
import java.util.Optional;

@Repository

public interface AgentBankRepository extends JpaRepository<AgentBank,Long> {
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);

    AgentBank findByEmail(String email);

    Optional<AgentBank> findByUsername(String username);

    List<AgentBank> findByBankAdmin(BankAdmin bankAdmin);

    AgentBank findByConfirmationToken (String confirmationToken);

    AgentBank findByPasswordResetToken(String passwordResetToken);
    Optional<AgentBank> findByBankAdminUsername(String username);

}