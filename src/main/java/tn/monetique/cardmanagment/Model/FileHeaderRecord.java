package tn.monetique.cardmanagment.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter

public class FileHeaderRecord {


    // Record attributes and their lengths
    @Column(length = 9)
    private String recordCount;

    @Column(length = 2)
    private String recordType;

    @Column(length = 1)
    private String refreshType;

    @Column(length = 2)
    private String applicationType;

    @Column(length = 4)
    private String grpcode;

    @Column(length = 8)
    private String tapeDate;

    @Column(length = 4)
    private String tapeTime;

    @Column(length = 4)
    private String ln;

    @Column(length = 2)
    private String releaseNumber;

    @Column(length = 2)
    private String partitionNumber;

    @Column(length = 6)
    private String atmLastExtractDate;

    @Column(length = 8)
    private String atmImpactingStartDate;

    @Column(length = 12)
    private String atmImpactingStartTime;

    @Column(length = 6)
    private String posLastExtractDate;

    @Column(length = 8)
    private String posImpactingStartDate;

    @Column(length = 12)
    private String posImpactingStartTime;

    @Column(length = 6)
    private String tlrLastExtractDate;

    @Column(length = 8)
    private String tlrImpactingStartDate;

    @Column(length = 12)
    private String tlrImpactingStartTime;

    @Column(length = 1)
    private String impactType;

    @Column(length = 1)
    private String cafExponent;

    @Column(length = 1)
    private String preAuthSupport;

    @Column(length = 5)
    private String userField2;

    @Column(length = 6)
    private String tbLastExtractDate;

    @Column(length = 8)
    private String tbImpactingStartDate;

    @Column(length = 12)
    private String tbImpactingStartTime;

    public FileHeaderRecord() {

    }
}


