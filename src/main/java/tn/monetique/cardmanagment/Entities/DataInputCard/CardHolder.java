package tn.monetique.cardmanagment.Entities.DataInputCard;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import tn.monetique.cardmanagment.Entities.ConfigBank.Bank;


import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDate;


    @AllArgsConstructor
    @Getter
    @Setter
    @Entity
    public class  CardHolder implements Serializable {


        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long customerId;
        private String HeaderRecord;
        private String primarycardcode;
        private String bin ;
        @Column(unique = true)
        private String cardholderNumber;
        private String updatecode;
        private String cardtype;
        private String currencycode;
        private String corporateName;
        private String name;
        private String address;
        private String addresstwo;
        private String addressthree;
        private String postalCode;
        private String firstAccount;
        private String secondAccount;
        private String branchcode;
        private String date1;
        private String date2;
        private String cardProcessIndicator;
        private String pinoffset;
        private String operatorUserCode;
        private String territorycode;
        private String julianDate;
        private String bankIdCode;
        private LocalDate BirthDate;
        private String passportId;
        private String freesCode;
        private String countryCode;
        private String cityCode;
        private String renewOption="0";
        private String Sourcecode;
        private String cin;
        private String phoneNumber;
        private String email;
        private String pkiindicator="0";
        private String acs;
        private boolean Confirmation=false;

        private String statuscard ;
        private Timestamp createdAt;
        private Timestamp updatedAt;
        private String createdBy;
        private String updatedBy;
        private String countryPhonecode;

        private boolean cardgenerated = false;
        private boolean skipUpdateTimestamp = false;


        public void skipUpdateTimestamp() {
            this.skipUpdateTimestamp = true;
        }

        @PreUpdate
        protected void onUpdate() {
            if (!skipUpdateTimestamp) {
                updatedAt = new Timestamp(System.currentTimeMillis());
            }
        }
        public CardHolder() {
        }

        @PrePersist
        protected void onCreate() {
            createdAt = new Timestamp(System.currentTimeMillis());
        }

        @ManyToOne
        @JoinColumn(name = "bank_id")
        private Bank bank;

    }

