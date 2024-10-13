package ru.feryafox.LZFamily.LZ77;

import ru.feryafox.LZFamily.Base.LZCode;

import java.util.List;

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

    public static String LZ77CodesArrayToString(List<LZCode> codes) {
        StringBuilder sb = new StringBuilder();
        for (LZCode code : codes) {
            LZ77Code code77 = (LZ77Code) code;
            sb.append(code77.getOffset());
            sb.append(',');
            sb.append(code77.getLength());
            sb.append(',');
            sb.append(code77.getDiscordLetter());
            sb.append(',');
        }

        return sb.substring(0, sb.length() - 1);
    }
}

