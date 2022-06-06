package com.humga.cloudservice.service;

import com.humga.cloudservice.model.entity.Authority;
import com.humga.cloudservice.model.entity.User;
import com.humga.cloudservice.repository.AuthorityCrudRepository;
import com.humga.cloudservice.repository.UserCrudRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    private final UserCrudRepository userRepo;
    private final AuthorityCrudRepository authRepo;

    public UserService(UserCrudRepository userRepo, AuthorityCrudRepository authRepo) {
        this.userRepo = userRepo;
        this.authRepo = authRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User user = findUser(login);

        List<Authority> authorities = authRepo.findAuthorityByUser(user);

        List<GrantedAuthority> grantedAuthorities = authorities.stream()
                .map(auth -> new SimpleGrantedAuthority(auth.getAuthority()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                user.getLogin(), user.getPassword(), grantedAuthorities);
    }

    private User findUser (String login) {
        return userRepo.getUserByLogin(login).orElseThrow(
                () -> new UsernameNotFoundException("Invalid username or password."));
    }
}
