package ru.feryafox.FFZip;


import ru.feryafox.Huffman.Huffman;
import ru.feryafox.Huffman.HuffmanResult;
import ru.feryafox.LZFamily.Base.LZBase;
import ru.feryafox.LZFamily.Base.LZCode;
import ru.feryafox.LZFamily.Base.LZResult;
import ru.feryafox.LZFamily.LZ77.LZ77;
import ru.feryafox.LZFamily.LZ77.LZ77Code;
import ru.feryafox.LZFamily.LZ77.LZ77CodeInfo;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FFZip {
    private final LZBase lzBase = new LZ77();

    public void compress(String inputFile, String outputFile, int dictSize, int bufferSize) {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            StringBuilder fileContent = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                fileContent.append(line).append("\n");
            }

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
            int discordLetterT;
            while ((offset = reader.read()) != -1) {
                length = reader.read();
                discordLetterT = reader.read();


                if (offset == 65535 || length == 65535 || discordLetterT == 65535) {
                    break;
                }

                lzCodes.add(new LZ77Code(offset, length, convertUnicodeToChar((char) discordLetterT)));

            }

            if (offset == -1) {
                System.out.println("Файл слишком поврежден или имеет неверный формат");
            }

            while ((offset = reader.read()) == 65535) {}

            int dictSize = offset;
            int bufferSize = reader.read();

            Map<Character, Integer> frequencies = new HashMap<>();

            while ((offset = reader.read()) != -1) {
                Character c = (char) offset;
                frequencies.put(c, reader.read());
            }

            String lzResult = lzBase.decode(new LZResult(new LZ77CodeInfo(dictSize, bufferSize), lzCodes));
            String decodedString = Huffman.decode(new HuffmanResult(frequencies, lzResult));

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
                writer.write(decodedString);
            } catch (IOException e) {
                System.err.println("Ошибка при записи в файл: " + e.getMessage());
            }
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

            for (int i = 0; i < 9; i++) {
                writer.write(Integer.MAX_VALUE);
            }
            writer.write(((LZ77CodeInfo) lzResult.codeInfo()).getDictSize());
            writer.write(((LZ77CodeInfo) lzResult.codeInfo()).getBuffSize());

            for (Character c : huffmanResult.frequencies().keySet()) {
                writer.write(c);
                writer.write(huffmanResult.frequencies().get(c));
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


    // моя любимая рубрика: костыли.... кто видит это... простите... не хотел...
    // ПРОШУ, НЕ СМОТРИТЕ... ПРОШУ... УМОЛЯЮ... БЕРЕГИТЕ ПСИХИКУ СВОЮ...
    private byte convertCharToByte(char c) {
        if (c == '0') return 0;
        return 1;
    }

    private char convertUnicodeToChar(char c) {
        if (c == '\u0000') return '0';
        else return '1';
    }
}
