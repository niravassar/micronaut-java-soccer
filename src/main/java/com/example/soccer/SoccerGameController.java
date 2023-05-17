package com.example.soccer;

import com.example.soccer.domain.OrganizedSoccerGame;
import com.example.soccer.domain.Player;
import com.example.soccer.domain.SoccerGame;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@Controller("/soccer")
class SoccerGameController {

    private final SoccerGameRepository soccerGameRepository;
    private final SoccerGameService soccerGameService;

    SoccerGameController(SoccerGameRepository soccerGameRepository, SoccerGameService soccerGameService) {
        this.soccerGameRepository = soccerGameRepository;
        this.soccerGameService = soccerGameService;
    }

    @Get("/{id}")
    SoccerGame show(Long id) {
        return soccerGameRepository
                .findSoccerGameById(id)
                .orElse(null);
    }

    @Get(value = "/list")
    List<SoccerGame> list() {
        return soccerGameRepository.findAllSoccerGames();
    }

    @Post
    HttpResponse<SoccerGame> save(@Body @Valid SoccerGameSaveCommand cmd) {
        SoccerGame soccerGame = soccerGameRepository.saveSoccerGame(cmd.getName(), cmd.getMinPlayers(), cmd.getMaxPlayers());

        return HttpResponse
                .created(soccerGame)
                .headers(headers -> headers.location(location(soccerGame.getId())));
    }

    @Post(value = "/savePlayerToGame")
    HttpResponse<Player> savePlayerToGame(@Body @Valid PlayerSaveCommand cmd) {
        Player player = soccerGameRepository.savePlayerToGame(cmd);

        return HttpResponse
                .created(player)
                .headers(headers -> headers.location(location(player.getId())));
    }

    @Post(value = "/organizeSoccerGames")
    List<OrganizedSoccerGame> organizeSoccerGames() {
        System.out.println("I am here");
        soccerGameService.organizeSoccerGames();
        return List.of(new OrganizedSoccerGame(null));
    }

    private URI location(Long id) {
        return URI.create("/soccer/" + id);
    }
}
