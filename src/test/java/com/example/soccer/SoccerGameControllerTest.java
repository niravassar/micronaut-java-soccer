package com.example.soccer;

import com.example.soccer.domain.OrganizedSoccerGame;
import com.example.soccer.domain.Player;
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
import org.junit.jupiter.api.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static io.micronaut.http.HttpHeaders.LOCATION;
import static io.micronaut.http.HttpStatus.CREATED;
import static io.micronaut.http.HttpStatus.NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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
    @Order(0)
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
    @Order(1)
    void testSavePlayerToGame_minNotMet() {

        // save game
        HttpRequest<?> request = HttpRequest.POST("/soccer", new SoccerGameSaveCommand("Thursday Pickup", 4,10));
        HttpResponse<?> response = blockingClient.exchange(request);
        Long soccerGameId = entityId(response);

        // save player to game
        HttpRequest<?> playerRequest = HttpRequest.POST("/soccer/savePlayerToGame", new PlayerSaveCommand(soccerGameId, "Bob", 12));
        HttpResponse<?> playerResponse = blockingClient.exchange(playerRequest);

        // organize game
        HttpRequest<?> organizedRequest = HttpRequest.POST("/soccer/organizeSoccerGames", null);
        List<OrganizedSoccerGame> organizedSoccerGames = blockingClient.retrieve(organizedRequest, Argument.of(List.class, OrganizedSoccerGame.class));
        OrganizedSoccerGame organizedSoccerGame = organizedSoccerGames.stream().filter( og -> "Thursday Pickup".equals(og.getSoccerGame().getName())).findAny().orElse(null);
        assertEquals("This game cannot be played because it has only 1 players and we need minimum 4 players.", organizedSoccerGame.getGameInstructions());
    }

    @Test
    void testSavePlayerToGame() {

        // save game
        HttpRequest<?> request = HttpRequest.POST("/soccer", new SoccerGameSaveCommand("Monday Pickup", 6,8));
        HttpResponse<?> response = blockingClient.exchange(request);
        Long soccerGameId = entityId(response);

        // save player to game
        HttpRequest<?> playerRequest = HttpRequest.POST("/soccer/savePlayerToGame", new PlayerSaveCommand(soccerGameId, "Nirav Assar", 45));
        HttpResponse<?> playerResponse = blockingClient.exchange(playerRequest);

        SoccerGame soccerGame = soccerGameRepository.findAllSoccerGames().stream().filter( sg -> sg.getName().equals("Monday Pickup")).findAny().orElse(null);
        assertEquals("Monday Pickup", soccerGame.getName());
        assertEquals("Nirav Assar", soccerGame.getPlayerPool().stream().findFirst().get().getName());;
        assertEquals(45, soccerGame.getPlayerPool().stream().findFirst().get().getAge());;
    }

    @Test
    void testOrganizeSoccerEvents_game() {
        // save game
        HttpRequest<?> soccerGameRequest = HttpRequest.POST("/soccer", new SoccerGameSaveCommand("Tues Pickup", 2,4));
        HttpResponse<?> response = blockingClient.exchange(soccerGameRequest);
        Long soccerGameId = entityId(response);

        // save players to game
        HttpRequest<?> playerRequest = HttpRequest.POST("/soccer/savePlayerToGame", new PlayerSaveCommand(soccerGameId, "Nirav Assar",45 ));
        HttpResponse<?> playerResponse = blockingClient.exchange(playerRequest);
        playerRequest = HttpRequest.POST("/soccer/savePlayerToGame", new PlayerSaveCommand(soccerGameId, "Shreyas Assar", 16));
        playerResponse = blockingClient.exchange(playerRequest);

        // organize games
        HttpRequest<?> organizedRequest = HttpRequest.POST("/soccer/organizeSoccerGames", null);
        List<OrganizedSoccerGame> organizedSoccerGames = blockingClient.retrieve(organizedRequest, Argument.of(List.class, OrganizedSoccerGame.class));
        OrganizedSoccerGame organizedSoccerGame = organizedSoccerGames.stream().filter( og -> "Tues Pickup".equals(og.getSoccerGame().getName())).findAny().orElse(null);
        assertEquals(2, organizedSoccerGame.getSoccerGame().getMinPlayers());
        assertEquals("Shreyas Assar", organizedSoccerGame.getTeamAPlayers().get(0).getName());
        assertEquals("Nirav Assar", organizedSoccerGame.getTeamBPlayers().get(0).getName());
        assertTrue(organizedSoccerGame.getGameInstructions().contains("This game is titled"));
        assertTrue(organizedSoccerGame.getGameInstructions().contains("Team A will have"));
    }

    @Test
    void testOrganizeSoccerEvents_simpleGameWithSubs() {
        // save game
        HttpRequest<?> soccerGameRequest = HttpRequest.POST("/soccer", new SoccerGameSaveCommand("Wed Pickup", 2,4));
        HttpResponse<?> response = blockingClient.exchange(soccerGameRequest);
        Long soccerGameId = entityId(response);

        // save players to game
        HttpRequest<?> playerRequest = HttpRequest.POST("/soccer/savePlayerToGame", new PlayerSaveCommand(soccerGameId, "Nirav Assar",45 ));
        HttpResponse<?> playerResponse = blockingClient.exchange(playerRequest);
        playerRequest = HttpRequest.POST("/soccer/savePlayerToGame", new PlayerSaveCommand(soccerGameId, "Shreyas Assar", 16));
        playerResponse = blockingClient.exchange(playerRequest);
        playerRequest = HttpRequest.POST("/soccer/savePlayerToGame", new PlayerSaveCommand(soccerGameId, "Abhinay Assar", 12));
        playerResponse = blockingClient.exchange(playerRequest);
        playerRequest = HttpRequest.POST("/soccer/savePlayerToGame", new PlayerSaveCommand(soccerGameId, "Aditya Assar", 14));
        playerResponse = blockingClient.exchange(playerRequest);

        // organize games
        HttpRequest<?> organizedRequest = HttpRequest.POST("/soccer/organizeSoccerGames", null);
        List<OrganizedSoccerGame> organizedSoccerGames = blockingClient.retrieve(organizedRequest, Argument.of(List.class, OrganizedSoccerGame.class));
        OrganizedSoccerGame organizedSoccerGame = organizedSoccerGames.stream().filter( og -> "Wed Pickup".equals(og.getSoccerGame().getName())).findAny().orElse(null);
        assertEquals(2, organizedSoccerGame.getSoccerGame().getMinPlayers());

        List<Player> playersA = organizedSoccerGame.getTeamAPlayers().stream().sorted(Comparator.comparing(Player::getAge)).collect(Collectors.toList());
        List<Player> playersB = organizedSoccerGame.getTeamBPlayers().stream().sorted(Comparator.comparing(Player::getAge)).collect(Collectors.toList());
        assertEquals("Abhinay Assar", playersA.get(0).getName());
        assertEquals("Shreyas Assar", playersA.get(1).getName());
        assertEquals("Aditya Assar", playersB.get(0).getName());
        assertEquals("Nirav Assar", playersB.get(1).getName());
        assertTrue(organizedSoccerGame.getGameInstructions().contains("This game is titled"));
        assertTrue(organizedSoccerGame.getGameInstructions().contains("Team A will have"));
    }

    @Test
    void testCreateGameInstructions() throws ParseException {
        SoccerGame soccerGame = new SoccerGame("Friends Soccer Game", 2, 4);

        OrganizedSoccerGame organizedSoccerGame = new OrganizedSoccerGame(soccerGame);
        LocalDate localDate = LocalDate.of(2023, 5, 22);
        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        organizedSoccerGame.setDateOrganized(date);

        organizedSoccerGame.addTeamAPlayer(new Player("Kevin P", 30));
        organizedSoccerGame.addTeamAPlayer(new Player("Danny P", 32));
        organizedSoccerGame.addTeamBPlayer(new Player("Jorge P", 36));
        organizedSoccerGame.addTeamBPlayer(new Player("Nirav A", 45));

        organizedSoccerGame.createGameInstructions();
        assertEquals("This game is titled `Friends Soccer Game` and will take place on " + date + ". The game needs a min of 2 and a max of 4. They are split into two teams. Team A will have [Kevin P - Age: 30, Danny P - Age: 32]. Team B will have [Jorge P - Age: 36, Nirav A - Age: 45].", organizedSoccerGame.getGameInstructions());
    }
        /**********************************************************************************************************/

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
