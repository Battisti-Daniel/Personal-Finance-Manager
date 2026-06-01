package com.daniel.pfm.exceptions;

public class RefreshTokenExpiredException extends RuntimeException{

    public RefreshTokenExpiredException(){
        super("Refresh token expirado, faça o login novamente");
    }

}
