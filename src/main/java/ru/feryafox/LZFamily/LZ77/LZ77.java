package ru.feryafox.LZFamily.LZ77;

import ru.feryafox.LZFamily.Base.*;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;


public final class LZ77 implements LZBase {

    @FunctionalInterface
    private interface lzSteps {
        void makeStep(ArrayList<LZCode> result, ArrayList<Character> dict, Queue<Character> buffer, ArrayList<Character> dictBuffer, int dictSize, int bufferSize, char cur);
    }

    @Override
    public LZResult code(String s, int dictSize, int bufferSize) {

        return lzSteps(s, dictSize, bufferSize, lz77StepCollection::lzStepWithoutOptimization);
    }


    private LZResult lzSteps(String s, int dictSize, int bufferSize, lzSteps lzSteps_) {
        ArrayList<LZCode> result = new ArrayList<>();

        ArrayList<Character> dict = new ArrayList<>();
        Queue<Character> buffer = new ArrayDeque<>();

        ArrayList<Character> dictBuffer = new ArrayList<>();





        int c = fillDictAndBuffer(dict, buffer, dictSize, bufferSize, s);

        while (!buffer.isEmpty()) {

            char cur = buffer.remove();
            dictBuffer.add(cur);


            if (buffer.isEmpty() || !dict.contains(cur)) {
                dict.add(cur);
                result.add(new LZ77Code(0, 0, cur));
            }
            else {
                if (result.size() % 10000 == 0) {
                    System.out.println(result.size());
                }
//                System.out.println("-".repeat(10));
//                System.out.println("Start: ");
//                System.out.println(result.size());
//                System.out.println(dict);
//                System.out.println(buffer);
                lzSteps_.makeStep(result, dict, buffer, dictBuffer, dictSize, bufferSize, cur);
//                System.out.println("-".repeat(10));
//                System.out.println("End:");
//                System.out.println(dict);
//                System.out.println(buffer);
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

    public LZResult code(String s, int dictSize, int bufferSize, boolean optimizationEnabled) {
        if (!optimizationEnabled) {
            return lzSteps(s, dictSize, bufferSize, lz77StepCollection::lzStepWithoutOptimization);
        }
        return lzSteps(s, dictSize, bufferSize, lz77StepCollection::lzStepWithOptimization);
    }


    private int fillDictAndBuffer(ArrayList<Character> dict, Queue<Character> buffer, int dictSize, int bufferSize, String s) {
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

        return c;
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

    static class lz77StepCollection {
        public static void lzStepWithoutOptimization(ArrayList<LZCode> result, ArrayList<Character> dict, Queue<Character> buffer, ArrayList<Character> dictBuffer, int dictSize, int bufferSize, char cur) {
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

        public static void lzStepWithOptimization(ArrayList<LZCode> result, ArrayList<Character> dict, Queue<Character> buffer, ArrayList<Character> dictBuffer, int dictSize, int bufferSize, char cur) {
            int indexOfCur = dict.indexOf(cur);
            int maxLength = 1;
            int bestIndex = indexOfCur;
            char r;

            for (int i = indexOfCur; i < dictSize - 1; i++) {
                if (cur == dict.get(i)) {

                    Queue<Character> tempBuffer = new ArrayDeque<>(buffer);
                    ArrayList<Character> dictBufferTemp = new ArrayList<>(dictBuffer);

                    while (dictBufferTemp.size() < bufferSize - 1) {
                        int curIndex = i + dictBufferTemp.size();
                        if (curIndex > dictSize - 1) {
                            break;
                        }
                        char q = dict.get(curIndex);
                        char qq = tempBuffer.element();
                        if (q == qq) {
                            dictBufferTemp.add(tempBuffer.poll());
                        }
                        else {
                            if (dictBufferTemp.size() > maxLength) {
//                                System.out.println(dictBufferTemp.size());
                                maxLength = dictBufferTemp.size();
                                bestIndex = i;
                            }
                            break;
                        }
                    }
                    if (tempBuffer.size() == 1) {
                        maxLength = dictBufferTemp.size();
                        bestIndex = i;
                    }
                }
            }

            dict.addAll(dictBuffer);
            for (int i = 0; i < maxLength; i++) {
                dict.add(buffer.poll());
            }

//            dictBuffer.add(buffer.poll());

            result.add(new LZ77Code(bestIndex, maxLength, dict.getLast()));
        }


        public static void lzStepWithOptimizationOLD(ArrayList<LZCode> result, ArrayList<Character> dict, Queue<Character> buffer, ArrayList<Character> dictBuffer, int dictSize, int bufferSize, char cur) {
            int bestOffset = -1;
            int bestLength = 0;

            // Ищем самое длинное совпадение в словаре
            for (int i = 0; i < dict.size(); i++) {
                // Проверяем, совпадает ли текущий символ словаря с искомым символом
                if (dict.get(i) == cur) {
                    int length = 1;
                    int dictIndex = i + 1;

                    if (dictIndex >= dict.size()) {
                        dictIndex = i;
                    }

                    // Создаем временную копию буфера для поиска совпадений
                    Queue<Character> tempBuffer = new ArrayDeque<>(buffer);
//                    tempBuffer.poll(); // Удаляем первый элемент, так как он уже соответствует cur

                    // Ищем максимальное совпадение, начиная с текущей позиции в словаре

                    char q = tempBuffer.peek();
                    char qq = dict.get(dictIndex);

                    while (dictIndex < dict.size() && !tempBuffer.isEmpty() &&
                            qq == q &&
                            length < bufferSize) {
                        length++;
                        dictIndex++;
                        tempBuffer.poll();
                    }

                    // Обновляем лучшее совпадение, если текущее длиннее
                    if (length > bestLength) {
                        bestLength = length;
                        bestOffset = i;
                    }
                }
            }

            // Обновляем dictBuffer, копируя символы из найденного совпадения
            for (int i = 1; i < bestLength; i++) {
                dictBuffer.add(buffer.poll());
            }

            // Добавляем следующий несовпадающий символ (если буфер не пуст)
            if (!buffer.isEmpty()) {
                dictBuffer.add(buffer.poll());
            } else {
                bestLength -= 1;
            }


            // Добавляем новые символы в словарь
            dict.addAll(dictBuffer);

            // Создаем и добавляем новый LZ77 код в результат
            result.add(new LZ77Code(bestOffset, bestLength, dictBuffer.getLast()));
        }
    }

    public static void main(String[] args) {
        LZ77 lz77 = new LZ77();
//        LZResult qq = lz77.code("красная краска",12,4);
        LZResult qq = lz77.code("1112111112323212123333333",12,4);




        System.out.println(LZ77Code.LZ77CodesArrayToString(qq.code()));
        System.out.println(lz77.decode(qq));

        LZResult q = lz77.code("1112111112323212123333333",12,4, true);
//        LZResult q = lz77.code("красная краска",12,4, true);

        System.out.println(LZ77Code.LZ77CodesArrayToString(q.code()));
        System.out.println(lz77.decode(q));

    }
}
