package com.example.soccer;

import io.micronaut.serde.annotation.Serdeable;

import javax.validation.constraints.NotBlank;

@Serdeable
public class PlayerSaveCommand {

    private Long soccerGameId;

    @NotBlank
    private String name;

    private int age;

    public PlayerSaveCommand(Long soccerGameId, String name, int age) {
        this.soccerGameId = soccerGameId;
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Long getSoccerGameId() {return soccerGameId;}

    public void setSoccerGameId(Long soccerGameId) {this.soccerGameId = soccerGameId;}
}
