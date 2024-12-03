package edu.jl.authenticationandauthorizationspring.controller;

import edu.jl.authenticationandauthorizationspring.dto.security.AccountCredentialsDto;
import edu.jl.authenticationandauthorizationspring.dto.security.CreateUserDto;
import edu.jl.authenticationandauthorizationspring.mock.UserMock;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.OffsetDateTime;
import java.util.Date;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for {@link AuthController}
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Testcontainers
class AuthControllerIT extends UserMock {

    @LocalServerPort
    private int port;

    @Value("${security.jwt.token.expire-length}")
    private Long validityInMilliseconds;


    @BeforeEach
    void setup() {
        RestAssured.port = port;
    }

    @Container
    @ServiceConnection
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16");

    @Test
    @DisplayName("Verify that the connection to PostgreSQL was established correctly")
    void connectionEstablished() {
        assertThat(postgreSQLContainer.isCreated()).isTrue();
        assertThat(postgreSQLContainer.isRunning()).isTrue();
    }

    @Test
    @DisplayName("Should generate a valid TokenDTO for a registered user")
    void loginSuccessfully() {
        AccountCredentialsDto accountCredentials =
                new AccountCredentialsDto(validAdminUser.getUsername(), validAdminUser.getPassword());
        Response response = given()
                .contentType(ContentType.JSON)
                .body(accountCredentials)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .body("username", equalTo(accountCredentials.getUsername()))
                .body("authenticated", equalTo(true))
                .body("created", is(notNullValue()))
                .body("accessToken", is(notNullValue()))
                .body("refreshToken", is(notNullValue()))
                .extract().response();

        String created = response.jsonPath().getString("created");
        OffsetDateTime offsetDateTime1 = OffsetDateTime.parse(created);
        Date createdTime = Date.from(offsetDateTime1.toInstant());

        String expire = response.jsonPath().getString("expiration");
        OffsetDateTime offsetDateTime2 = OffsetDateTime.parse(expire);
        Date expiration = Date.from(offsetDateTime2.toInstant());

        assertThat(expiration).isEqualTo(new Date(createdTime.getTime() + validityInMilliseconds));
    }

    @Test
    @DisplayName("Should not login and get access token when username is null")
    void loginWithNullUsername() {
        AccountCredentialsDto accountCredentials =
                new AccountCredentialsDto(adminUserWithUsernameNull.getUsername(), adminUserWithUsernameNull.getPassword());

        given()
                .contentType(ContentType.JSON)
                .body(accountCredentials)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Should not login and get access token when username is empty")
    void loginWithEmptyUsername() {
        AccountCredentialsDto accountCredentials =
                new AccountCredentialsDto(adminUserWithUsernameEmpty.getUsername(), adminUserWithUsernameEmpty.getPassword());

        given()
                .contentType(ContentType.JSON)
                .body(accountCredentials)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Should not login and get access token when username is a blank string")
    void loginWithBlankUsername() {
        AccountCredentialsDto accountCredentials =
                new AccountCredentialsDto(adminUserWithUsernameBlank.getUsername(), adminUserWithUsernameBlank.getPassword());

        given()
                .contentType(ContentType.JSON)
                .body(accountCredentials)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Should not login and get access token when username length is less than the minimum of eight characters")
    void loginWithShortUsername() {
        AccountCredentialsDto accountCredentials =
                new AccountCredentialsDto(adminUserWithShortUsername.getUsername(), adminUserWithShortUsername.getPassword());

        given()
                .contentType(ContentType.JSON)
                .body(accountCredentials)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Should not login and get access token when provided password is incorrect")
    void loginWithWrongPassword() {
        AccountCredentialsDto accountCredentials =
                new AccountCredentialsDto(adminUserWithWrongPassword.getUsername(), adminUserWithWrongPassword.getPassword());

        given()
                .contentType(ContentType.JSON)
                .body(accountCredentials)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(401);
    }

    @Test
    @DisplayName("Should not login and get access token when username does not exist in the database")
    void loginWithUnregisteredUsername() {
        AccountCredentialsDto accountCredentials =
                new AccountCredentialsDto(adminUserWithUnregisteredUsername.getUsername(), adminUserWithUnregisteredUsername.getPassword());

        given()
                .contentType(ContentType.JSON)
                .body(accountCredentials)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(401);
    }


    @Test
    @DisplayName("Should successfully save a valid user and return an access token")
    void shouldRegisterValidUserAndReturnAccessToken() {
        CreateUserDto createUser =
                new CreateUserDto(validNewUser.getUsername(), validNewUser.getPassword(), validNewUser.getRoles());
        Response response = given()
                .contentType(ContentType.JSON)
                .body(createUser)
                .when()
                .post("/auth/register")
                .then()
                .statusCode(201)
                .body("username", equalTo(createUser.getUsername()))
                .body("authenticated", equalTo(true))
                .body("created", is(notNullValue()))
                .body("accessToken", is(notNullValue()))
                .body("refreshToken", is(notNullValue()))
                .extract().response();

        String created = response.jsonPath().getString("created");
        OffsetDateTime offsetDateTime1 = OffsetDateTime.parse(created);
        Date createdTime = Date.from(offsetDateTime1.toInstant());

        String expire = response.jsonPath().getString("expiration");
        OffsetDateTime offsetDateTime2 = OffsetDateTime.parse(expire);
        Date expiration = Date.from(offsetDateTime2.toInstant());

        assertThat(expiration).isEqualTo(new Date(createdTime.getTime() + validityInMilliseconds));
    }

    @Test
    @DisplayName("Should not register a user because username is null")
    void shouldNotRegisterUserWithNullUsername() {
        CreateUserDto createUser =
                new CreateUserDto(newUserWithNullUsername.getUsername(), newUserWithNullUsername.getPassword(), newUserWithNullUsername.getRoles());

        given()
                .contentType(ContentType.JSON)
                .body(createUser)
                .when()
                .post("/auth/register")
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Should not register a user because username is empty")
    void shouldNotRegisterUserWithEmptyUsername() {
        CreateUserDto createUser =
                new CreateUserDto(newUserWithEmptyUsername.getUsername(), newUserWithEmptyUsername.getPassword(), newUserWithEmptyUsername.getRoles());

        given()
                .contentType(ContentType.JSON)
                .body(createUser)
                .when()
                .post("/auth/register")
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Should not register a user because username is a blank string")
    void shouldNotRegisterUserWithBlankUsername() {
        CreateUserDto createUser =
                new CreateUserDto(newUserWithBlankUsername.getUsername(), newUserWithBlankUsername.getPassword(), newUserWithBlankUsername.getRoles());

        given()
                .contentType(ContentType.JSON)
                .body(createUser)
                .when()
                .post("/auth/register")
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Should not register a user because username is too short")
    void shouldNotRegisterUserWithShortUsername() {
        CreateUserDto createUser =
                new CreateUserDto(newUserWithShortUsername.getUsername(), newUserWithShortUsername.getPassword(), newUserWithShortUsername.getRoles());

        given()
                .contentType(ContentType.JSON)
                .body(createUser)
                .when()
                .post("/auth/register")
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Should not register a user because password contains only numbers")
    void shouldNotRegisterUserWithNumericPassword() {
        CreateUserDto createUser =
                new CreateUserDto(newUserWithNumericPassword.getUsername(), newUserWithNumericPassword.getPassword(), newUserWithNumericPassword.getRoles());

        given()
                .contentType(ContentType.JSON)
                .body(createUser)
                .when()
                .post("/auth/register")
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Should not register a user because password contains only lowercase letters")
    void shouldNotRegisterUserWithLowercasePassword() {
        CreateUserDto createUser =
                new CreateUserDto(newUserWithLowercasePassword.getUsername(), newUserWithLowercasePassword.getPassword(), newUserWithLowercasePassword.getRoles());

        given()
                .contentType(ContentType.JSON)
                .body(createUser)
                .when()
                .post("/auth/register")
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Should not register a user because password contains only uppercase letters")
    void shouldNotRegisterUserWithUppercasePassword() {
        CreateUserDto createUser =
                new CreateUserDto(newUserWithUppercasePassword.getUsername(), newUserWithUppercasePassword.getPassword(), newUserWithUppercasePassword.getRoles());

        given()
                .contentType(ContentType.JSON)
                .body(createUser)
                .when()
                .post("/auth/register")
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Should not register a user because password contains only special characters")
    void shouldNotRegisterUserWithSpecialCharPassword() {
        CreateUserDto createUser =
                new CreateUserDto(newUserWithSpecialCharPassword.getUsername(), newUserWithSpecialCharPassword.getPassword(), newUserWithSpecialCharPassword.getRoles());

        given()
                .contentType(ContentType.JSON)
                .body(createUser)
                .when()
                .post("/auth/register")
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Should not register a user because password is null")
    void shouldNotRegisterUserWithNullPassword() {
        CreateUserDto createUser =
                new CreateUserDto(newUserWithNullPassword.getUsername(), newUserWithNullPassword.getPassword(), newUserWithNullPassword.getRoles());

        given()
                .contentType(ContentType.JSON)
                .body(createUser)
                .when()
                .post("/auth/register")
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Should not register a user because password is empty")
    void shouldNotRegisterUserWithEmptyPassword() {
        CreateUserDto createUser =
                new CreateUserDto(newUserWithEmptyPassword.getUsername(), newUserWithEmptyPassword.getPassword(), newUserWithEmptyPassword.getRoles());

        given()
                .contentType(ContentType.JSON)
                .body(createUser)
                .when()
                .post("/auth/register")
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Should not register a user because password is a blank string")
    void shouldNotRegisterUserWithBlankPassword() {
        CreateUserDto createUser =
                new CreateUserDto(newUserWithBlankPassword.getUsername(), newUserWithBlankPassword.getPassword(), newUserWithBlankPassword.getRoles());

        given()
                .contentType(ContentType.JSON)
                .body(createUser)
                .when()
                .post("/auth/register")
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Should not register a user because username is already in use")
    void shouldNotRegisterUserWithUsernameAlreadyInUse() {
        CreateUserDto createUser =
                new CreateUserDto(validAdminUser.getUsername(), validAdminUser.getPassword(), validAdminUser.getRoles());

        given()
                .contentType(ContentType.JSON)
                .body(createUser)
                .when()
                .post("/auth/register")
                .then()
                .statusCode(400);
    }
}