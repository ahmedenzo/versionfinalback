package tn.monetique.cardmanagment.Model;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter

    public class FileTrailerRecord {


    private String recordCount;

    private String recordType;

    private String numberOfRecords;

    private String nextFileIndicator;

    private String userField1;

    public FileTrailerRecord() {

    }
}
