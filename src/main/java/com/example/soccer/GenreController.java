package com.example.soccer;

import com.example.soccer.domain.Soccer;
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

    private final GenreRepository genreRepository;

    GenreController(GenreRepository genreRepository) { // <3>
        this.genreRepository = genreRepository;
    }

    @Get("/{id}") // <4>
    Soccer show(Long id) {
        return genreRepository
                .findById(id)
                .orElse(null); // <5>
    }

    @Put // <6>
    HttpResponse<?> update(@Body @Valid GenreUpdateCommand command) { // <7>
        int numberOfEntitiesUpdated = genreRepository.update(command.getId(), command.getName());

        return HttpResponse
                .noContent()
                .header(LOCATION, location(command.getId()).getPath()); // <8>
    }

    @Get(value = "/list{?args*}") // <9>
    List<Soccer> list(@Valid SortingAndOrderArguments args) {
        return genreRepository.findAll(args);
    }

    @Post // <10>
    HttpResponse<Soccer> save(@Body @Valid GenreSaveCommand cmd) {
        Soccer soccer = genreRepository.save(cmd.getName());

        return HttpResponse
                .created(soccer)
                .headers(headers -> headers.location(location(soccer.getId())));
    }

    @Post("/ex") // <11>
    HttpResponse<Soccer> saveExceptions(@Body @Valid GenreSaveCommand cmd) {
        try {
            Soccer soccer = genreRepository.saveWithException(cmd.getName());
            return HttpResponse
                    .created(soccer)
                    .headers(headers -> headers.location(location(soccer.getId())));
        } catch(PersistenceException e) {
            return HttpResponse.noContent();
        }
    }

    @Delete("/{id}") // <12>
    HttpResponse<?> delete(Long id) {
        genreRepository.deleteById(id);
        return HttpResponse.noContent();
    }

    private URI location(Long id) {
        return URI.create("/genres/" + id);
    }
}