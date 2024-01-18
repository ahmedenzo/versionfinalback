package tn.monetique.cardmanagment.service.Interface.GestionUserInterface;



import tn.monetique.cardmanagment.Entities.Auth_User.BankAdmin;

import java.util.List;

public interface UserService {
    BankAdmin CreateUser(BankAdmin bankAdmin);
    BankAdmin UpdateUser(BankAdmin bankAdmin);

    List<BankAdmin>AllUser();
    BankAdmin GetbyId (Long id);
    void DeleteUser(Long id);

    BankAdmin getCurrentUser();

    boolean isUserIdMatchingToken(Long userId, String token);
}
