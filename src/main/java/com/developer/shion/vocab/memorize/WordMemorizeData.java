package com.developer.shion.vocab.memorize;

import java.io.PrintStream;
import java.util.ArrayList;

public class WordMemorizeData {
    String word;
    ArrayList<Long> dates;

    public WordMemorizeData(String word, String datesString) {
        this.word=word;
        String[] datesArray = datesString.split(",");
        dates = new ArrayList<Long>();
        for (int i = 0; i < datesArray.length; i++) {
            dates.add(Long.parseLong(datesArray[i]));
        }
    }

    public WordMemorizeData(String word, ArrayList<Long> dates) {
        this.word = word;
        this.dates = dates;
    }
}
