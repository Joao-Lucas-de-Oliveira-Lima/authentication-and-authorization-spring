package edu.jl.authenticationandauthorizationspring.controller;

import edu.jl.authenticationandauthorizationspring.mock.JwtMock;
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
 * Class responsible for validating JSON Web Tokens
 */
@Testcontainers
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class JwtValidationIT extends JwtMock {

    @LocalServerPort
    private int port;

    @Container
    @ServiceConnection
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16");

    @BeforeEach
    void setup() {
        RestAssured.port = port;
    }

    @Test
    @DisplayName("Verify that the PostgreSQL connection is established correctly")
    void shouldVerifyPostgreSQLConnection() {
        assertThat(postgreSQLContainer.isCreated()).isTrue();
        assertThat(postgreSQLContainer.isRunning()).isTrue();
    }

    @Test
    @DisplayName("Should return status code 200 for a valid access token with the correct secret and a registered user in the database")
    void shouldReturn200ForValidAccessToken() {
        String accessToken = generateValidToken(validAdminUser.getUsername(), validAdminUser.getRoles());
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get("/api/v1/users/greetings")
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("Should return status code 403 for a user with insufficient permissions to access the specified route")
    void shouldReturn403ForUnauthorizedUser() {
        String accessToken = generateValidToken(guestUser.getUsername(), guestUser.getRoles());
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get("/api/v1/users/greetings")
                .then()
                .statusCode(403);
    }


    @Test
    @DisplayName("Should return status code 500 if the token has expired")
    void shouldReturn500ForExpiredToken() throws InterruptedException {
        String accessToken = generateTokenWithOneSecondValidity(guestUser.getUsername(), guestUser.getRoles());
        Thread.sleep(2000);
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get("/api/v1/users/greetings")
                .then()
                .statusCode(500);
    }

    @Test
    @DisplayName("Should return status code 500 if the token has an incorrect secret")
    void shouldReturn500ForInvalidTokenSecret() {
        String accessToken = generateInvalidToken(guestUser.getUsername(), guestUser.getRoles());
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get("/api/v1/users/greetings")
                .then()
                .statusCode(500);
    }

    @Test
    @DisplayName("Should return status code 403 if the Authorization header is missing")
    void shouldReturn403IfAuthorizationHeaderIsMissing() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/v1/users/greetings")
                .then()
                .statusCode(403);
    }

    @Test
    @DisplayName("Should return status code 403 if the Authorization header is missing the 'Bearer' prefix")
    void shouldReturn403IfAuthorizationHeaderIsMissingBearerPrefix() {
        String accessToken = generateValidToken(guestUser.getUsername(), guestUser.getRoles());
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", accessToken)
                .when()
                .get("/api/v1/users/greetings")
                .then()
                .statusCode(403);
    }

    @Test
    @DisplayName("Should return status code 403 if the Authorization header contains an empty token")
    void shouldReturn403IfTokenIsEmpty() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer ")
                .when()
                .get("/api/v1/users/greetings")
                .then()
                .statusCode(403);
    }
}
