package ru.feryafox;


import ru.feryafox.FFZip.FFZip;

public class Main {
    public static void main(String[] args) {
        FFZip zip = new FFZip();


//        zip.compress("inputSaw.txt", "outputSaw.txt", 10000, 512);
//        zip.compress("input.txt", "output.txt", 9, 4);
        zip.decompress("pom.xml", "pom.txt");
//        zip.decompress("output.txt", "inputK.txt");
    }
}

