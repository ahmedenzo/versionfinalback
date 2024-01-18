package tn.monetique.cardmanagment.security.services;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import tn.monetique.cardmanagment.Entities.Auth_User.BankAdmin;
import tn.monetique.cardmanagment.Entities.Auth_User.AgentBank;
import com.fasterxml.jackson.annotation.JsonIgnore;
import tn.monetique.cardmanagment.Entities.Auth_User.MonetiqueAdmin;

public class UserDetailsImpl implements UserDetails {
	private static final long serialVersionUID = 1L;
	private Boolean isTwoFactorEnabled;
	private Long id;

	private String username;

	private String email;
	private String twoFactorCode;

	@JsonIgnore
	private String password;

	private Collection<? extends GrantedAuthority> authorities;

	public UserDetailsImpl(Long id, String username, String email, String password, String twoFactorCode,
						   Boolean isTwoFactorEnabled, Collection<? extends GrantedAuthority> authorities) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.password = password;
		this.twoFactorCode = twoFactorCode;
		this.isTwoFactorEnabled = isTwoFactorEnabled;  // Setting the 2FA status
		this.authorities = authorities;
	}

	public static UserDetailsImpl build(Object obj) {
		if (obj instanceof BankAdmin) {
			BankAdmin bankAdmin = (BankAdmin) obj;
			List<GrantedAuthority> authorities = bankAdmin.getRoles().stream()
					.map(role -> new SimpleGrantedAuthority(role.getName().name()))
					.collect(Collectors.toList());

			return new UserDetailsImpl(
					bankAdmin.getId(),
					bankAdmin.getUsername(),
					bankAdmin.getEmail(),
					bankAdmin.getPassword(),
					bankAdmin.getTwoFactorCode(),
					bankAdmin.getIsTwoFactorEnabled(),
					authorities);
		} else if (obj instanceof AgentBank) {
			AgentBank agentBank = (AgentBank) obj;
			List<GrantedAuthority> authorities = agentBank.getRoles().stream()
					.map(role -> new SimpleGrantedAuthority(role.getName().name()))
					.collect(Collectors.toList());

			return new UserDetailsImpl(
					agentBank.getId(),
					agentBank.getUsername(),
					agentBank.getEmail(),
					agentBank.getPassword(),
					agentBank.getTwoFactorCode(),
					agentBank.getIsTwoFactorEnabled(),
					authorities);
		}else if (obj instanceof MonetiqueAdmin) {
			MonetiqueAdmin monetiqueAdmin = (MonetiqueAdmin) obj;
			List<GrantedAuthority> authorities = monetiqueAdmin.getRoles().stream()
					.map(role -> new SimpleGrantedAuthority(role.getName().name()))
					.collect(Collectors.toList());

			return new UserDetailsImpl(
					monetiqueAdmin.getId(),
					monetiqueAdmin.getUsername(),
					monetiqueAdmin.getEmail(),
					monetiqueAdmin.getPassword(),
					monetiqueAdmin.getTwoFactorCode(),
					monetiqueAdmin.getIsTwoFactorEnabled(),
					authorities);
		}	else {
			throw new IllegalArgumentException("Unsupported user type");
		}
	}
	public String getTwoFactorCode() {
		return twoFactorCode;
	}

	public Boolean getIsTwoFactorEnabled() {
		return isTwoFactorEnabled;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public Long getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	@Override
	public String getPassword() {
		return password;
	}





	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		UserDetailsImpl user = (UserDetailsImpl) o;
		return Objects.equals(id, user.id);
	}


}