package edu.jl.authenticationandauthorizationspring.repository;

import edu.jl.authenticationandauthorizationspring.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserModel, Long> {
}
