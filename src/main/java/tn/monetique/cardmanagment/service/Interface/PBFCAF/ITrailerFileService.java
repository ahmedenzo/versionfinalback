package tn.monetique.cardmanagment.service.Interface.PBFCAF;



import java.util.List;

public interface ITrailerFileService {



    String createAndGenerateTrailerRecord(List<Long> customerIds, String fileType);

    String createAndGenerateorgTrailerRecord(List<Long> customerIds, String fileType);
}
