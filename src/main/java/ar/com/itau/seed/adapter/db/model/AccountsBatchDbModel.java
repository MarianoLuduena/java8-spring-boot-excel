package ar.com.itau.seed.adapter.db.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.time.LocalDateTime;

@Entity(name = "ACCOUNTS_BATCH")
@Data
public class AccountsBatchDbModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", updatable = false)
    private Long id;

    @Column(name = "SOURCE_NAME", nullable = false, updatable = false)
    private String sourceName;

    @Column(name = "CONTENT", nullable = false)
    @Lob
    private byte[] content;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt;

}
