package ru.cft.test.task.Exception;

public class InvalidPrefixException extends RuntimeException{
    public InvalidPrefixException(String msg) {
        super(msg);
    }
}