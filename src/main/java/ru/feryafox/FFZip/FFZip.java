package ru.feryafox.FFZip;


import ru.feryafox.Huffman.Huffman;
import ru.feryafox.Huffman.HuffmanResult;
import ru.feryafox.LZFamily.Base.LZBase;
import ru.feryafox.LZFamily.Base.LZCode;
import ru.feryafox.LZFamily.Base.LZCodeInfo;
import ru.feryafox.LZFamily.Base.LZResult;
import ru.feryafox.LZFamily.LZ77.LZ77;
import ru.feryafox.LZFamily.LZ77.LZ77Code;
import ru.feryafox.LZFamily.LZ77.LZ77CodeInfo;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FFZip {
    private final LZBase lzBase = new LZ77();

    public void compress(String inputFile, String outputFile, int dictSize, int bufferSize) {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            StringBuilder fileContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                fileContent.append(line).append("\n");
            }

            fileContent.toString();

            HuffmanResult hr = Huffman.code(fileContent.toString());
            LZResult lzCodeInfo = lzBase.code(hr.codeString(), dictSize, bufferSize);

            writeToFile(outputFile, lzCodeInfo, hr);

//            try (FileWriter fileWriter = new FileWriter("outputW.txt")) {
//                fileWriter.write(String.valueOf(q.code()));
//            } catch (IOException e) {
//                System.err.println("Ошибка при записи в файл: " + e.getMessage());
//            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void decompress(String inputFile, String outputFile) {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            List<LZCode> lzCodes = new ArrayList<>();

            int offset;
            int length;
            char discordLetter;
            while ((offset = reader.read()) != -1) {
                length = reader.read();
                discordLetter = (char) reader.read();
                System.out.println(discordLetter);
                LZ77Code code = new LZ77Code(offset, length, discordLetter);
                lzCodes.add(code);
            }

            System.out.println(lzBase.decode(new LZResult(new LZ77CodeInfo(9, 8), lzCodes)));
//            LZResult lzResult = new LZResult(lzCodes);
//            String decodedString = lzBase.decode(lzResult);

//            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
//                writer.write(decodedString);
//            } catch (IOException e) {
//                System.err.println("Ошибка при записи в файл: " + e.getMessage());
//            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeToFile(String outputFile, LZResult lzResult, HuffmanResult huffmanResult) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            for (LZCode element: lzResult.code()) {
                LZ77Code lz77Code = (LZ77Code) element;
                writer.write(lz77Code.getOffset());
                writer.write(lz77Code.getLength());
                writer.write(convertCharToByte(lz77Code.getDiscordLetter()));
            }
            System.out.println("Данные успешно записаны в файл.");
        } catch (IOException e) {
            System.err.println("Ошибка при записи в файл: " + e.getMessage());
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("tt.txt"))) {
            for (LZCode element: lzResult.code()) {
                LZ77Code lz77Code = (LZ77Code) element;
                writer.write(String.valueOf(lz77Code.getOffset()));
                writer.write(String.valueOf(lz77Code.getLength()));
                writer.write(lz77Code.getDiscordLetter());
            }
            System.out.println("Данные успешно записаны в файл.");
        } catch (IOException e) {
            System.err.println("Ошибка при записи в файл: " + e.getMessage());
        }
    }

    private byte convertCharToByte(char c) {
        if (c == '0') return 0;
        return 1;
    }
}
