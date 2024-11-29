package edu.jl.authenticationandauthorizationspring.repository;

import edu.jl.authenticationandauthorizationspring.model.PermissionModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<PermissionModel, Long> {
}
