package ru.feryafox.Huffman;

import java.util.Map;

public record HuffmanResult(Map<Character, Integer> frequencies, String codeString) { }
