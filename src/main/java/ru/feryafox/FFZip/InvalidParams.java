package ru.feryafox.FFZip;

public class InvalidParams extends FFZipException {
    public InvalidParams() {
        super("Параметр некоректный.");
    }
}
