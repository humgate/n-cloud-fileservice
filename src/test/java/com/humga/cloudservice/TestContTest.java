package com.humga.cloudservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.humga.cloudservice.model.LoginFormDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;


import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;


@SpringBootTest(webEnvironment = RANDOM_PORT)
@Testcontainers
@Slf4j
public class TestContTest {
    @Autowired
    TestRestTemplate restTemplate;
    static final PostgreSQLContainer<?> postgreSQLContainer;
    static final GenericContainer<?> backendContainer;

    static {
        Network network = Network.newNetwork();

        postgreSQLContainer = new PostgreSQLContainer<>(DockerImageName.parse("horana")
                .asCompatibleSubstituteFor("postgres"))
                .withDatabaseName("test")
                .withUsername("postgres")
                .withPassword("postgres")
                .withNetwork(network)
                .withNetworkAliases("postgresdb")
                .withReuse(true);

        postgreSQLContainer.start();

        backendContainer = new GenericContainer<>("backend-service")
                .withExposedPorts(5500)
                .withNetwork(network)
                .withEnv("SPRING_DATASOURCE_URL", "jdbc:postgresql://postgresdb/test")
                .withLogConsumer(new Slf4jLogConsumer(log).withPrefix("-----backend-service----"))
                .dependsOn(postgreSQLContainer);

        backendContainer.start();
    }

    @DynamicPropertySource
    static void datasourceConfig(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    }

    @BeforeAll
    static void init() {
        //backendContainer.start();
    }

    @Test
    void test() throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Content-Type", "application/json");
        headers.add("Accept", "application/json,*/*");
        final ObjectMapper objectMapper = new ObjectMapper();
        HttpEntity<String> request = new HttpEntity<>(
                objectMapper.writeValueAsString(new LoginFormDTO("alex@email.com", "passAlex")), headers);


        ResponseEntity<String> forEntity = restTemplate.postForEntity(
                "http://" + backendContainer.getHost() + ":" + backendContainer.getMappedPort(5500) + "/cloud/login",
                request, String.class);
    }
}
