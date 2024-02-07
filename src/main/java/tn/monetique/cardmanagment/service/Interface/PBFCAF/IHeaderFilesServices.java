package tn.monetique.cardmanagment.service.Interface.PBFCAF;


import org.springframework.security.core.Authentication;

import java.util.List;

public interface IHeaderFilesServices {


    String createAndGenerateheaderRecord(String fileType, Authentication authentication);



    String createAndGenerateorganisationRecord();
}
