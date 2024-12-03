package edu.jl.authenticationandauthorizationspring.service.implementation;

import edu.jl.authenticationandauthorizationspring.model.PermissionModel;
import edu.jl.authenticationandauthorizationspring.repository.PermissionRepository;
import edu.jl.authenticationandauthorizationspring.service.PermissionService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PermissionServiceImplementation implements PermissionService {
    private final PermissionRepository permissionRepository;

    public PermissionServiceImplementation(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public Map<String, PermissionModel> getPermissionsMap() {
        return permissionRepository.findAll().stream()
                .collect(Collectors.toMap(PermissionModel::getAuthority, permission -> permission));
    }
}
