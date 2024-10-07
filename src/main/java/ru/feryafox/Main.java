package ru.feryafox;


import ru.feryafox.FFZip.FFZip;

public class Main {
    public static void main(String[] args) {
        FFZip zip = new FFZip();
//        zip.compress("inputSaw.txt", "outputSaw.txt", 1024, 256);
        zip.compress("input.txt", "output.txt", 9, 4);
        zip.decompress("output.txt", "input.txt");
    }
}

