package com.developer.shion.vocab.oxford;
/**
 * This class is created on 10/2021.
 * Completed on 11/2021.
 */

import javafx.application.Application;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class OxfordDictionaryToolCommander extends Application {
    static UrlAudioPlayer player;
    static String previous;
    static OxfordCommandHandler handler;
    static OxfordDictionaryDataManager tool;
    static Scanner scanner;

    public static void main(String[] args) throws Exception {
        player = new UrlAudioPlayer();
        player.init();
        player.start(null);
        tool = new OxfordDictionaryDataManager();
        scanner = new Scanner(System.in);
        handler=new OxfordCommandHandler();
        System.out.println("------------------------------------------------------------------------");
        while (scanner.hasNextLine()) {
            try {
                String keyword = scanner.nextLine().toLowerCase();
                if (keyword.length() != 0) {
                    if (keyword.charAt(0) != '$') {
                        OxfordApiData data = tool.search(keyword);
                        System.out.println(" - - - - - - - - ");
                        if (data != null) {
                            previous = data.getKeyword();
                            System.out.println("SEARCH FOR: " + keyword);
                            System.out.println(new OxfordDictionaryResultFormatter(data.getResponse()).format());
                        } else {
                            System.out.println("NOT FOUND: " + keyword);
                            System.out.println("------------------------------------------------------------------------");
                        }
                    } else {
                        handler.exec(keyword);
                    }
                }
            } catch (SQLException | InterruptedException e) {
                System.out.println(e.getMessage());
                tool.init();
            }
        }
    }

    @Override
    public void start(Stage primaryStage) {

    }
}
