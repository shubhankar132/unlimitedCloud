package project.google.cloudvault.model;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class ConnectedAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // auto-generated PK 
    private String googleSubjectId;
    @ManyToOne
    private MasterUser masterUserID;
    private String accessToken;
    private String refreshToken;
    private Instant tokenExpiry;

}
