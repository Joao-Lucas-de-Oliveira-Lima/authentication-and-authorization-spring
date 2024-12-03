package edu.jl.authenticationandauthorizationspring.service;

import edu.jl.authenticationandauthorizationspring.model.PermissionModel;

import java.util.Map;

public interface PermissionService {
    Map<String, PermissionModel> getPermissionsMap();
}
