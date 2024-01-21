package ru.cft.test.task.Exception;

public class InvalidArgsException extends RuntimeException{
    public InvalidArgsException(String msg) {
        super(msg);
    }
}