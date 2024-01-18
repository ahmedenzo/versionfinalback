package tn.monetique.cardmanagment.service.Interface.BankConfig;

import tn.monetique.cardmanagment.Entities.ConfigBank.Bin;

import java.util.List;

public interface IbinService {



    Bin creatBIn(Bin bin, String bankname);

    Bin Updatebin(Bin bin, Long binId);

    List getbinbybank(String bankname);
    boolean deletebin(Long binId);

    List<Bin> getallbins();

    boolean binsofbank(Long bankId);
}
