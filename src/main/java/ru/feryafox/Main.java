package ru.feryafox;


import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import ru.feryafox.FFZip.FFZip;
import ru.feryafox.FFZip.FileAreDamaged;
import ru.feryafox.FFZip.InvalidParams;

public class Main {

    @Option(name = "-c", aliases = {"--compress"}, usage = "Режим сжатия", forbids = {"-d"}, depends = {"-bs", "-ds"})
    private boolean compressMode = false;

    @Option(name = "-d", aliases = {"--decompress"}, usage = "Режим распаковки", forbids = {"-c"})
    private boolean decompressMode = false;

    @Option(name = "-bs", aliases = {"--buffer-size"}, usage = "Размер буфера (в байтах)")
    private int bufferSize;

    @Option(name = "-ds", aliases = {"--dict-size"}, usage = "Размер словаря (в байтах)")
    private int dictSize;

    @Argument(required = true, usage = "Входной файл", metaVar = "входной_файл", index = 0)
    private String inputFile;

    @Argument(required = true, usage = "Выходной файл", metaVar = "выходной_файл", index = 1)
    private String outputFile;

    public static void main(String[] args) throws CmdLineException {
        FFZip ffZip = new FFZip();
        Main params = new Main();
        CmdLineParser parser = new CmdLineParser(params);
        parser.parseArgument(args);

        if (params.compressMode) {
            try {
                ffZip.compress(params.inputFile, params.outputFile, params.dictSize, params.bufferSize);
            }
            catch (InvalidParams e ) {
                System.out.println(e.getMessage());
            }
        }
        else {
            try {
                ffZip.decompress(params.inputFile, params.outputFile);
            }
            catch (FileAreDamaged e) {
                System.out.println(e.getMessage());
            }
        }
    }


}

