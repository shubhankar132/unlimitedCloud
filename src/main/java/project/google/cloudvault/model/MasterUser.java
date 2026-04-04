package project.google.cloudvault.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@Entity
public class MasterUser {
    @Id
    private String googleSubjectId;
    
}
