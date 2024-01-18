package tn.monetique.cardmanagment.Entities.Auth_User;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import tn.monetique.cardmanagment.Entities.ConfigBank.Agence;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name ="agentbank")
@Data
@AllArgsConstructor

public class AgentBank implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;
    @NotBlank
    @Size(max = 20)
    private String username;
    @NotBlank
    @Size(max = 50)
    @Email
    private String email;
    @NotBlank
    @Size(max = 120)
    private String password;
    private Boolean confirme = false;
    private String passwordResetToken;
    private String image;
    private String confirmationToken;
    private String fullname;
    private Long  phone;
    private String adresse;
    private String twoFactorCode; // Nullable; check if null to determine if it's set
    private Boolean isTwoFactorEnabled = false;  // A flag to check if 2FA is active
    private String qrCodeUrl;


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"),
            foreignKey = @ForeignKey(name = "none"),
            inverseForeignKey = @ForeignKey(name = "none"))
    private Set<Role> roles = new HashSet<>();
    @JsonIgnore
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    private BankAdmin bankAdmin;


    public AgentBank(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
    @ManyToOne
    private Agence agence;
    public AgentBank() {
    }
}
