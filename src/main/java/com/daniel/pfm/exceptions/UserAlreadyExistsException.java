package com.daniel.pfm.exceptions;

public class UserAlreadyExistsException extends RuntimeException{

    public UserAlreadyExistsException(String email){
        super("Usuário já cadastrado com o email: " + email);
    }

}
