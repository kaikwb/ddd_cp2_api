package br.com.fiap.ddd_cp2.swapi.exceptions;

public class InvalidSearchString extends Exception {
    public InvalidSearchString(String searchString) {
        super(String.format("Invalid search string [%s] : Only letters, numbers, hyphens and spaces are allowed", searchString));
    }
}
