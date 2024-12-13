package edu.jl.authenticationandauthorizationspring;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(SpringExtension.class)
@Testcontainers
class AuthenticationAndAuthorizationSpringApplicationTests {

	@LocalServerPort
	private int port;
	@ServiceConnection
	@Container
	private final static PostgreSQLContainer<?> postgreSQLContainer =
			new PostgreSQLContainer<>("postgres:16");

	@BeforeEach
	public void setup(){
		RestAssured.port = port;
	}

	@Test
	@DisplayName("Verify that the connection to PostgreSQL was established correctly")
	void connectionEstablished() {
		assertThat(postgreSQLContainer.isCreated()).isTrue();
		assertThat(postgreSQLContainer.isRunning()).isTrue();
	}

	@Test
	void contextLoads() {
	}

}
