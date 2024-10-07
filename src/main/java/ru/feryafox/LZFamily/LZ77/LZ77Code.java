package ru.feryafox.LZFamily.LZ77;

import ru.feryafox.LZFamily.Base.LZCode;

public final class LZ77Code implements LZCode {
    private int offset;
    private int length;
    private char discordLetter;

    public LZ77Code(int offset, int length, char discordLetter) {
        this.offset = offset;
        this.length = length;
        this.discordLetter = discordLetter;
    }

    public int getLength() {
        return length;
    }

    public char getDiscordLetter() {
        return discordLetter;
    }

    public int getOffset() {
        return offset;
    }
}

