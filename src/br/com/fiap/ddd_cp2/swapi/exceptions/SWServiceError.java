package br.com.fiap.ddd_cp2.swapi.exceptions;

public class SWServiceError extends Exception {
    public SWServiceError(String message) {
        super(message);
    }
}
