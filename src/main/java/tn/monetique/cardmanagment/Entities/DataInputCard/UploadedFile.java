package tn.monetique.cardmanagment.Entities.DataInputCard;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@AllArgsConstructor
@Getter
@Setter
@Entity
public class UploadedFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String UploadedBy;
    private Timestamp UploadedAt;

    private boolean upoaded=false;
    @PrePersist
    protected void onCreate() { UploadedAt = new Timestamp(System.currentTimeMillis());
    }

    public UploadedFile() {

    }


}

