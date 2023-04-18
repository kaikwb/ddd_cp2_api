package br.com.fiap.ddd_cp2.swapi.exceptions;

public class PeoplePageError extends Exception {
    public PeoplePageError(String message) {
        super(message);
    }
}
