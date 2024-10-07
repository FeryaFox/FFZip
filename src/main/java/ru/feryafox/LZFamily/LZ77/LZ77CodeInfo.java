package ru.feryafox.LZFamily.LZ77;

import ru.feryafox.LZFamily.Base.LZCodeInfo;

public class LZ77CodeInfo implements LZCodeInfo {
    private final int dictSize;
    private final int buffSize;

    public LZ77CodeInfo(int dictSize, int buffSize) {
        this.dictSize = dictSize;
        this.buffSize = buffSize;
    }

    public int getDictSize() {
        return dictSize;
    }

    public int getBuffSize() {
        return buffSize;
    }
}
