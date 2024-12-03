package edu.jl.authenticationandauthorizationspring.dto.security;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class AccountCredentialsDto implements Serializable {
    @Serial
    private final static long serialVersionUID = 1L;

    @NotBlank
    @Pattern(
            regexp = "^.{8,}$",
            message = "Username must be at least 8 characters long and not be empty."
    )
    private String username;
    @NotBlank
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\p{Alnum}])[\\p{Alnum}\\p{Punct}\\p{S}çÇ]{8,}$",
            message = "Password must be at least 8 characters long, including one uppercase letter, one lowercase letter, one number, and one special character."
    )
    private String password;

    public AccountCredentialsDto() {
    }

    public AccountCredentialsDto(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        AccountCredentialsDto that = (AccountCredentialsDto) object;
        return Objects.equals(username, that.username) && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password);
    }
}
