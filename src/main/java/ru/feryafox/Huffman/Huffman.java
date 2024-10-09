package ru.feryafox.Huffman;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class Huffman {
    private static class Node implements Comparable<Node> {
        char character; // символ, если это лист, иначе '\0'
        int frequency;
        Node left, right;

        Node(char character, int frequency, Node left, Node right) {
            this.character = character;
            this.frequency = frequency;
            this.left = left;
            this.right = right;
        }

        boolean isLeaf() {
            return left == null && right == null;
        }

        @Override
        public int compareTo(Node o) {
            return this.frequency - o.frequency;
        }
    }

    public static Map<Character, Integer> getFrequency(String inputString) {
        Map<Character, Integer> frequencies = new HashMap<>();
        for (char character : inputString.toCharArray()) {
            frequencies.put(character, frequencies.getOrDefault(character, 0) + 1);
        }
        return frequencies;
    }

    private static Node buildTree(Map<Character, Integer> frequencies) {
        PriorityQueue<Node> queue = new PriorityQueue<Node>();
        for (Map.Entry<Character, Integer> entry : frequencies.entrySet()) {
            queue.add(new Node(entry.getKey(), entry.getValue(), null, null));
        }

        while (queue.size() > 1) {
            Node left = queue.poll();
            Node right = queue.poll();
            Node parent = new Node('\0', left.frequency + right.frequency, left, right);
            queue.offer(parent);
        }

        return queue.poll();
    }

    private static void getCodes(Node node, String code, Map<Character, String> huffmanCode) {
        if (node.isLeaf()) {
            huffmanCode.put(node.character, code);
            return;
        }
        getCodes(node.left, code + "0", huffmanCode);
        getCodes(node.right, code + "1", huffmanCode);
    }

    public static HuffmanResult code(String s){
        Map<Character, Integer> frequencies = getFrequency(s);

        Node root = buildTree(frequencies);

        Map<Character, String> huffmanCode = new HashMap<>();
        getCodes(root, "", huffmanCode);

        StringBuilder code = new StringBuilder();

        for (char character : s.toCharArray()) {
            code.append(huffmanCode.get(character));
        }

        return new HuffmanResult(frequencies, code.toString());
    }

    public static String decode(HuffmanResult result) {
        Map<Character, Integer> frequencies = result.frequencies();
        String encodedString = result.codeString();

        Node root = buildTree(frequencies);
        StringBuilder decodedString = new StringBuilder();

        Node current = root;
        int i = 0;
        while (i < encodedString.length()) {
            if (encodedString.charAt(i) == '0') {
                current = current.left;
            } else {
                current = current.right;
            }

            if (current.isLeaf()) {
                decodedString.append(current.character);
                current = root;
            }

            i++;
        }

        return decodedString.toString();
    }
}
