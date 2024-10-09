package ru.feryafox.LZFamily.LZ77;

import ru.feryafox.LZFamily.Base.*;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;


public final class LZ77 implements LZBase {

    @Override
    public LZResult code(String s, int dictSize, int bufferSize) {
        ArrayList<LZCode> result = new ArrayList<>();

        ArrayList<Character> dict = new ArrayList<>();
        Queue<Character> buffer = new ArrayDeque<>();

        int c = 0;
        while (c < bufferSize && c < s.length()) {
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
}
