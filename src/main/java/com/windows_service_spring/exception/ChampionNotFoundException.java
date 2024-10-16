package com.windows_service_spring.exception;

public class ChampionNotFoundException extends RuntimeException{
    public  ChampionNotFoundException(long id){
        super("Couldn't find champion with ID: " + id);
    }
}
