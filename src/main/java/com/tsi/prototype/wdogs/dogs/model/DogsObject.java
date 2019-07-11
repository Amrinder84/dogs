package com.tsi.prototype.wdogs.dogs.model;

import javax.persistence.Transient;

public abstract class DogsObject {

    @Transient
    private String self;
    @Transient
    private String kind;

    public String getSelf() {
        return self;
    }

    public void setSelf(String self) {
        this.self = self;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }
}
