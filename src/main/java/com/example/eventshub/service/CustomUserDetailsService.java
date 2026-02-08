package com.example.eventshub.service;

import com.example.eventshub.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository repo;

    public CustomUserDetailsService(UserRepository repo) { this.repo = repo; }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repo.findByEmail(username)
                .map(u -> User.builder()
                        .username(u.getEmail())
                        .password(u.getPassword())
                        .authorities(List.of(new SimpleGrantedAuthority(u.getRole().name())))
                        .accountLocked(false)
                        .disabled(false)
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
