package ru.feryafox.FFZip;

public class FileAreDamaged extends FFZipException{
    public FileAreDamaged(String fileName) {
        super("Файл поврежден " + fileName + " или не поддержуется");
    }
}
