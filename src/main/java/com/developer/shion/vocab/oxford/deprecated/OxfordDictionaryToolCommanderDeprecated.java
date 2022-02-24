package com.developer.shion.vocab.oxford.deprecated;

/**
 * This class is created on 06/2020.
 * Deprecated on 11/2021.
 */

import com.developer.shion.vocab.oxford.OxfordApiData;
import com.developer.shion.vocab.oxford.OxfordDictionaryDataManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class OxfordDictionaryToolCommanderDeprecated {
    static String previous;

    public static void main(String[] args) throws IOException {
        OxfordDictionaryDataManager tool = new OxfordDictionaryDataManager();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            try {
                String keyword = scanner.nextLine().toLowerCase();
                if (keyword.length() != 0) {
                    if (keyword.charAt(0) != '$') {
                        OxfordApiData data = tool.search(keyword);
                        if (data != null) {
                            previous = data.getKeyword();
                            ObjectMapper mapper = new ObjectMapper();
                            JsonNode root = mapper.readTree(data.getResponse());
                            JsonNode results = root.get("results");
                            System.out.println("SEARCH FOR: " + keyword);
                            for (int r = 0; r < results.size(); r++) {
                                JsonNode lexicalEntries = results.get(r).get("lexicalEntries");
                                for (int le = 0; le < lexicalEntries.size(); le++) {
                                    JsonNode entries = lexicalEntries.get(le).get("entries");
                                    for (int i = 0; i < entries.size(); i++) {
                                        JsonNode senses = entries.get(i).get("senses");
                                        for (int i1 = 0; i1 < senses.size(); i1++) {
                                            String head = " " + (r + 1) + "." + (le + 1) + "." + (i1 + 1) + "  ";
                                            JsonNode definitions = senses.get(i1).get("definitions");
                                            if (definitions != null) {
                                                System.out.print(head);
                                                for (int i2 = 0; i2 < definitions.size(); i2++) {
                                                    if (i2 != 0) {
                                                        System.out.print("        ");
                                                    }
                                                    System.out.print("§ ");
                                                    System.out.println(definitions.get(i2).textValue());
                                                }
                                            }
                                            JsonNode examples = senses.get(i1).get("examples");
                                            if (examples != null) {
                                                for (int ex = 0; ex < examples.size(); ex++) {
                                                    System.out.print("           --");
                                                    System.out.println(examples.get(ex).get("text").textValue());
                                                }
                                            }
                                        }
                                    }
                                    System.out.println();
                                }
                            }
                        } else {
                            System.out.println("NOT FOUND: " + keyword);
                        }
                    } else {
                        int space = keyword.indexOf(' ');
                        if (space == -1) {
                            if (keyword.equals("$audio")) {
                                playSound(tool.search(previous), args);
                            } else if (keyword.equals("$history")) {
                                boolean go = true;
                                ArrayList<String> his = tool.getHistory();
                                int current = 0;
                                while (go) {
                                    for (int i = current; i < current + 20 && i < his.size(); i++) {
                                        System.out.println(his.get(i));
                                    }
                                    current+=20;
                                    if (current<his.size()) {
                                        System.out.println("## Type sth to continue");
                                        System.out.println("## Type END to exit");
                                        if (scanner.nextLine().equals("END")){
                                            go=false;
                                        }
                                    }else {
                                        go=false;
                                    }
                                }

                            }
                        } else if (keyword.substring(0, space).equals("$audio")) {
                            playSound(tool.search(keyword.substring(space + 1)), args);
                        }
                    }
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                tool.init();
            }
        }
    }

    public static void playSound(OxfordApiData data, String... args) throws JsonProcessingException {
        System.out.println("playSound");
        String resp = data.getResponse();
        int index = 0;
        Set<String> urls = new HashSet<String>();
        int index2 = 0;
        while (index != -1) {
            System.out.println(index);
            index = resp.indexOf("\"audioFile\":", index2);
            int index1 = resp.indexOf("\"", index + 11);
            index2 = resp.indexOf("\"", index1 + 1);
            urls.add(resp.substring(index1 + 1, index2));
        }
        boolean isPlayed = false;
        System.out.println("URL EARNED " + urls.size());
        for (String url : urls) {
//            com.sun.javafx.application.PlatformImpl.startup()
//            try {
//                new SoundPlayer(new URL(url)).play(args);
//                isPlayed=true;
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
        if (!isPlayed) {
            System.out.println("Play Audio Failed");
        }
    }
}
