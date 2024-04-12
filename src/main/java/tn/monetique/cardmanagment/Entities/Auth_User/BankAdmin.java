package tn.monetique.cardmanagment.Entities.Auth_User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.monetique.cardmanagment.Entities.ConfigBank.Bank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(	name = "BankAdmin",
		uniqueConstraints = {
			@UniqueConstraint(columnNames = "username"),
			@UniqueConstraint(columnNames = "email")
		})
public class BankAdmin {
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private Long id;
	@NotBlank
	@Size(max = 20)
	private String username;
	private String passwordResetToken;
	private String confirmationToken;
	private String image;
	private String fullname;
	private Long phone;
	private String adresse;
	private Boolean active = false;
	@NotBlank
	@Size(max = 50)
	@Email
	private String email;
	@NotBlank
	@Size(max = 120)
	private String password;
	private Boolean confirme = false;
	@ManyToOne
	@JoinColumn(name = "bank_id")
	private Bank bank;


	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "users_roles",
			joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "role_id"),
			foreignKey = @ForeignKey(name = "none"),
			inverseForeignKey = @ForeignKey(name = "none"))
	private Set<Role> roles = new HashSet<>();
	@JsonManagedReference
	@OneToMany(mappedBy = "bankAdmin", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<AgentBank> agentBanks = new ArrayList<>();
	// 2FA fields
	private String twoFactorCode; // Nullable; check if null to determine if it's set
	private Boolean isTwoFactorEnabled = false;  // A flag to check if 2FA is active
	private String qrCodeUrl;

	public BankAdmin(String username, String email, String password) {
		this.username = username;
		this.email = email;
		this.password = password;
	}

	// Setter for active field with logic to disable associated AgentBanks
	public void setActive(Boolean active) {
		this.active = active;
		if (!active) {
			// If the admin is being disabled, also disable associated AgentBanks
			for (AgentBank agentBank : agentBanks) {
				agentBank.setActive(false);
			}
		}
	}
}