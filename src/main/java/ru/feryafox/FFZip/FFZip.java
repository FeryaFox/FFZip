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

    public void compress(String inputFile, String outputFile, int dictSize, int bufferSize) throws InvalidParams {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {

            if (dictSize > 60000 || bufferSize > 60000) {
                throw new InvalidParams();
            }

            StringBuilder fileContent = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                fileContent.append(line).append("\n");
            }

            HuffmanResult hr = Huffman.code(fileContent.toString());
            LZResult lzCodeInfo = lzBase.code(hr.codeString(), dictSize, bufferSize);

            writeToFile(outputFile, lzCodeInfo, hr);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InvalidParams e) {
          throw new InvalidParams();
        }
    }

    public void decompress(String inputFile, String outputFile) throws FileAreDamaged {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            List<LZCode> lzCodes = new ArrayList<>();

            int offset;
            int length;
            int discordLetter;
            while ((offset = reader.read()) != -1) {
                length = reader.read();
                discordLetter = reader.read();


                if (offset == 65535 && length == 65535 && discordLetter == 65535) {
                    break;
                }

                lzCodes.add(new LZ77Code(offset, length, convertUnicodeToChar((char) discordLetter)));

            }

            if (offset == -1) {
                throw new FileAreDamaged(inputFile);
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
                System.out.println("Файл успешно расжат в файл " + outputFile);
            } catch (IOException e) {
                System.err.println("Ошибка при записи в файл: " + e.getMessage());
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (FileAreDamaged e) {
            throw new FileAreDamaged(inputFile);
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

            System.out.println("Файл успешно сжат в " + outputFile);
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
