package ru.feryafox;


import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import ru.feryafox.FFZip.FFZip;

public class Main {

    @Option(name = "-c", aliases = {"--compress"}, usage = "Режим сжатия", forbids = {"-d"}, depends = {"-bs", "-ds"})
    private boolean compressMode = false;

    @Option(name = "-d", aliases = {"--decompress"}, usage = "Режим распаковки", forbids = {"-c"})
    private boolean decompressMode = false;

    @Option(name = "-bs", aliases = {"--buffer-size"}, usage = "Размер буфера (в байтах)", required = true)
    private int bufferSize;

    @Option(name = "-ds", aliases = {"--dict-size"}, usage = "Размер словаря (в байтах)", required = true)
    private int dictSize;

    @Argument(required = true, usage = "Входной файл", metaVar = "входной_файл", index = 0)
    private String inputFile;

    @Argument(required = true, usage = "Выходной файл", metaVar = "выходной_файл", index = 1)
    private String outputFile;

    public static void main(String[] args) {
        FFZip ffZip = new FFZip();
        CmdLineParser parser = new CmdLineParser(ffZip);

        try {
            parser.parseArgument(args);

            if (!ffZip.compressMode && !ffZip.decompressMode) {
                throw new CmdLineException(parser, "Необходимо указать режим: -c или -d");
            }

            if (ffZip.compressMode) {
                ffZip.compress(ffZip.inputFile, ffZip.outputFile, ffZip.dictSize, ffZip.bufferSize);
            } else {
                ffZip.decompress(ffZip.inputFile, ffZip.outputFile);
            }
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
        }
    }

    private void compressFile() {
        // Реализация сжатия файла
        System.out.println("Сжатие файла: " + fileName);
        System.out.println("Размер буфера: " + bufferSize + " байт");
        System.out.println("Размер словаря: " + dictSize + " байт");
        // Добавьте вашу логику сжатия здесь
    }

    private void decompressFile() {
        // Реализация распаковки файла
        System.out.println("Распаковка файла: " + fileName);
        // Добавьте вашу логику распаковки здесь
    }

//    public static void main(String[] args) {
//        FFZip zip = new FFZip();
//
//
//
//        zip.compress("input.txt", "outputSaw.txt", 1024, 128);
//        zip.decompress("outputSaw.txt", "decode.txt");
//    }
}

