package br.com.fiap.ddd_cp2.swapi.exceptions;

public class PeopleNotFound extends Exception {
    public PeopleNotFound(String message) {
        super(message);
    }
}
