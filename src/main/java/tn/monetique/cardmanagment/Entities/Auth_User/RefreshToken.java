package tn.monetique.cardmanagment.Entities.Auth_User;

import java.time.Instant;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "refreshtoken")
public class RefreshToken {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "bank_admin_id")
  private BankAdmin bankAdmin;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "monetique_admin_id")
  private MonetiqueAdmin monetiqueAdmin;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "agent_bank_id")
  private AgentBank agentBank;

  @Column(nullable = false, unique = true)
  private String token;

  @Column(nullable = false)
  private Instant expiryDate;



}