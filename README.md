# Authentication and Authorization with Spring Boot

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white) ![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white) ![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens) ![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white) ![Swagger](https://img.shields.io/badge/-Swagger-%23Clojure?style=for-the-badge&logo=swagger&logoColor=white) ![SonarQube](https://img.shields.io/badge/SonarQube-black?style=for-the-badge&logo=sonarqube&logoColor=4E9BCD) ![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Joao-Lucas-de-Oliveira-Lima_spring-redis-cache&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Joao-Lucas-de-Oliveira-Lima_spring-redis-cache) ![Workflow Status](https://github.com/Joao-Lucas-de-Oliveira-Lima/authentication-and-authorization-spring/actions/workflows/main.yaml/badge.svg)

## Overview

Authentication and authorization for a REST API implemented using `spring-boot-starter-security` and `java-jwt` from Auth0.

## Table of Contents

- [Installation](#installation)
  - [Prerequisites](#prerequisites)
  - [Installation Steps](#installation-steps)
- [Usage](#usage)
- [Entities](#entities)
  - [Users](#users)
  - [Permissions](#permissions)
- [Security](#security)
  - [Authentication](#authentication)
  - [JWT Token Filter](#jwt-token-filter)
  - [JWT Token Provider](#jwt-token-provider)
  - [Password Security](#password-security)
- [Testing](#testing)
- [API Documentation](#api-documentation)

---

## Installation

### Prerequisites

Ensure you have the following tools installed:

- [Docker Desktop](https://www.docker.com/products/docker-desktop/)
- [Java JDK 21](https://www.oracle.com/java/technologies/downloads/#java21)

### Installation Steps

1. **Start the PostgreSQL Database**
   Navigate to the root directory of the project and run:

   ```bash
   docker-compose up api_db -d
   ```

2. **Run the Application**
   Start the Spring Boot application:

   ```bash
   ./mvnw spring-boot:run
   ```

By default, the application runs at `http://localhost:8001/api`.

## Usage

1. Register a new user by sending a request to `/auth/register`.
2. Log in via `/auth/login` to receive an access token.
3. Use the access token in the `Authorization` header as `Bearer token`.
4. Access protected endpoints like `/api/v1/users` (requires admin role).
5. Roles must follow the format `ROLE_ADMIN`, using the `ROLE_` prefix.
6. More details on required request payloads can be found in the Swagger documentation.

---

## Entities

### Users

The `UserModel` entity implements `UserDetails` and has a many-to-many relationship with permissions:

```java
@Entity @Table(name = "users") @NoArgsConstructor @Data
public class UserModel implements UserDetails {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "user_id")
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
  public Collection<? extends GrantedAuthority> getAuthorities() {
      return permissions;
  }
}
```

### Permissions

```java
@Entity @Table(name = "permissions") @Data @NoArgsConstructor
public class PermissionModel implements GrantedAuthority {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "permission_id")
    private Long id;
    
    private String authority;
    
    public PermissionModel(String authority) {
        this.authority = authority;
    }
}
```

## Security

### Authentication

A `SecurityFilterChain` is configured to:
- Disable basic HTTP authentication
- Delegate CORS policies
- Set session management to stateless
- Delegate the appropriate filter to be used in token validation

```java
@Bean
SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    JwtTokenFilter jwtTokenFilter = new JwtTokenFilter(tokenProvider);
    return httpSecurity
            .cors(cors -> {})
            .httpBasic(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize -> authorize
                    .requestMatchers("/auth/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                    .requestMatchers("/api/v1/users/**").hasRole("ADMIN")
                    .requestMatchers("/api/v1/**").authenticated()
            )
            .build();
}
```

### JWT Token Filter

A filter that verifies JWT tokens in incoming requests:

```java
public class JwtTokenFilter extends GenericFilter {
    @Override
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
}
```

### JWT Token Provider

The JWT is generated using an HMAC-SHA512 hash function. This class is responsible for handling token creation, validation, and decoding:

```java
public class JwtTokenProvider {
    @PostConstruct
    protected void init() {
        secret = Base64.getEncoder().encodeToString(secret.getBytes());
        algorithm = Algorithm.HMAC512(secret.getBytes());
    }
     public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring("Bearer ".length());
        }
        return null;
    }
    public Boolean validateToken(String token) throws InvalidJwtAuthenticationException {
        try {
            DecodedJWT decodedJWT = decodedToken(token);
            if (decodedJWT.getExpiresAt().before(new Date())) {
                throw new InvalidJwtAuthenticationException("Token expired!");
            }
            return true;
        } catch (Exception e) {
            throw new InvalidJwtAuthenticationException("Invalid token!");
        }
    }
    // Additional methods for token generation and management
}
```

### Password Security

Passwords are securely stored using PBKDF2 with HMAC-SHA512:

```java
@Bean
public PasswordEncoder passwordEncoder() {
    Map<String, PasswordEncoder> encoders = new HashMap<>();
    PasswordEncoder pbkdf2Encoder = new Pbkdf2PasswordEncoder("", 16, 310000, 
      Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA512);
    encoders.put("pbkdf2", pbkdf2Encoder);
    DelegatingPasswordEncoder delegatingPasswordEncoder = new DelegatingPasswordEncoder("pbkdf2", encoders);
    delegatingPasswordEncoder.setDefaultPasswordEncoderForMatches(pbkdf2Encoder);
    return delegatingPasswordEncoder;
}
```

## Testing

To execute tests:

```bash
./mvnw verify
```
> Ensure Docker is running for TestContainers.

## API Documentation

- **Swagger UI:** Access interactive API documentation at [`/swagger-ui/index.html`](http://localhost:8080/swagger-ui/index.html).
- **OpenAPI Specification (JSON):** Retrieve the API spec at [`/v3/api-docs`](http://localhost:8080/v3/api-docs).

