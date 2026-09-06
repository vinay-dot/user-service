package com.userservice.netflux.user.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.servlet.client.RestTestClient;

import com.userservice.netflux.user.AbstractApiTest;
import com.userservice.netflux.user.dto.LoginRequest;
import com.userservice.netflux.user.dto.LoginResponse;
import com.userservice.netflux.user.dto.RegisterRequest;

import static org.assertj.core.api.Assertions.assertThat;

class AuthApiTest extends AbstractApiTest {

    @Autowired
    RestTestClient restTestClient;

    @Test
    @DisplayName("Should register a new user")
    void shouldRegisterNewUser() {
        var request = new RegisterRequest("newuser-register", "password123", "New User");

        restTestClient.post()
                .uri("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    @DisplayName("Should return 409 ProblemDetail when username already exists")
    void shouldReturn409WhenUsernameAlreadyExists() {
        var request = new RegisterRequest("duplicate-user", "password123", "Duplicate User");

        restTestClient.post()
                .uri("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isCreated();

        var problem = restTestClient.post()
                .uri("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody(ProblemDetail.class)
                .returnResult()
                .getResponseBody();

        assertThat(problem.getStatus()).isEqualTo(409);
    }

    @Test
    @DisplayName("Should return 409 ProblemDetail when username already exists with different case")
    void shouldReturn409WhenUsernameAlreadyExistsWithDifferentCase() {
        restTestClient.post()
                .uri("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new RegisterRequest("CaseUser", "password123", "Case User"))
                .exchange()
                .expectStatus().isCreated();

        restTestClient.post()
                .uri("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new RegisterRequest("caseuser", "password123", "Case User"))
                .exchange()
                .expectStatus().isEqualTo(409);
    }

    @Test
    @DisplayName("Should login and return a token with the user's name")
    void shouldLoginAndReturnToken() {
        restTestClient.post()
                .uri("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new RegisterRequest("login-user", "password123", "Login User"))
                .exchange()
                .expectStatus().isCreated();

        var response = restTestClient.post()
                .uri("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new LoginRequest("login-user", "password123"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(LoginResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response.token()).isNotBlank();
        assertThat(response.name()).isEqualTo("Login User");
    }

    @Test
    @DisplayName("Should login with a different username case")
    void shouldLoginWithDifferentUsernameCase() {
        restTestClient.post()
                .uri("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new RegisterRequest("CaseLogin", "password123", "Case Login"))
                .exchange()
                .expectStatus().isCreated();

        restTestClient.post()
                .uri("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new LoginRequest("caselogin", "password123"))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("Should return 401 ProblemDetail when password is incorrect")
    void shouldReturn401WhenPasswordIncorrect() {
        restTestClient.post()
                .uri("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new RegisterRequest("wrongpass-user", "password123", "Wrong Pass"))
                .exchange()
                .expectStatus().isCreated();

        var problem = restTestClient.post()
                .uri("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new LoginRequest("wrongpass-user", "incorrect"))
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody(ProblemDetail.class)
                .returnResult()
                .getResponseBody();

        assertThat(problem.getStatus()).isEqualTo(401);
    }

    @Test
    @DisplayName("Should return 401 ProblemDetail when username does not exist")
    void shouldReturn401WhenUsernameDoesNotExist() {
        restTestClient.post()
                .uri("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new LoginRequest("nonexistent-user", "password123"))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("Should return 400 ProblemDetail when register fields are blank")
    void shouldReturn400WhenRegisterFieldsBlank() {
        var problem = restTestClient.post()
                .uri("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new RegisterRequest("", "", ""))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class)
                .returnResult()
                .getResponseBody();

        assertThat(problem.getStatus()).isEqualTo(400);
    }
}
