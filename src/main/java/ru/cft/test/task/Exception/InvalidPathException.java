package ru.cft.test.task.Exception;

public class InvalidPathException extends RuntimeException{
    public InvalidPathException(String msg) {
        super(msg);
    }
}
