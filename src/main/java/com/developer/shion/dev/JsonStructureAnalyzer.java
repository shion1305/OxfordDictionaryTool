package com.developer.shion.dev;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

public class JsonStructureAnalyzer {
    StringBuilder out;
    JsonNode node;

    public static void main(String[] args) throws JsonProcessingException {
        Scanner scanner = new Scanner(System.in);
        StringBuilder in = new StringBuilder();
        while (scanner.hasNextLine()) {
            String next = scanner.nextLine();
            if (next.equals("END")) break;
            in.append(next);
        }
        String target = in.toString();
        JsonStructureAnalyzer js = new JsonStructureAnalyzer(target);
        String result = js.run();
        System.out.println(result);
    }

    /**
     * This class analyzes the structure of Json File and let you easily understand its structure.
     *
     * @param target :target Json code.
     * @throws JsonProcessingException
     * @output the result of analysis
     */
    public JsonStructureAnalyzer(String target) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        node = mapper.readTree(target);
    }

    public String run() {
        out = new StringBuilder();
        if (node.size() == 0) {
            out.append("EMPTY");
        } else {
            go(node, 0);
        }
        return out.toString();
    }

    private void go(JsonNode node, int level) {
        for (Iterator<Map.Entry<String, JsonNode>> it = node.fields(); it.hasNext(); ) {
            Map.Entry<String, JsonNode> entry = it.next();
            switch (entry.getValue().getNodeType()) {
                case OBJECT:
                    print(level, entry.getKey(), null, false);
                    go(entry.getValue(), level + 1);
                case NULL:
                    print(level, entry.getKey(), "NULL", false);
                    break;
                case POJO:
                    print(level, entry.getKey(), "POJO", false);
                    break;
                case NUMBER:
                    print(level, entry.getKey(), "NUMBER", false);
                    break;
                case BOOLEAN:
                    print(level, entry.getKey(), "BOOLEAN", false);
                    break;
                case ARRAY:
                        switch (entry.getValue().get(0).getNodeType()) {
                            case OBJECT:
                            case ARRAY:
                                print(level, entry.getKey(), null, true);
                                break;
                            default:
                                print(level, entry.getKey(), entry.getValue().get(0).getNodeType().name(), true);
                        }
                        if (entry.getValue().size() != 0) {
                            for (int i=0;i<entry.getValue().size();i++) {
                                go(entry.getValue().get(i), level + 1);
                            }
                        }
                    break;
                case STRING:
                    print(level, entry.getKey(), "STRING", false);
                    break;
                case MISSING:
                    print(level, entry.getKey(), "MISSING", false);
                    break;
                case BINARY:
                    print(level, entry.getKey(), "BINARY", false);
                    break;
            }
        }
    }

    private void print(int level, String name, String valueType, boolean isArray) {
        for (int i = 0; i < level; i++) {
            out.append("    ");
        }
        out.append("- ");
        out.append(name);
        out.append(isArray ? " [" : ((valueType != null) ? " :" : ""));
        if (valueType != null) out.append(valueType);
        if (isArray) out.append("]");
        out.append("\n");
    }
}
