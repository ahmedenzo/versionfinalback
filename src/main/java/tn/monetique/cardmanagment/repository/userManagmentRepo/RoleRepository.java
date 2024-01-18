package tn.monetique.cardmanagment.repository.userManagmentRepo;

import java.util.Optional;

import tn.monetique.cardmanagment.Entities.Auth_User.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tn.monetique.cardmanagment.Entities.Auth_User.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByName(ERole name);

}
