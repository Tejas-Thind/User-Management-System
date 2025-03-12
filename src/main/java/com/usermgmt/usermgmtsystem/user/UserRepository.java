package com.usermgmt.usermgmtsystem.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repository annotation to mark the interface as interacting with a database
// Handles data access
@Repository
// Spring Data JPA interface that already provides common database methods
public interface UserRepository extends JpaRepository<User, Long /* Type of the primary key */> {

}
