package com.example.soccer;

import com.example.soccer.domain.SoccerGame;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;

import javax.persistence.PersistenceException;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static io.micronaut.http.HttpHeaders.LOCATION;

@ExecuteOn(TaskExecutors.IO)  // <1>
@Controller("/genres")  // <2>
class GenreController {

    private final SoccerGameRepository soccerGameRepository;

    GenreController(SoccerGameRepository soccerGameRepository) { // <3>
        this.soccerGameRepository = soccerGameRepository;
    }

    @Get("/{id}") // <4>
    SoccerGame show(Long id) {
        return soccerGameRepository
                .findById(id)
                .orElse(null); // <5>
    }

    @Put // <6>
    HttpResponse<?> update(@Body @Valid GenreUpdateCommand command) { // <7>
        int numberOfEntitiesUpdated = soccerGameRepository.update(command.getId(), command.getName());

        return HttpResponse
                .noContent()
                .header(LOCATION, location(command.getId()).getPath()); // <8>
    }

    @Get(value = "/list{?args*}") // <9>
    List<SoccerGame> list(@Valid SortingAndOrderArguments args) {
        return soccerGameRepository.findAll(args);
    }

    @Post // <10>
    HttpResponse<SoccerGame> save(@Body @Valid GenreSaveCommand cmd) {
        SoccerGame soccerGame = soccerGameRepository.save(cmd.getName());

        return HttpResponse
                .created(soccerGame)
                .headers(headers -> headers.location(location(soccerGame.getId())));
    }

    private URI location(Long id) {
        return URI.create("/genres/" + id);
    }
}
