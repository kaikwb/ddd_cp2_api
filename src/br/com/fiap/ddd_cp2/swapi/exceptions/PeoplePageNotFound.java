package br.com.fiap.ddd_cp2.swapi.exceptions;

public class PeoplePageNotFound extends Exception {
    public PeoplePageNotFound(String url) {
        super(String.format("Not found people page with URL %s", url));
    }
}
