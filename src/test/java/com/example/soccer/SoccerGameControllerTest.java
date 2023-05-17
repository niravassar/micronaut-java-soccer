package com.example.soccer;

import com.example.soccer.domain.OrganizedSoccerGame;
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

import java.util.List;
import java.util.stream.Collectors;

import static io.micronaut.http.HttpHeaders.LOCATION;
import static io.micronaut.http.HttpStatus.CREATED;
import static io.micronaut.http.HttpStatus.NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
class SoccerGameControllerTest {

    private BlockingHttpClient blockingClient;

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    SoccerGameRepository soccerGameRepository;

    @BeforeEach
    void setup() {
        blockingClient = client.toBlocking();
    }

    @Test
    void testFindNonExistingGenreReturns404() {
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () ->
            blockingClient.exchange(HttpRequest.GET("/soccer/99"))
        );

        assertNotNull(thrown.getResponse());
        assertEquals(NOT_FOUND, thrown.getStatus());
    }

    @Test
    void testGenreCrudOperations() {

        HttpRequest<?> request = HttpRequest.POST("/soccer", new SoccerGameSaveCommand("Saturday Pickup", 6,8));
        HttpResponse<?> response = blockingClient.exchange(request);

        assertEquals(CREATED, response.getStatus());

        request = HttpRequest.POST("/soccer", new SoccerGameSaveCommand("Sunday Pickup",3,5));
        response = blockingClient.exchange(request);

        assertEquals(CREATED, response.getStatus());

        Long id = entityId(response);
        request = HttpRequest.GET("/soccer/" + id);

        SoccerGame soccerGame = blockingClient.retrieve(request, SoccerGame.class);

        assertEquals("Sunday Pickup", soccerGame.getName());
        assertEquals(3, soccerGame.getMinPlayers());
        assertEquals(5, soccerGame.getMaxPlayers());

        request = HttpRequest.GET("/soccer/list");
        List<SoccerGame> soccerGames = blockingClient.retrieve(request, Argument.of(List.class, SoccerGame.class));

        assertEquals(2, soccerGames.size());
        assertEquals("Saturday Pickup", soccerGames.get(0).getName());
        assertEquals("Sunday Pickup", soccerGames.get(1).getName());
    }

    @Test
    void testSavePlayerToGame() {

        // save game
        HttpRequest<?> request = HttpRequest.POST("/soccer", new SoccerGameSaveCommand("Monday Pickup", 6,8));
        HttpResponse<?> response = blockingClient.exchange(request);
        Long soccerGameId = entityId(response);

        // save player to game
        HttpRequest<?> playerRequest = HttpRequest.POST("/soccer/savePlayerToGame", new PlayerSaveCommand(soccerGameId, "Shreyas Assar", 16));
        HttpResponse<?> playerResponse = blockingClient.exchange(playerRequest);

        List<SoccerGame> soccerGames = soccerGameRepository.findAllSoccerGames().stream().filter( sg -> sg.getName().equals("Monday Pickup")).collect(Collectors.toList());
        assertEquals("Monday Pickup", soccerGames.get(0).getName());
        assertEquals("Shreyas Assar", soccerGames.get(0).getPlayerPool().stream().findFirst().get().getName());;
        assertEquals(16, soccerGames.get(0).getPlayerPool().stream().findFirst().get().getAge());;
    }

    @Test
    void testOrganizeSoccerEvents() {
        HttpRequest<?> request = HttpRequest.POST("/soccer/organizeSoccerGames", null);
        List<OrganizedSoccerGame> organizedSoccerGames = blockingClient.retrieve(request, Argument.of(List.class, OrganizedSoccerGame.class));
        System.out.println(organizedSoccerGames.get(0));
    }

    private Long entityId(HttpResponse response) {
        String path = "/soccer/";
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
