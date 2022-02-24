package com.developer.shion.vocab.oxford;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import static com.developer.shion.vocab.oxford.OxfordDictionaryToolCommander.*;

public class OxfordCommandHandler {
    public OxfordCommandHandler() {

    }

    public void exec(String command) throws SQLException, IOException, InterruptedException {
        int space = command.indexOf(' ');
        if (space == -1) {
            if (command.equals("$history")) {
                boolean go = true;
                ArrayList<String> his = tool.getHistory();
                int current = 0;
                while (go) {
                    for (int i = current; i < current + 20 && i < his.size(); i++) {
                        System.out.println(his.get(i));
                    }
                    current += 20;
                    if (current < his.size()) {
                        System.out.println("## Type sth to continue");
                        System.out.println("## Type END to exit");
                        if (scanner.nextLine().equals("END")) {
                            go = false;
                        }
                    } else {
                        go = false;
                    }
                }
            } else if (command.startsWith("$s")) {
                OxfordApiData data = tool.search(previous);
                if (data != null) {
                    System.out.println("SOUND: " + previous);
                    ArrayList<String> audios = new OxfordDictionaryResultFormatter(data.getResponse()).audio();
                    System.out.println(audios);
                    player.play(audios);
                } else {
                    System.out.println("NOT FOUND: " + command);
                }
                System.out.println("------------------------------------------------------------------------");
            }
        }
    }
}
