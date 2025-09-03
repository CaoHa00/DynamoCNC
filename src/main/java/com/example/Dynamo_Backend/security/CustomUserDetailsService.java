package com.example.Dynamo_Backend.security;

import com.example.Dynamo_Backend.entities.Admin;
import com.example.Dynamo_Backend.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        // Try to find by username first, then email
        Admin admin = adminRepository.findByUsername(identifier)
                .or(() -> adminRepository.findByEmail(identifier))
                .orElseThrow(
                        () -> new UsernameNotFoundException("Admin not found with username or email: " + identifier));
        return new CustomUserDetails(admin);
    }
}