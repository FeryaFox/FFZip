package ru.feryafox.LZFamily.Base;

@FunctionalInterface
public interface LZStringToCode {
    LZCode toCode(String code);
}
