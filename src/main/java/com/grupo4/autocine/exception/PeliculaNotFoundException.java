package com.grupo4.autocine.exception;

public class PeliculaNotFoundException extends RuntimeException {
    public PeliculaNotFoundException(String message) {
        super(message);
    }
    
    public PeliculaNotFoundException(Long id) {
        super("No se encontró la película con ID: " + id);
    }
} 