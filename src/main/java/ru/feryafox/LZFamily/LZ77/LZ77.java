package ru.feryafox.LZFamily.LZ77;

import ru.feryafox.LZFamily.Base.*;

import java.util.*;

public final class LZ77 implements LZBase {

    @Override
    public LZResult code(String s, int dictSize, int bufferSize) {
        ArrayList<LZCode> result = new ArrayList<>();

        ArrayList<Character> dict = new ArrayList<>();
        ArrayList<Character> buffer = new ArrayList<>();
        ArrayList<Character> dictBuffer = new ArrayList<>();

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

        while (!buffer.isEmpty()) {
            char cur = buffer.removeFirst();
            dictBuffer.add(cur);

            if (buffer.isEmpty() || !dict.contains(cur)) {
                dict.add(cur);
                result.add(new LZ77Code(0, 0, cur));
            } else {
                int indexOfCur = dict.indexOf(cur);

                while (dictBuffer.size() < bufferSize - 1) {
                    int curIndex = indexOfCur + dictBuffer.size();
                    if (curIndex > dictSize - 1) {
                        break;
                    }
                    // вдруг кто-то это прочитает. Если ты вдруг спросишь, зачем эти странные переменные - отвечу: не знаю. Если просто в сравнение их запихнуть, то не всегда работает...
                    char q = dict.get(curIndex);
                    char qq = buffer.getFirst();
                    if (q == qq) {
                        dictBuffer.add(buffer.removeFirst());
                    } else {
                        break;
                    }
                }

                dictBuffer.add(buffer.removeFirst());
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

    public LZResult newCode(String s, int dictSize, int bufferSize) {
        ArrayList<LZCode> result = new ArrayList<>();

        ArrayList<Character> dict = new ArrayList<>();
        ArrayList<Character> buffer = new ArrayList<>();

        int c = 0;
        while (c < bufferSize && c < s.length()) {
            buffer.add(s.charAt(c));
            c++;
        }

        int ic = 0;
        while (ic < dictSize) {
            dict.add('\0');
            ic++;
        }

        while (!buffer.isEmpty()) {
            char cur = buffer.getFirst();
            int startIndex = dict.indexOf(cur);

            if (startIndex == -1) {
                dict.add(buffer.removeFirst());
                result.add(new LZ77Code(0, 0, cur));
            }
            else {
                int maxLength = 0;
                int maxLengthIndex = 0;
                int curLength = 0;

                for (int i = startIndex; i < dictSize; i++) {
                    for (int j = 0; j < buffer.size() && i + j < dictSize; j++) {
                        // вдруг кто-то это прочитает. Если ты вдруг спросишь, зачем эти странные переменные - отвечу: не знаю. Если просто в сравнение их запихнуть, то не всегда работает...
                        char q = dict.get(i + j);
                        char qq = buffer.get(j);
                        if (q == qq) {
                            curLength++;
                            if (curLength > maxLength) {
                                maxLength++;
                                maxLengthIndex = i;

                            }
                        }
                        else {
                            break;
                        }
                    }
                    curLength = 0;
                }
                if (maxLength == buffer.size()) {
                    maxLength--;
                }
                for (int i = 0; i < maxLength + 1; i++) {
                    dict.add(buffer.removeFirst());
                }

                result.add(new LZ77Code(maxLengthIndex, maxLength, dict.getLast()));
            }

            while (dict.size() > dictSize) {
                dict.removeFirst();  // Удаляем первый элемент
            }

            while (buffer.size() < bufferSize && c < s.length()) {
                buffer.add(s.charAt(c));
                c++;
            }
        }

        return new LZResult(new LZ77CodeInfo(dictSize, bufferSize), result);
    }

    @Override
    public String decode(LZResult coded) {
        StringBuilder result = new StringBuilder();
        int dictSize = ((LZ77CodeInfo) coded.codeInfo()).getDictSize();
        ArrayList<Character> dict = new ArrayList<>();

        int i = 0;
        while (i < dictSize) {
            dict.add('\0');
            i++;
        }

        for (LZCode c : coded.code()) {
            LZ77Code code = (LZ77Code) c;
            if (code.getLength() == 0 && code.getOffset() == 0) {
                result.append(code.getDiscordLetter());
            } else {
                for (int j = 0; j < code.getLength(); j++) {
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
