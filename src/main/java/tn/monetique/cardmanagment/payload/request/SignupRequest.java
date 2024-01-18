package tn.monetique.cardmanagment.payload.request;

import java.util.Set;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SignupRequest {
    @NotBlank
    @Size(min = 3, max = 20)
    private String username;
 
    @NotBlank
    @Size(max = 50)
    @Email
    private String email;
    
    private Set<String> role;
    private Set<String> Eroleadmin;
    
    @NotBlank
    @Size(min = 6, max = 40)
    private String password;
    private String affectedBankRef;
   // private String adress;
   // private String city;

   // private String matricule;
   // private String service;
   // private String company;

   // private Boolean confirme =false;


}
