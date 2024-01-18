package tn.monetique.cardmanagment.Model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrganisationTrailerRecord {


    private String recordCounter;

    private String recordType;

    private Long amount;

    private String numberOfRecords;

    public OrganisationTrailerRecord() {

    }
}
