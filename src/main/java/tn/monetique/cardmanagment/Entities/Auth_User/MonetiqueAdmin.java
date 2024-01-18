package tn.monetique.cardmanagment.Entities.Auth_User;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;

import java.util.Set;
@Entity
@Getter
@Setter
@AllArgsConstructor
public class MonetiqueAdmin {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;
    @NotBlank
    @Size(max = 20)
    private String username;
    private String passwordResetToken;
    private String image;
    private String cantactphone;
    @NotBlank
    @Size(max = 50)
    @Email
    private String email;
    @NotBlank
    @Size(max = 120)
    private String password;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"),
            foreignKey = @ForeignKey(name = "none"),
            inverseForeignKey = @ForeignKey(name = "none"))
    private Set<Role> roles = new HashSet<>();
    private String twoFactorCode; // Nullable; check if null to determine if it's set
    private Boolean isTwoFactorEnabled = false;  // A flag to check if 2FA is active
    private String qrCodeUrl;


    public MonetiqueAdmin(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public MonetiqueAdmin() {
    }
}
