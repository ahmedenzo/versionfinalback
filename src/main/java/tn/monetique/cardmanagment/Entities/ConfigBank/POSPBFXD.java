package tn.monetique.cardmanagment.Entities.ConfigBank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class POSPBFXD implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long posPbfXdId;
        private String segxLgth="0042";
        private String ttlFloat="000000000000000";
        private String daysDelinq="00";
        private String monthsActive="00";
        private String cycle1="00";
        private String cycle2="00";
        private String cycle3="00";
        private String unknown="000000000000";
        private String userFld2;


    public POSPBFXD() {

    }
    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "binid_id")
    private Bin bin;
}
