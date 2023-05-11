package com.example.soccer;

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

    SoccerGameController(SoccerGameRepository soccerGameRepository) { // <3>
        this.soccerGameRepository = soccerGameRepository;
    }

    @Get("/{id}")
    SoccerGame show(Long id) {
        return soccerGameRepository
                .findById(id)
                .orElse(null);
    }

    @Get(value = "/list")
    List<SoccerGame> list() {
        return soccerGameRepository.findAll();
    }

    @Post
    HttpResponse<SoccerGame> save(@Body @Valid SoccerGameSaveCommand cmd) {
        SoccerGame soccerGame = soccerGameRepository.save(cmd.getName());

        return HttpResponse
                .created(soccerGame)
                .headers(headers -> headers.location(location(soccerGame.getId())));
    }

    private URI location(Long id) {
        return URI.create("/soccer/" + id);
    }
}
