package com.example.Dynamo_Backend.service.implementation;

import com.example.Dynamo_Backend.entities.Role;
import com.example.Dynamo_Backend.repository.RoleRepository;
import com.example.Dynamo_Backend.service.RoleService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class RoleImplementation implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public Role addRole(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public Role updateRole(Long id, Role role) {
        Role existing = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found: " + id));
        existing.setName(role.getName());
        return roleRepository.save(existing);
    }

    @Override
    public Role getRoleById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found: " + id));
    }

    @Override
    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found: " + id));
        roleRepository.delete(role);
    }

    @Override
    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Role getRoleByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Role not found: " + name));
    }
}