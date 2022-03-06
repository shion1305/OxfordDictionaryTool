package com.developer.shion.vocab.oxford;
/**
 * This class is created on 10/2021.
 * Completed on 11/2021.
 */

import com.developer.shion.vocab.oxford.data.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.logging.Logger;

class OxfordDictionaryResultFormatter {
    String data;
    StringBuilder out;
    Logger logger;

    OxfordDictionaryResultFormatter(String data) {
        this.data = data;
        logger = Logger.getLogger("OxfordDictionaryResultFormatter");
    }

    private Result[] read() throws JsonProcessingException {
        out = new StringBuilder();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(data);
        JsonNode results = root.get("results");
        out.append("Definition of: " + root.get("word").asText());
        Result[] data = new Result[results.size()];
        for (int i = 0; i < results.size(); i++) {
            JsonNode node = results.get(i).get("lexicalEntries");
            data[i] = new Result();
            data[i].word = results.get(i).get("word").asText();
            data[i].lexicalEntries = new LexicalEntry[node.size()];
            for (int i1 = 0; i1 < node.size(); i1++) {
                JsonNode node1 = node.get(i1).get("entries");
                data[i].lexicalEntries[i1] = new LexicalEntry();
                data[i].lexicalEntries[i1].lexicalCategory = node.get(i1).get("lexicalCategory").get("text").textValue();
                data[i].lexicalEntries[i1].entries = new Entry[node1.size()];
                for (int i2 = 0; i2 < node1.size(); i2++) {
                    JsonNode node2 = node1.get(i2).get("senses");
                    data[i].lexicalEntries[i1].entries[i2] = new Entry();
                    Entry entry = data[i].lexicalEntries[i1].entries[i2];
                    if (node1.get(i2).has("pronunciations")) {
                        JsonNode pronunciation = node1.get(i2).get("pronunciations");
                        for (int j = 0; j < pronunciation.size(); j++) {
                            if (pronunciation.get(j).has("audioFile")) {
                                if (entry.audioFiles == null) {
                                    entry.audioFiles = new ArrayList<>();
                                }
                                entry.audioFiles.add(pronunciation.get(j).get("audioFile").asText());
                            }
                        }
                    }
                    entry.senses = new Sense[node2.size()];
                    for (int i3 = 0; i3 < node2.size(); i3++) {
                        JsonNode node3 = node2.get(i3);
                        entry.senses[i3] = new Sense();
                        Sense sense = entry.senses[i3];
                        if (node3.has("definitions")) {
                            sense.definitions = new String[node3.get("definitions").size()];
                            for (int i4 = 0; i4 < node3.get("definitions").size(); i4++) {
                                sense.definitions[i4] = node3.get("definitions").get(i4).asText();
                            }
                        }
                        if (node3.has("crossReferenceMarkers")) {
                            sense.crossReferenceMarkers = new String[node3.get("crossReferenceMarkers").size()];
                            for (int i4 = 0; i4 < node3.get("crossReferenceMarkers").size(); i4++) {
                                sense.crossReferenceMarkers[i4] = node3.get("crossReferenceMarkers").get(i4).asText();
                            }
                        }
                        if (node3.has("shortDefinitions")) {
                            data[i].lexicalEntries[i1].entries[i2].senses[i3].shortDefinitions = new String[node3.get("shortDefinitions").size()];
                            for (int i4 = 0; i4 < node3.get("shortDefinitions").size(); i4++) {
                                data[i].lexicalEntries[i1].entries[i2].senses[i3].shortDefinitions[i4] = node3.get("shortDefinitions").get(i4).asText();
                            }
                        }
                        if (node3.has("subsenses")) {
                            JsonNode subsenses = node3.get("subsenses");
                            sense.subSenses = new SubSense[subsenses.size()];
                            for (int i4 = 0; i4 < subsenses.size(); i4++) {
                                sense.subSenses[i4] = new SubSense();
                                SubSense subSense = sense.subSenses[i4];
                                JsonNode subSenseNode = subsenses.get(i4);
                                if (subSenseNode.has("definitions")) {
                                    subSense.definitions = new String[subSenseNode.get("definitions").size()];
                                    for (int i5 = 0; i5 < subSenseNode.get("definitions").size(); i5++) {
                                        subSense.definitions[i5] = subSenseNode.get("definitions").get(i5).asText();
                                    }
                                }
                                if (subSenseNode.has("crossReferenceMarkers")) {
                                    subSense.crossReferenceMarkers = new String[subSenseNode.get("crossReferenceMarkers").size()];
                                    for (int i5 = 0; i5 < subSenseNode.get("crossReferenceMarkers").size(); i5++) {
                                        subSense.crossReferenceMarkers[i5] = subSenseNode.get("crossReferenceMarkers").get(i5).asText();
                                    }
                                }
                                if (subSenseNode.has("shortDefinitions")) {
                                    subSense.shortDefinitions = new String[subSenseNode.get("shortDefinitions").size()];
                                    for (int i5 = 0; i5 < subSenseNode.get("shortDefinitions").size(); i5++) {
                                        subSense.shortDefinitions[i5] = subSenseNode.get("shortDefinitions").get(i5).asText();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return data;
    }

    public String format() throws JsonProcessingException {
        StringBuilder out = new StringBuilder();
        Result[] data = read();
        for (int i = 0; i < data.length; i++) {
            out.append(data[i].word).append(superscriptConverter(i)).append("\n");
            for (int i1 = 0; i1 < data[i].lexicalEntries.length; i1++) {
                for (int i2 = 0; i2 < data[i].lexicalEntries[i1].entries.length; i2++) {
                    String category = data[i].lexicalEntries[i1].lexicalCategory;
                    for (int i3 = 0; i3 < data[i].lexicalEntries[i1].entries[i2].senses.length; i3++) {
                        out.append("  ");
                        out.append(i3==0?category == null ? "NULL" : category.length() > 4 ? category.substring(0, 4).toUpperCase() : category.toUpperCase():"    ");
                        out.append("  ");
                        out.append(i3 + 1);
                        out.append(". ");
                        if (data[i].lexicalEntries[i1].entries[i2].senses[i3].definitions != null) {
                            out.append(data[i].lexicalEntries[i1].entries[i2].senses[i3].definitions[0]).append("\n");
                        } else if (data[i].lexicalEntries[i1].entries[i2].senses[i3].crossReferenceMarkers != null) {
                            out.append(data[i].lexicalEntries[i1].entries[i2].senses[i3].crossReferenceMarkers[0]).append("\n");
                        } else {
                            out.append("NULL DEFINITION OR CROSS REFERENCE MARKERS\n");
                            continue;
                        }
                        if (data[i].lexicalEntries[i1].entries[i2].senses[i3].subSenses != null) {
                            for (int i4 = 0; i4 < data[i].lexicalEntries[i1].entries[i2].senses[i3].subSenses.length; i4++) {
                                out.append("          ");
                                out.append(i3+1);
                                out.append(".");
                                out.append(i4+1);
                                out.append(" ");
                                SubSense subSense = data[i].lexicalEntries[i1].entries[i2].senses[i3].subSenses[i4];
                                if (subSense.definitions != null) {
                                    out.append(subSense.definitions[0]).append("\n");
                                } else if (subSense.crossReferenceMarkers != null) {
                                    out.append(subSense.crossReferenceMarkers[0]).append("\n");
                                } else {
                                    out.append("NULL SUBSENSES\n");
                                }
                            }
                        }
                    }
                }
            }
        }
        out.append("------------------------------------------------------------------------");
        return out.toString();
    }

    public ArrayList<String> audio() throws JsonProcessingException {
        ArrayList<String> out = null;
        Result[] read = read();
        for (Result result : read) {
            for (LexicalEntry lEntry : result.lexicalEntries) {
                for (Entry entry : lEntry.entries) {
                    if (entry != null) {
                        if (entry.audioFiles != null) {
                            for (String file : entry.audioFiles) {
                                if (out == null) {
                                    out = new ArrayList<>();
                                }
                                if (!out.contains(file)) {
                                    out.add(file);
                                }
                            }
                        }
                    }
                }
            }
        }
        return out;
    }
    private char superscriptConverter(int i) {
        char[] c = {'¹', '²', '³', '⁴', '⁵', '⁶', '⁷', '⁸', '⁹'};
        return c[i];
    }
}
