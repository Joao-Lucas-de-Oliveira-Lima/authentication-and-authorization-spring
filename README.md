
# Authentication and Authorization Spring
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Joao-Lucas-de-Oliveira-Lima_spring-redis-cache&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Joao-Lucas-de-Oliveira-Lima_spring-redis-cache) ![example workflow](https://github.com/Joao-Lucas-de-Oliveira-Lima/authentication-and-authorization-spring/actions/workflows/main.yaml/badge.svg)


This project is a REST API developed with Spring Boot for user authentication and authorization. It uses Java JWT for secure, stateless authentication and Spring Security to manage roles, permissions, and endpoint access control.

## Table of Contents

- [Installation](#installation)
  - [Prerequisites](#prerequisites)
  - [Steps to Install](#steps-to-install)
- [Overview](#overview)
  - [Entities](#entities)
    - [Users](#users)
    - [Permissions](#permissions)
  - [Authentication](#authentication)
  - [Password Persistence](#password-persistence)
  - [Authorization](#authorization)
- [Creating a Custom Password](#creating-a-custom-password)
- [Tests](#tests)
- [Documentation](#documentation)
  - [Swagger](#swagger-documentation)
  - [Main Endpoints](#main-endpoints)
  - [Example Usage](#example-usage)

---

## Installation

### Prerequisites

Ensure you have the following tools installed on your system:

- [Docker Desktop](https://www.docker.com/products/docker-desktop/)
- [Java JDK 21](https://www.oracle.com/br/java/technologies/downloads/#java21)
- [Apache Maven 3.x or later](https://maven.apache.org/download.cgi)

### Steps to Install

Follow these steps to set up and run the application:

1. **Start the PostgreSQL Database**
   Navigate to the root directory of the project and run:
```bash
docker-compose up -d
```
2. **Build the Project** Clean and build the project using Maven:
```bash
mvn clean install
```

3. **Run the Application** Start the Spring Boot application:
```bash
mvn spring-boot:run
```

## Overview

- **Authentication**: Stateless user authentication using JWTs with HMAC512 signature.
- **Authorization**: Role-based and permission-based access control.
- **Password Management**: Secure password storage using PBKDF2 with HMAC-SHA512.
- **Security Filters**: Custom filters to validate and authorize requests.

### Entities

The system adheres to Spring conventions for managing users and permissions by defining two primary entities: `UserModel` and `PermissionModel`. A third intermediate table, `user_permission`, is specified within the `UserModel` entity to store the many-to-many relationship between users and permissions.

#### Users
```java
public class UserModel implements UserDetails {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
        name = "user_permission",
        joinColumns = {@JoinColumn(name = "user_id")},
        inverseJoinColumns = {@JoinColumn(name = "permission_id")}
    )
    private List<PermissionModel> permissions;
    
    @Override
    public String getPassword() {
        return this.password;
    }
  
    @Override
    public String getUsername() {
        return this.username;
    }
    //...
}
```

#### Permissions

```java
public class PermissionModel implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permission_id")
    private Long id;

    private String authority;

    @Override
    public String getAuthority() {
        return this.authority;
    }
    //...
}
```

### Authentication

JWT tokens are signed using the HMAC512 algorithm. The signing process ensures secure, stateless authentication.

```java
public class JwtTokenProvider {
    //...
    @PostConstruct
    protected void init() {
        secret = Base64.getEncoder().encodeToString(secret.getBytes());
        algorithm = Algorithm.HMAC512(secret.getBytes());
    }

    private String createAccessToken(String username, List<String> roles, Date now, Date expire) {
        String issuerUri = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        return JWT.create()
                .withIssuer(issuerUri)
                .withClaim("roles", roles)
                .withSubject(username)
                .withIssuedAt(now)
                .withExpiresAt(expire)
                .sign(algorithm)
                .strip();
    }
    //...
}
```

### Password Persistence

Passwords are securely stored using PBKDF2 with HMAC-SHA512 and a 16-byte salt to protect against brute-force attacks.

```java
public PasswordEncoder passwordEncoder() {
    Map<String, PasswordEncoder> encoders = new HashMap<>();
    PasswordEncoder pbkdf2Encoder = new Pbkdf2PasswordEncoder(
        "", 
        16, 
        310000, 
        Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA512);
    encoders.put("pbkdf2", pbkdf2Encoder);
    DelegatingPasswordEncoder delegatingPasswordEncoder = new DelegatingPasswordEncoder("pbkdf2", encoders);
    delegatingPasswordEncoder.setDefaultPasswordEncoderForMatches(pbkdf2Encoder);
    return delegatingPasswordEncoder;
}
```

### Authorization

Authorization is implemented using Spring Security's `SecurityFilterChain`, which defines the access control policies, and a custom `JwtTokenFilter` to validate JWTs and authenticate incoming requests.

#### SecurityFilterChain
```java
SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    JwtTokenFilter jwtTokenFilter = new JwtTokenFilter(tokenProvider);
    return httpSecurity
            .cors(cors -> {})
            .httpBasic(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                    .requestMatchers(
                            "/auth/**",
                            "/swagger-ui/**",
                            "/v3/api-docs/**"
                    ).permitAll()
                    .requestMatchers("/api/v1/users/**").hasRole("ADMIN")
                    .requestMatchers("/api/v1/**").authenticated()
            )
            .build();
}
```
#### JwtTokenFilter
```java
public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
      String token = jwtTokenProvider.resolveToken((HttpServletRequest) servletRequest);
      if (token != null && jwtTokenProvider.validateToken(token)) {
          Authentication authentication = jwtTokenProvider.getAuthentication(token);
          if (authentication != null) {
              SecurityContextHolder.getContext().setAuthentication(authentication);
          }
      }
      filterChain.doFilter(servletRequest, servletResponse);
}
```

---

## Creating a Custom Password

You can generate an encrypted password for predefined users using the `PasswordEncoder`.

```java
@SpringBootApplication
public class AuthenticationAndAuthorizationSpringApplication {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(...);
        PasswordEncoder passwordEncoder = context.getBean(PasswordEncoder.class);
        String encryptedPassword = passwordEncoder.encode("your_password");
        System.out.println(encryptedPassword);
    }
}
```

---

## Tests

Integration tests are implemented using:
- **JUnit 5**: For test execution.
- **REST Assured**: For HTTP request testing.
- **TestContainers**: For isolated database environments.

To execute tests:
```bash
mvn verify
```
Ensure Docker is running for TestContainers.

---

## Documentation

### Swagger Documentation
Interactive API documentation is available at:
- **Swagger UI:** `/swagger-ui/index.html`
- **API Documentation in JSON:** `/v3/api-docs`

### Main Endpoints

```text
POST /auth/login - Authenticate and return an access token.
POST /auth/register - Register a user and return an access token.
GET /api/v1/users/greetings - Return a greeting for authenticated admins.
```
### Example Usage

**Login**
```http
POST /auth/login
Content-Type: application/json

{
  "username": "example_user",
  "password": "example_password"
}
```

**Register**
```http
POST /auth/register
Content-Type: application/json

{
  "username": "new_user",
  "password": "new_password",
  "roles": ["ROLE_USER"]
}
```

**Fetch Greeting**
```http
GET /api/v1/users/greetings
Authorization: Bearer your_access_token
```
---
