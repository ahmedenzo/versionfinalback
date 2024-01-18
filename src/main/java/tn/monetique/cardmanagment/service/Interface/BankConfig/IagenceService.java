package tn.monetique.cardmanagment.service.Interface.BankConfig;

import tn.monetique.cardmanagment.Entities.ConfigBank.Agence;


import java.util.List;

public interface IagenceService {




    Agence createAgence(Agence agence, String bankname);

    Agence UpdateAgence(Long agenceId, Agence updatedAgency);



    List<Agence> getallagencebyBank(String BankName);

    List<Agence> getallagence();
    boolean deleteAgence(Long agenceId);

    boolean agencyofbank(Long bankId);
}
