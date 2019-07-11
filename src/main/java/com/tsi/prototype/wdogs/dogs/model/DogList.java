package com.tsi.prototype.wdogs.dogs.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class DogList extends DogsObject {

    private String pageOf;
    private String next;
    private String previous;
    private String first;
    private String last;
    private List<Dog> contents = new ArrayList<>();

    public DogList() {
        setKind("Page");
    }

    public String getPageOf() {
        return pageOf;
    }

    public void setPageOf(String pageOf) {
        this.pageOf = pageOf;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public List<Dog> getContents() {
        return contents;
    }

    public void setContents(List<Dog> contents) {
        this.contents = contents;
    }
}
