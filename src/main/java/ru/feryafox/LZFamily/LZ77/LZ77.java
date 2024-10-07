package ru.feryafox.LZFamily.LZ77;

import ru.feryafox.Huffman.Huffman;
import ru.feryafox.LZFamily.Base.*;

import java.io.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;


public final class LZ77 implements LZBase {

    @Override
    public LZResult code(String s, int dictSize, int bufferSize) {
        ArrayList<LZCode> result = new ArrayList<>();

        ArrayList<Character> dict = new ArrayList<>();
        Queue<Character> buffer = new ArrayDeque<>();

        int c = 0;
        while (c < bufferSize) {
            buffer.add(s.charAt(c));
            c++;
        }

        int i = 0;
        while (i < dictSize) {
            dict.add('\0');
            i++;
        }
        ArrayList<Character> dictBuffer = new ArrayList<>();


        while (!buffer.isEmpty()) {

            char cur = buffer.remove();
            dictBuffer.add(cur);

            if (buffer.isEmpty() || !dict.contains(cur)) {
                dict.add(cur);
                result.add(new LZ77Code(0, 0, cur));
            }
            else {
                int indexOfCur = dict.indexOf(cur);

                while (dictBuffer.size() < bufferSize - 1) {
                    int curIndex = indexOfCur + dictBuffer.size();
                    if (curIndex > dictSize - 1) {
//                        dictBuffer.add(buffer.poll());
                        break;
                    }
                    char q = dict.get(curIndex);
                    char qq = buffer.element();
                    if (q == qq) {
                        dictBuffer.add(buffer.poll());
                    }
                    else {
                        break;
                    }

                }

                dictBuffer.add(buffer.poll());

                dict.addAll(dictBuffer);
                result.add(new LZ77Code(indexOfCur, dictBuffer.size() - 1, dictBuffer.getLast()));

            }

            while (dict.size() > dictSize) {
                dict.removeFirst();
            }

            while (buffer.size() < bufferSize && c < s.length()) {
                buffer.add(s.charAt(c));
                c++;
            }

            if (!dictBuffer.isEmpty()) {
                dictBuffer.clear();
            }
        }


        System.out.println(buffer);
        System.out.println(dict);
        System.out.println(result.toString());

        return new LZResult(new LZ77CodeInfo(dictSize, bufferSize), result);
    }

    @Override
    public String decode(LZResult coded) {
        StringBuilder result = new StringBuilder();

        int dictSize = ((LZ77CodeInfo)coded.codeInfo()).getDictSize();

        ArrayList<Character> dict = new ArrayList<>();

        int i = 0;
        while (i < dictSize) {
            dict.add('\0');
            i++;
        }

        for (LZCode c : coded.code()) {
            LZ77Code code = (LZ77Code) c;
            if ((code.getLength() == 0 && code.getOffset() == 0)) {
                result.append(code.getDiscordLetter());
            }
            else {
                for (int j = 0 ; j < code.getLength(); j++) {
                    char cur = dict.get(code.getOffset() + j);
                    result.append(cur);
                    dict.add(cur);
                }

                result.append(code.getDiscordLetter());
            }

            dict.add(code.getDiscordLetter());
            while (dict.size() > dictSize) {
                dict.removeFirst();
            }
        }

        return result.toString();
    }


    public static void main(String[] args) throws IOException {

        LZ77 lz77 = new LZ77();

//        LZResult q = lz77.code("красная краска", 9, 4, LZ77::codeToString);
//        writeToFile(q, "test.txt");
//        System.out.println(readFromFile("test.txt"));
//        LZResult q;
//        try (BufferedReader reader = new BufferedReader(new FileReader("inputSaw.txt"))) {
//            StringBuilder fileContent = new StringBuilder();
//            String line;
//            while ((line = reader.readLine()) != null) {
//                fileContent.append(line).append("\n");
//            }
////            String qq = Huffman.code(fileContent.toString()).codeString();
//             q = lz77.code(fileContent.toString(), 2048, 128);
//            System.out.println(q);
//            try (FileWriter fileWriter = new FileWriter("outputW.txt")) {
//                fileWriter.write(String.valueOf(q.code()));
//            } catch (IOException e) {
//                System.err.println("Ошибка при записи в файл: " + e.getMessage());
//            }
//        }

     LZResult q = lz77.code("красная краска", 9, 4);
        System.out.println(lz77.decode(q));


    }

//    public static void writeToFile(LZResult result, String filePath) throws IOException {
//        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(filePath))) {
//            // Записываем информацию о размерах словаря и буфера
//            LZ77CodeInfo info = (LZ77CodeInfo) result.codeInfo();
//            dos.writeInt(info.getDictSize());
//            dos.writeInt(info.getBuffSize());
//
//            // Парсим и записываем коды
//            String[] codes = result.code().split(",");
//            dos.writeInt(codes.length / 3); // количество кодов
//
//            for (int i = 0; i < codes.length; i += 3) {
//                dos.writeInt(Integer.parseInt(codes[i]));     // offset
//                dos.writeInt(Integer.parseInt(codes[i + 1])); // length
//                dos.writeChar(codes[i + 2].charAt(0));        // discord letter
//            }
//        }
//    }
//
//    public static LZResult readFromFile(String filePath) throws IOException {
//        try (DataInputStream dis = new DataInputStream(new FileInputStream(filePath))) {
//            // Читаем информацию о размерах
//            int dictSize = dis.readInt();
//            int bufferSize = dis.readInt();
//            LZ77CodeInfo info = new LZ77CodeInfo(dictSize, bufferSize);
//
//            // Читаем коды
//            int codesCount = dis.readInt();
//            StringBuilder result = new StringBuilder();
//
//            for (int i = 0; i < codesCount; i++) {
//                int offset = dis.readInt();
//                int length = dis.readInt();
//                char discordLetter = dis.readChar();
//
//                result.append(offset).append(",")
//                        .append(length).append(",")
//                        .append(discordLetter).append(",");
//            }
//
//            return new LZResult(info, result.toString());
//        }
//    }
}
