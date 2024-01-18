package tn.monetique.cardmanagment.security.services;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tn.monetique.cardmanagment.exception.TokenRefreshException;
import tn.monetique.cardmanagment.Entities.Auth_User.AgentBank;
import tn.monetique.cardmanagment.Entities.Auth_User.RefreshToken;
import tn.monetique.cardmanagment.repository.userManagmentRepo.AgentBankRepository;
import tn.monetique.cardmanagment.repository.userManagmentRepo.MonetiqueAdminRepo;
import tn.monetique.cardmanagment.repository.userManagmentRepo.RefreshTokenRepository;
import tn.monetique.cardmanagment.repository.userManagmentRepo.AdminBankRepository;

@Service
public class RefreshTokenService {
  @Value("${bezkoder.app.jwtRefreshExpirationMs}")
  private Long refreshTokenDurationMs;
  @Autowired
  private RefreshTokenRepository refreshTokenRepository;
  @Autowired
  private AdminBankRepository adminBankRepository;
  @Autowired
  private AgentBankRepository agentBankRepository;
  @Autowired
  MonetiqueAdminRepo monetiqueAdminRepo;


  public Optional<RefreshToken> findByToken(String token) {
    return Optional.ofNullable(refreshTokenRepository.findByToken(token));
  }

  public RefreshToken createRefreshTokenforadminbank(Long userId) {
    RefreshToken refreshToken = new RefreshToken();

    refreshToken.setBankAdmin(adminBankRepository.findById(userId).get());
    refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
    refreshToken.setToken(UUID.randomUUID().toString());

    refreshToken = refreshTokenRepository.save(refreshToken);
    return refreshToken;
  }
  public RefreshToken createRefreshTokenforMonetiqueadmin(Long userId) {
    RefreshToken refreshToken = new RefreshToken();

    refreshToken.setMonetiqueAdmin(monetiqueAdminRepo.findById(userId).get());
    refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
    refreshToken.setToken(UUID.randomUUID().toString());

    refreshToken = refreshTokenRepository.save(refreshToken);
    return refreshToken;
  }

  public RefreshToken createRefreshTokenForagent(long customerId) {
    Optional<AgentBank> optionalCustomer = agentBankRepository.findById(customerId);
    if (optionalCustomer.isEmpty()) {
      throw new UsernameNotFoundException("agent not found with id: " + customerId);
    }

    AgentBank agentBank = optionalCustomer.get();
    RefreshToken refreshToken = new RefreshToken();
    refreshToken.setAgentBank(agentBank);
    refreshToken.setToken(UUID.randomUUID().toString());
    refreshToken.setExpiryDate(Instant.now().plusMillis(86400000)); // Set expiry date to 24 hours from now

    return refreshTokenRepository.save(refreshToken);
  }
  public RefreshToken generatenewRefreshToken(Long userId, RefreshToken oldtoken) {
    RefreshToken newrefreshToken = new RefreshToken();
    newrefreshToken.setBankAdmin(adminBankRepository.findById(userId).get());
    newrefreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
    newrefreshToken.setToken(UUID.randomUUID().toString());
    refreshTokenRepository.delete(oldtoken);
    newrefreshToken = refreshTokenRepository.save(newrefreshToken);
    return newrefreshToken;
  }
  public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
                             AdminBankRepository adminBankRepository,
                             AgentBankRepository agentBankRepository,MonetiqueAdminRepo monetiqueAdminRepo) {
    this.refreshTokenRepository = refreshTokenRepository;
    this.adminBankRepository = adminBankRepository;
    this.monetiqueAdminRepo = monetiqueAdminRepo;
    this.agentBankRepository = agentBankRepository;
  }
  public RefreshToken verifyExpiration(RefreshToken token) {
    Instant now = Instant.now();
    System.out.println("Current time: " + now);
    System.out.println("Expiredate: " + token.getExpiryDate());
    if (token.getExpiryDate().isBefore(now)) {
      System.out.println("Token expired: " + token.getToken());
      refreshTokenRepository.delete(token);
      throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
    }

    return token;
  }
  public boolean isRefreshTokenAboutToExpire(RefreshToken refreshToken) {
    Instant now = Instant.now();
    Instant expireDate = refreshToken.getExpiryDate();
    Duration timeRemaining = Duration.between(now, expireDate);

    return timeRemaining.toMinutes() <= 1;
  }


  @Transactional
  public int deleteByUserId(Long bankAdminId) {
    return refreshTokenRepository.deleteByBankAdmin(adminBankRepository.findById(bankAdminId).get());
  }

  @Transactional
  public int deleteByCustomerId(Long AgentbankID) {
    return refreshTokenRepository.deleteByAgentBank(agentBankRepository.findById(AgentbankID).get());
  }
  @Transactional
  public int deleteByMonetiqueAdmin(Long monetiqueId) {
    return refreshTokenRepository.deleteByMonetiqueAdmin(monetiqueAdminRepo.findById(monetiqueId).get());
  }
}
