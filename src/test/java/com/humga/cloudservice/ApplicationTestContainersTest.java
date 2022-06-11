package com.humga.cloudservice;

import com.humga.cloudservice.model.LoginFormDTO;
import org.junit.ClassRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.*;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.utility.DockerImageName;


import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@ActiveProfiles("integration-test")
public class ApplicationTestContainersTest {
//    @Autowired
//    TestRestTemplate restTemplate;
//    private static final Network network = Network.newNetwork();
//    //db container
//    public static GenericContainer<?> dbContainer = new GenericContainer<>(DockerImageName.parse("horana:latest"))
//              .withExposedPorts(5432)
//              .withNetworkAliases("postgresdb")
//              .withEnv("POSTGRES_PASSWORD", "postgres")
//              .withEnv("PGDATA", "/var/lib/postgresql/data")
//              .withFileSystemBind("/var/lib/docker/volumes/pg_data_tb/_data",
//                    "/var/lib/postgresql/data", BindMode.READ_WRITE);
//
//    //our backend service container
//    public static GenericContainer<?> backendContainer = new GenericContainer<>("backend-service")
//            .withExposedPorts(5500)
//            .withEnv("SPRING_DATASOURCE_URL","jdbc:postgresql://postgresdb/cloudstoragetest")
//            .dependsOn(dbContainer);
//
//    @BeforeAll
//    public static void setUp() {
//        //start containers
//        dbContainer.start();
//        backendContainer.start();
//    }
//
//    @Test
//    void loginTest() throws JsonProcessingException {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.add("Content-Type", "application/json");
//        headers.add("Accept", "application/json,*/*");
//        final ObjectMapper objectMapper = new ObjectMapper();
//        HttpEntity<String> request = new HttpEntity<>(
//                objectMapper.writeValueAsString(new LoginFormDTO("alex@email.com", "passAlex")), headers);
//
//
//        ResponseEntity<String> forEntity = restTemplate.postForEntity(
//                "http://" + backendContainer.getHost() + ":" + backendContainer.getMappedPort(5500) + "/cloud/login",
//                request, String.class);
////        assertTrue(forEntity.getBody().matches("\\{\"auth-token\":\"[.]*\"}"));
//    }

//    @Test
//    void logoutTest() {
//        final String token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbGV4QGVtYWlsLmNvbSIs" +
//                "InNjb3BlcyI6W3siYXV0aG9yaXR5IjoiUk9MRV9SRUFEIn0seyJhdXRob3JpdHkiOiJST0xFX1dSSVR" +
//                "FIn1dLCJpc3MiOiJodHRwOi8vaHVtZ2Fpc3N1ZXIuY29tIiwiaWF0IjoxNjU0NjIyMTAyLCJleHAiOj" +
//                "kyNDY0NTc4MDAwfQ.fl3FwFZsx6IuuFbaQZuEOxC9oZPv6P3W0Jk4MUSxXt8";
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.add("auth-token", token);
//        HttpEntity<String> request = new HttpEntity<>(headers);
//        ResponseEntity<String> forEntity = restTemplate.postForEntity(
//                "http://" + container.getHost() + ":" + container.getMappedPort(5500) + "/cloud/logout"
//                , request
//                , String.class);
//    }
}
