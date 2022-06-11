package com.humga.cloudservice;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.humga.cloudservice.model.LoginFormDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@Slf4j
public class TestContainersTests {
    private static final TestRestTemplate restTemplate = new TestRestTemplate();
    private static String token;
    private static final String appUrl;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final HttpHeaders headers = new HttpHeaders();
    private static final PostgreSQLContainer<?> postgreSQLContainer;
    private static final GenericContainer<?> backendContainer;

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
                .withLogConsumer(new Slf4jLogConsumer(log).withPrefix("----backend-service----"))
                .dependsOn(postgreSQLContainer);

        backendContainer.start();

        appUrl =  "http://" + backendContainer.getHost() + ":" + backendContainer.getMappedPort(5500);
    }

    @BeforeAll
    static void init() {

    }

    @Test
    @Order(1)
    void loginTest() throws JsonProcessingException {
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Content-Type", "application/json");
        headers.add("Accept", "application/json,*/*");
        HttpEntity<String> request = new HttpEntity<>(
                objectMapper.writeValueAsString(new LoginFormDTO("alex@email.com", "passAlex")), headers);

        String response = restTemplate.postForObject(appUrl + "/cloud/login", request, String.class);

        assertNotNull(response);
        assertTrue(response.matches("\\{\"auth-token\":\"[\\w_.-]{269}\"}"));

        token = "Bearer " + response.substring(15, response.length() - 2);
    }

    @Test
    @Order(2)
    void logoutTest() {
        HttpEntity<String> request = new HttpEntity<>(headers);
        headers.add("auth-token", token);
        restTemplate.postForObject(appUrl + "/cloud/logout", request, String.class);
    }
}
