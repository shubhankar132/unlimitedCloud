package project.google.cloudvault.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import project.google.cloudvault.model.MasterUser;

public interface MasterUserRepository extends JpaRepository<MasterUser,String>{

}
