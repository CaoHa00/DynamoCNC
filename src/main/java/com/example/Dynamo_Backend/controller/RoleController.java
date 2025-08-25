package com.example.Dynamo_Backend.controller;

import com.example.Dynamo_Backend.entities.Role;
import com.example.Dynamo_Backend.service.RoleService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/roles")
public class RoleController {
    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(roleService.getRoles());
    }

    @PostMapping
    public ResponseEntity<Role> addRole(@RequestBody Role role) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roleService.addRole(role));
    }

    @PutMapping("/{role_id}")
    public ResponseEntity<Role> updateRole(@PathVariable("role_id") Long id, @RequestBody Role role) {
        return ResponseEntity.ok(roleService.updateRole(id, role));
    }

    @DeleteMapping("/{role_id}")
    public ResponseEntity<Void> deleteRole(@PathVariable("role_id") Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{role_id}")
    public ResponseEntity<Role> getRoleById(@PathVariable("role_id") Long id) {
        return ResponseEntity.ok(roleService.getRoleById(id));
    }
}