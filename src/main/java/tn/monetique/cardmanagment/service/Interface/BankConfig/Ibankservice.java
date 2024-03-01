package tn.monetique.cardmanagment.service.Interface.BankConfig;

import tn.monetique.cardmanagment.Entities.ConfigBank.Bank;
import tn.monetique.cardmanagment.payload.request.ConfigureDataRequest;
import tn.monetique.cardmanagment.payload.response.ConfigureDataResponse;


import java.util.List;
import java.util.Optional;

public interface Ibankservice {
    Bank creatBank(Bank bank);




    Bank updateBank(Long bankId, Bank updatedBank);

    List<Bank> getallbank();
    Optional<Bank> getbankbyid(Long bankId);


    boolean deleteBank(Long id);


    ConfigureDataResponse configureData(ConfigureDataRequest request, Long BinId);

    abstract ConfigureDataResponse GETconfigureDatabybin(Long BinId);
}
