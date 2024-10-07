package ru.feryafox.LZFamily.Base;

import java.util.ArrayList;

public interface LZBase {
    LZResult code(String s, int dictSize, int bufferSize);
    String decode(LZResult i);
}
