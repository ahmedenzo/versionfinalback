package tn.monetique.cardmanagment.Model;

import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
public class OrganisationHeaderRecord {


    // Record attributes and their lengths

    private String recordCounter;


    private String recordType;


    private String cardIssuer;


    private String endRange;

    private String userField;

    public OrganisationHeaderRecord() {

    }
}
