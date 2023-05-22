package com.example.soccer.domain;

import io.micronaut.serde.annotation.Serdeable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import static javax.persistence.GenerationType.AUTO;

@Serdeable
@Entity
public class Player implements Comparable<Player> {

    @Id
    @GeneratedValue(strategy = AUTO)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "age", nullable = false)
    private int age;

    public Player() {}

    public Player(@NotNull String name,
                  @NotNull int age) {
        this.name = name;
        this.age = age;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void setAge(String isbn) {
        this.age = age;
    }

    @Override
    public int compareTo(Player o) {
        return 0;
    }

    @Override
    public String toString() {
        return this.name + " - Age: " + this.age;
    }
}
