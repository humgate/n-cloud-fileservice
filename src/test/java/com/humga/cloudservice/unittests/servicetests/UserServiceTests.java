package com.humga.cloudservice.unittests.servicetests;

import com.humga.cloudservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTests {
    @Autowired
    UserService userService;

    @Autowired
    BCryptPasswordEncoder encoder;

    @Test
    void loadUserByNameTest() {
        //given user db table filled in while app initialization

        //when
        UserDetails userDetails = userService.loadUserByUsername("alex@email.com");

        //then
        assertEquals("alex@email.com", userDetails.getUsername());
        assertTrue(encoder.matches("passAlex", userDetails.getPassword()));
        List<String> authorities =
                userDetails.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList());

        assertFalse(authorities.isEmpty());
        assertEquals(2, authorities.size());
        assertTrue(authorities.contains("ROLE_READ"));
        assertTrue(authorities.contains("ROLE_WRITE"));
    }

    @Test
    void loadUserByNameNotExistingUserTest() {
        //given user db table filled in while app initialization

        //when
        UsernameNotFoundException e = assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername("notexist@email.com"));

        //then
        assertEquals(e.getMessage(), "Invalid username or password.");
    }
}
