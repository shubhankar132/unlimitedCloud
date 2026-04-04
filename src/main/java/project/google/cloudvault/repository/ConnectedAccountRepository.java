package project.google.cloudvault.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import project.google.cloudvault.model.ConnectedAccount;

public interface ConnectedAccountRepository extends JpaRepository<ConnectedAccount,Long>{

    
} 
