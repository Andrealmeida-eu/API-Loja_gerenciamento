package com.studioAlanGodoy.Loja.infrastructure.exceptions;

public class HorarioIndisponivelException extends RuntimeException {
    public HorarioIndisponivelException(String message) {
        super(message);
    }
}
