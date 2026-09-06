package com.userservice.netflux.user.controller;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;

import com.userservice.netflux.user.AbstractApiTest;
import com.userservice.netflux.user.dto.AddWatchlistRequest;

import static org.assertj.core.api.Assertions.assertThat;

class WatchlistApiTest extends AbstractApiTest {

    @Autowired
    RestTestClient restTestClient;

    @Test
    @DisplayName("Should return the watchlist for a user")
    @Sql(scripts = "/sql/insert-watchlist-items.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/delete-watchlist-items.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldReturnWatchlistForUser() {
        var movieIds = restTestClient.get()
                .uri("/api/users/watchlist")
                .header("X-User-Id", "100")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<Long>>() {})
                .returnResult()
                .getResponseBody();

        assertThat(movieIds).containsExactlyInAnyOrder(123L, 456L);
    }

    @Test
    @DisplayName("Should return an empty list when the user has no watchlist items")
    void shouldReturnEmptyListWhenNoWatchlistItems() {
        var movieIds = restTestClient.get()
                .uri("/api/users/watchlist")
                .header("X-User-Id", "999")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<Long>>() {})
                .returnResult()
                .getResponseBody();

        assertThat(movieIds).isEmpty();
    }

    @Test
    @DisplayName("Should add a movie to the watchlist")
    @Sql(scripts = "/sql/delete-watchlist-test-users.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldAddMovieToWatchlist() {
        restTestClient.post()
                .uri("/api/users/watchlist")
                .header("X-User-Id", "200")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new AddWatchlistRequest(111L))
                .exchange()
                .expectStatus().isCreated();

        var movieIds = restTestClient.get()
                .uri("/api/users/watchlist")
                .header("X-User-Id", "200")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<Long>>() {})
                .returnResult()
                .getResponseBody();

        assertThat(movieIds).containsExactly(111L);
    }

    @Test
    @DisplayName("Should not duplicate a watchlist entry when adding the same movie twice")
    @Sql(scripts = "/sql/delete-watchlist-test-users.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldNotDuplicateWhenAddingSameMovieTwice() {
        restTestClient.post()
                .uri("/api/users/watchlist")
                .header("X-User-Id", "201")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new AddWatchlistRequest(222L))
                .exchange()
                .expectStatus().isCreated();

        restTestClient.post()
                .uri("/api/users/watchlist")
                .header("X-User-Id", "201")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new AddWatchlistRequest(222L))
                .exchange()
                .expectStatus().isCreated();

        var movieIds = restTestClient.get()
                .uri("/api/users/watchlist")
                .header("X-User-Id", "201")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<Long>>() {})
                .returnResult()
                .getResponseBody();

        assertThat(movieIds).containsExactly(222L);
    }

    @Test
    @DisplayName("Should remove a movie from the watchlist")
    void shouldRemoveMovieFromWatchlist() {
        restTestClient.post()
                .uri("/api/users/watchlist")
                .header("X-User-Id", "202")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new AddWatchlistRequest(333L))
                .exchange()
                .expectStatus().isCreated();

        restTestClient.delete()
                .uri("/api/users/watchlist/{movieId}", 333)
                .header("X-User-Id", "202")
                .exchange()
                .expectStatus().isNoContent();

        var movieIds = restTestClient.get()
                .uri("/api/users/watchlist")
                .header("X-User-Id", "202")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<Long>>() {})
                .returnResult()
                .getResponseBody();

        assertThat(movieIds).isEmpty();
    }

    @Test
    @DisplayName("Should return 204 when removing a movie that was never added")
    void shouldReturn204WhenRemovingNonExistentMovie() {
        restTestClient.delete()
                .uri("/api/users/watchlist/{movieId}", 444)
                .header("X-User-Id", "203")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @DisplayName("Should return 401 ProblemDetail when X-User-Id header is missing")
    void shouldReturn401WhenUserIdHeaderMissing() {
        restTestClient.get()
                .uri("/api/users/watchlist")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("Should return 401 ProblemDetail when X-User-Id header is invalid")
    void shouldReturn401WhenUserIdHeaderInvalid() {
        restTestClient.get()
                .uri("/api/users/watchlist")
                .header("X-User-Id", "not-a-number")
                .exchange()
                .expectStatus().isUnauthorized();
    }
}
