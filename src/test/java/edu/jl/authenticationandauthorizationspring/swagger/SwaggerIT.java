package edu.jl.authenticationandauthorizationspring.swagger;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for Swagger
 */
@Testcontainers
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class SwaggerIT {

    @LocalServerPort
    private int port;

    @Container
    @ServiceConnection
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16");

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    @DisplayName("Verify successful PostgreSQL connection setup")
    void shouldVerifyPostgreSQLConnection() {
        assertThat(postgreSQLContainer.isCreated()).isTrue();
        assertThat(postgreSQLContainer.isRunning()).isTrue();
    }

    @Test
    @DisplayName("Verify Swagger UI is loading correctly")
    public void shouldVerifySwaggerUI() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/swagger-ui/index.html")
                .then()
                .statusCode(200)
                .contentType(ContentType.HTML);
    }

    @Test
    @DisplayName("Verify API documentation is available at /v3/api-docs")
    public void shouldVerifyApiDocsEndpoint() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/v3/api-docs")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }
}
