package project.google.cloudvault.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import project.google.cloudvault.model.ConnectedAccount;

public interface ConnectedAccountRepository extends JpaRepository<ConnectedAccount, Long> {

    boolean existsByGoogleSubjectId(String googleSubjectId);

    List<ConnectedAccount> findByMasterUserID_GoogleSubjectId(String googleSubjectId); // for finding all connected account against a masterUSer
}
