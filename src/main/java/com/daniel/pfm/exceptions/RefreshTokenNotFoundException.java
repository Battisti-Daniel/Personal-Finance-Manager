package com.daniel.pfm.exceptions;

public class RefreshTokenNotFoundException extends RuntimeException{

    public RefreshTokenNotFoundException(){
        super("Refresh token não encontrado");
    }

}
