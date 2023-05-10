package com.example.soccer;

import com.example.soccer.domain.Soccer;
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
import static io.micronaut.http.HttpStatus.NO_CONTENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@MicronautTest // <1>
class SoccerControllerTest {

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

        HttpRequest<?> request = HttpRequest.POST("/genres", new GenreSaveCommand("DevOps")); // <3>
        HttpResponse<?> response = blockingClient.exchange(request);
        genreIds.add(entityId(response));

        assertEquals(CREATED, response.getStatus());

        request = HttpRequest.POST("/genres", new GenreSaveCommand("Microservices")); // <3>
        response = blockingClient.exchange(request);

        assertEquals(CREATED, response.getStatus());

        Long id = entityId(response);
        genreIds.add(id);
        request = HttpRequest.GET("/genres/" + id);

        Soccer soccer = blockingClient.retrieve(request, Soccer.class); // <4>

        assertEquals("Microservices", soccer.getName());

        request = HttpRequest.PUT("/genres", new GenreUpdateCommand(id, "Micro-services"));
        response = blockingClient.exchange(request);  // <5>

        assertEquals(NO_CONTENT, response.getStatus());

        request = HttpRequest.GET("/genres/" + id);
        soccer = blockingClient.retrieve(request, Soccer.class);
        assertEquals("Micro-services", soccer.getName());

        request = HttpRequest.GET("/genres/list");
        List<Soccer> soccers = blockingClient.retrieve(request, Argument.of(List.class, Soccer.class));

        assertEquals(2, soccers.size());

        request = HttpRequest.POST("/genres/ex", new GenreSaveCommand("Microservices")); // <3>
        response = blockingClient.exchange(request);

        assertEquals(NO_CONTENT, response.getStatus());

        request = HttpRequest.GET("/genres/list");
        soccers = blockingClient.retrieve(request, Argument.of(List.class, Soccer.class));

        assertEquals(2, soccers.size());

        request = HttpRequest.GET("/genres/list?max=1");
        soccers = blockingClient.retrieve(request, Argument.of(List.class, Soccer.class));

        assertEquals(1, soccers.size());
        assertEquals("DevOps", soccers.get(0).getName());

        request = HttpRequest.GET("/genres/list?max=1&order=desc&sort=name");
        soccers = blockingClient.retrieve(request, Argument.of(List.class, Soccer.class));

        assertEquals(1, soccers.size());
        assertEquals("Micro-services", soccers.get(0).getName());

        request = HttpRequest.GET("/genres/list?max=1&offset=10");
        soccers = blockingClient.retrieve(request, Argument.of(List.class, Soccer.class));

        assertEquals(0, soccers.size());

        // cleanup:
        for (Long genreId : genreIds) {
            request = HttpRequest.DELETE("/genres/" + genreId);
            response = blockingClient.exchange(request);
            assertEquals(NO_CONTENT, response.getStatus());
        }
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
