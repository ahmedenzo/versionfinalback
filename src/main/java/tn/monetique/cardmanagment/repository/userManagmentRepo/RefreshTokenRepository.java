package tn.monetique.cardmanagment.repository.userManagmentRepo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tn.monetique.cardmanagment.Entities.Auth_User.BankAdmin;
import tn.monetique.cardmanagment.Entities.Auth_User.AgentBank;
import tn.monetique.cardmanagment.Entities.Auth_User.MonetiqueAdmin;
import tn.monetique.cardmanagment.Entities.Auth_User.RefreshToken;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
  RefreshToken findByToken(String token);

  @Query("SELECT r.bankAdmin.id FROM refreshtoken r WHERE r.token = :token")
  Long findBankAdminByToken(@Param("token") String token);

  @Modifying
  int deleteByBankAdmin(BankAdmin bankAdmin);


  @Modifying
  int deleteByAgentBank(AgentBank agentBank);
  @Modifying
  int deleteByMonetiqueAdmin(MonetiqueAdmin monetiqueAdmin);
}
