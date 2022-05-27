package com.humga.cloudservice.init;


import com.humga.cloudservice.model.entity.Authority;
import com.humga.cloudservice.model.entity.User;
import com.humga.cloudservice.repository.AuthorityCrudRepository;
import com.humga.cloudservice.repository.UserCrudRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


/**
 * Инициализирует тестовых пользователей и authority пользователей в базе если таблица пользователей пуста
 *
 */
@Component
public class DbCommandLineRunnerInitializr implements CommandLineRunner {
    private final UserCrudRepository userRepo;
    private final AuthorityCrudRepository authRepo;
    private final PasswordEncoder encoder;

    public DbCommandLineRunnerInitializr(
            UserCrudRepository userRepo, AuthorityCrudRepository authRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.authRepo = authRepo;
        this.encoder = encoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepo.findAll().isEmpty()) {
            User user= new User("alex@email.com", encoder.encode("passAlex"));
            userRepo.save(user);
            authRepo.save(new Authority(user, "ROLE_READ"));
            authRepo.save(new Authority(user, "ROLE_WRITE"));

            user= new User("fedor@email.com", encoder.encode("passFedor"));
            userRepo.save(user);
            authRepo.save(new Authority(user, "ROLE_READ"));

            user= new User("ivan@email.com", encoder.encode("пассИван"));
            userRepo.save(user);
            authRepo.save(new Authority(user, "ROLE_WRITE"));
            authRepo.save(new Authority(user, "ROLE_READ"));
        }
    }
}
