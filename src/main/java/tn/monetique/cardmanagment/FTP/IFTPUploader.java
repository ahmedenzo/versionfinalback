package tn.monetique.cardmanagment.FTP;

import org.springframework.security.core.Authentication;
import tn.monetique.cardmanagment.Entities.DataInputCard.UploadedFile;

import java.io.IOException;
import java.util.List;

public interface IFTPUploader {
   boolean connect() throws IOException;

   boolean uploadFiles(List<Long> fileInformationIds, Authentication authentication) throws IOException;

   void disconnect() throws IOException;

   List<UploadedFile> getallFiles();
}
