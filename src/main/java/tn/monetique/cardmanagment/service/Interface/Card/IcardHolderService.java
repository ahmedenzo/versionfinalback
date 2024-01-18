package tn.monetique.cardmanagment.service.Interface.Card;

import org.springframework.security.core.Authentication;
import tn.monetique.cardmanagment.Entities.DataInputCard.CardHolder;
import tn.monetique.cardmanagment.Entities.ConfigBank.Agence;
import tn.monetique.cardmanagment.Entities.ConfigBank.Bin;

import java.util.List;

public interface IcardHolderService {
    CardHolder DataINPutpreparation(CardHolder cardHolder, String bankname, Bin Selectedbin, Agence Useragence, String username);
    CardHolder createNewCard(CardHolder cardHolder, Long SelectedbinId, Authentication authentication);

    CardHolder updategeneratedcard(Long customerId, Long selectedBinId, CardHolder updatedData, Authentication authentication);

    CardHolder UpdateDataInput(Long customerId, Long SelectedbinId, CardHolder updatedData, Authentication authentication);
    CardHolder getCardHolderById(Long customerId);
    List<CardHolder> getAllCardHolders(Authentication authentication);

    List<CardHolder> getAllCardHolderbyagency(String branchCode);
    List<CardHolder> getAllCardHolderbyBank(String Bankname);
    void deleteDataInput(Long customerId);



    CardHolder checkgeneratedfile(Long customerId);
    CardHolder resetCardGenerated(Long customerId);
    CardHolder Confirmation(Long customerId);
    /////////////////generation card input Data file ///////////////////////////////////////////////
    List<CardHolder> getCardsbyCustomerIds(List<Long> customerIds);

    ////////////////////////////////////Generation de num de card ///////////////////////////////////////////////////

}
