package com.tsi.prototype.wdogs.dogs.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

/**
 * Represents JPA Entity for DOG.
 */
@Entity
@Table(name="dogs")
public class Dog extends DogsObject {

    public Dog() {
        setKind("Dog");
    }

    @Id
    @Column(name="uuid")
    private String uuid;

    @NotNull
    @Column(name="name")
    private String dogName;

    @NotNull
    @Column(name="rasse")
    private String dogRace;

    @NotNull
    @Column(name="besitzer")
    private String owner;


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDogName() {
        return dogName;
    }

    public void setDogName(String dogName) {
        this.dogName = dogName;
    }

    public String getDogRace() {
        return dogRace;
    }

    public void setDogRace(String dogRace) {
        this.dogRace = dogRace;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

}
