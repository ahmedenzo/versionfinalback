package tn.monetique.cardmanagment.repository.userManagmentRepo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.monetique.cardmanagment.Entities.Auth_User.MonetiqueAdmin;

import java.util.Optional;

@Repository
public interface MonetiqueAdminRepo extends JpaRepository<MonetiqueAdmin, Long> {

    Optional<MonetiqueAdmin> findByUsername(String username);
    MonetiqueAdmin findByEmail(String emial);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);
}
