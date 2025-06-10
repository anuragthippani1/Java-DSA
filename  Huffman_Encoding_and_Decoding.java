import java.io.*;
import java.util.*;

class HuffmanNode {
    char c;
    int freq;
    HuffmanNode left, right;

    HuffmanNode(char c, int freq) {
        this.c = c;
        this.freq = freq;
    }
}

class HuffmanCoding {
    Map<Character, String> codes = new HashMap<>();
    Map<String, Character> reverseCodes = new HashMap<>();
    HuffmanNode root;

    public void buildTree(String text) {
        Map<Character, Integer> freqMap = new HashMap<>();
        for (char c : text.toCharArray())
            freqMap.put(c, freqMap.getOrDefault(c, 0) + 1);

        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>(Comparator.comparingInt(n -> n.freq));
        for (var entry : freqMap.entrySet())
            pq.add(new HuffmanNode(entry.getKey(), entry.getValue()));

        while (pq.size() > 1) {
            HuffmanNode a = pq.poll(), b = pq.poll();
            HuffmanNode merged = new HuffmanNode('\0', a.freq + b.freq);
            merged.left = a;
            merged.right = b;
            pq.add(merged);
        }

        root = pq.poll();
        generateCodes(root, "");
    }

    private void generateCodes(HuffmanNode node, String code) {
        if (node == null) return;

        if (node.left == null && node.right == null) {
            codes.put(node.c, code);
            reverseCodes.put(code, node.c);
        }

        generateCodes(node.left, code + "0");
        generateCodes(node.right, code + "1");
    }

    public String encode(String text) {
        StringBuilder encoded = new StringBuilder();
        for (char c : text.toCharArray())
            encoded.append(codes.get(c));
        return encoded.toString();
    }

    public String decode(String binaryStr) {
        StringBuilder decoded = new StringBuilder();
        HuffmanNode current = root;
        for (char bit : binaryStr.toCharArray()) {
            current = (bit == '0') ? current.left : current.right;

            if (current.left == null && current.right == null) {
                decoded.append(current.c);
                current = root;
            }
        }
        return decoded.toString();
    }

    public static void writeToFile(String data, String fileName) throws IOException {
        FileWriter fw = new FileWriter(fileName);
        fw.write(data);
        fw.close();
    }

    public static String readFromFile(String fileName) throws IOException {
        return new String(Files.readAllBytes(new File(fileName).toPath()));
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the file to compress: ");
        String fileName = scanner.nextLine();

        String content = readFromFile(fileName);

        HuffmanCoding hc = new HuffmanCoding();
        hc.buildTree(content);

        String encoded = hc.encode(content);
        System.out.println("Original size (bits): " + content.length() * 8);
        System.out.println("Compressed size (bits): " + encoded.length());
        System.out.println("Compression Ratio: " + (float)(content.length() * 8) / encoded.length());

        writeToFile(encoded, "compressed.txt");

        String decoded = hc.decode(encoded);
        writeToFile(decoded, "uncompressed.txt");

        System.out.println("Compression and Decompression Completed.");
    }
}
