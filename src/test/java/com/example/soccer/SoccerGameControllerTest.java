package com.example.soccer;

import com.example.soccer.domain.SoccerGame;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static io.micronaut.http.HttpHeaders.LOCATION;
import static io.micronaut.http.HttpStatus.BAD_REQUEST;
import static io.micronaut.http.HttpStatus.CREATED;
import static io.micronaut.http.HttpStatus.NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@MicronautTest // <1>
class SoccerGameControllerTest {

    private BlockingHttpClient blockingClient;

    @Inject
    @Client("/")
    HttpClient client; // <2>

    @BeforeEach
    void setup() {
        blockingClient = client.toBlocking();
    }

    @Test
    void supplyAnInvalidOrderTriggersValidationFailure() {
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () ->
                blockingClient.exchange(HttpRequest.GET("/genres/list?order=foo"))
        );

        assertNotNull(thrown.getResponse());
        assertEquals(BAD_REQUEST, thrown.getStatus());
    }

    @Test
    void testFindNonExistingGenreReturns404() {
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () ->
            blockingClient.exchange(HttpRequest.GET("/genres/99"))
        );

        assertNotNull(thrown.getResponse());
        assertEquals(NOT_FOUND, thrown.getStatus());
    }

    @Test
    void testGenreCrudOperations() {

        List<Long> genreIds = new ArrayList<>();

        HttpRequest<?> request = HttpRequest.POST("/genres", new SoccerGameSaveCommand("DevOps")); // <3>
        HttpResponse<?> response = blockingClient.exchange(request);
        genreIds.add(entityId(response));

        assertEquals(CREATED, response.getStatus());

        request = HttpRequest.POST("/genres", new SoccerGameSaveCommand("Microservices")); // <3>
        response = blockingClient.exchange(request);

        assertEquals(CREATED, response.getStatus());

        Long id = entityId(response);
        genreIds.add(id);
        request = HttpRequest.GET("/genres/" + id);

        SoccerGame soccerGame = blockingClient.retrieve(request, SoccerGame.class); // <4>

        assertEquals("Microservices", soccerGame.getName());

        request = HttpRequest.GET("/genres/list");
        List<SoccerGame> soccerGames = blockingClient.retrieve(request, Argument.of(List.class, SoccerGame.class));

        assertEquals(2, soccerGames.size());

        request = HttpRequest.GET("/genres/list");
        soccerGames = blockingClient.retrieve(request, Argument.of(List.class, SoccerGame.class));

        assertEquals(2, soccerGames.size());

        request = HttpRequest.GET("/genres/list?max=1");
        soccerGames = blockingClient.retrieve(request, Argument.of(List.class, SoccerGame.class));

        assertEquals(1, soccerGames.size());
        assertEquals("DevOps", soccerGames.get(0).getName());

        request = HttpRequest.GET("/genres/list?max=1&order=desc&sort=name");
        soccerGames = blockingClient.retrieve(request, Argument.of(List.class, SoccerGame.class));

        assertEquals(1, soccerGames.size());
        assertEquals("Microservices", soccerGames.get(0).getName());

        request = HttpRequest.GET("/genres/list?max=1&offset=10");
        soccerGames = blockingClient.retrieve(request, Argument.of(List.class, SoccerGame.class));

        assertEquals(0, soccerGames.size());
    }

    private Long entityId(HttpResponse response) {
        String path = "/genres/";
        String value = response.header(LOCATION);
        if (value == null) {
            return null;
        }

        int index = value.indexOf(path);
        if (index != -1) {
            return Long.valueOf(value.substring(index + path.length()));
        }

        return null;
    }
}
