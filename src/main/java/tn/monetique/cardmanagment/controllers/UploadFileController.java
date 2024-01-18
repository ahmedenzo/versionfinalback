package tn.monetique.cardmanagment.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.monetique.cardmanagment.FTP.IFTPUploader;
import tn.monetique.cardmanagment.advice.UploadResponse;
import tn.monetique.cardmanagment.Entities.DataInputCard.UploadedFile;
import tn.monetique.cardmanagment.Entities.Auth_User.BankAdmin;
import tn.monetique.cardmanagment.repository.userManagmentRepo.AdminBankRepository;
import tn.monetique.cardmanagment.service.Interface.GestionUserInterface.UserService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth/card")
public class UploadFileController {

    @Autowired
    IFTPUploader iftpUploader;
    @Autowired
    UserService userService;
    @Autowired
    private AdminBankRepository adminBankRepository;

    @PostMapping("/upload")
    public ResponseEntity<UploadResponse> uploadFiles(@RequestParam List<Long> fileInformationIds, Authentication authentication) {
        try {

            boolean uploaded = iftpUploader.uploadFiles(fileInformationIds,authentication);

            if (uploaded) {
                return ResponseEntity.ok(new UploadResponse("Files uploaded successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new UploadResponse("Failed to upload files."));
            }

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new UploadResponse("Error uploading files: " + e.getMessage()));
        }
    }

    @GetMapping("/Uplodedfile")
    public ResponseEntity<?> getAllUploadeddataCardHolders(@RequestHeader String RefrechTokenV) {
        String usernam = userService.getCurrentUser().getUsername();
        Long userid = userService.getCurrentUser().getId();
        Optional<BankAdmin> optionalUser = adminBankRepository.findByUsername(usernam);
        if (optionalUser.isPresent()) {
            if (userService.isUserIdMatchingToken(userid, RefrechTokenV)) {
                List<UploadedFile> uploadedFiles = iftpUploader.getallFiles();
                if (!uploadedFiles.isEmpty()) {

                    return new ResponseEntity<>(uploadedFiles, HttpStatus.OK);
                } else {
                    return ResponseEntity.notFound().build();
                }
            } else {
                // Token is invalid or expired, return an error response
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Open another session. Refresh token invalid.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
        }
    }

}
