package com.example.Dynamo_Backend.service;

import com.example.Dynamo_Backend.entities.Role;
import java.util.List;

public interface RoleService {
    Role addRole(Role role);

    Role updateRole(Long id, Role role);

    Role getRoleById(Long id);

    void deleteRole(Long id);

    List<Role> getRoles();

    Role getRoleByName(String name);
}