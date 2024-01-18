package tn.monetique.cardmanagment.repository.userManagmentRepo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.monetique.cardmanagment.Entities.Auth_User.BankAdmin;


@Repository
public interface AdminBankRepository extends JpaRepository<BankAdmin, Long> {
  Optional<BankAdmin> findByUsername(String username);
  BankAdmin findByEmail(String emial);

  Boolean existsByUsername(String username);

  Boolean existsByEmail(String email);
  BankAdmin findByConfirmationToken (String confirmationToken);

  BankAdmin findByPasswordResetToken(String passwordResetToken);



}
