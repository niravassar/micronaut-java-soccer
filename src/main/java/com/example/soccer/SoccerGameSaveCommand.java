package com.example.soccer;

import io.micronaut.serde.annotation.Serdeable;

import javax.validation.constraints.NotBlank;

@Serdeable
public class SoccerGameSaveCommand {

    @NotBlank
    private String name;

    public SoccerGameSaveCommand(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
