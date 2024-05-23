package tn.monetique.cardmanagment.Entities.ApplicationDataRecord;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class PBFBalanceHistory implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long availBal;
    private Long ledgBal;
    private Timestamp timestamp;

    @ManyToOne
    @JoinColumn(name = "pbf_application_data_record_id")
    private PBFApplicationDataRecord pbfApplicationDataRecord;

    public PBFBalanceHistory() {
    }

    public PBFBalanceHistory(Long availBal, Long ledgBal, PBFApplicationDataRecord pbfApplicationDataRecord) {
        this.availBal = availBal;
        this.ledgBal = ledgBal;
        this.pbfApplicationDataRecord = pbfApplicationDataRecord;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }
}