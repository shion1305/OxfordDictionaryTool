package com.developer.shion.vocab.oxford;

/**
 * This class is created on 11/2021.
 * Completed on 11/2021.
 */

import com.developer.shion.fundamentals.FileDownloader;
import javafx.application.Application;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class UrlAudioPlayer extends Application {
    ArrayList<MediaPlayer> mediaPlayers;
    int index;
    int count;
    int countM;

    public static void main(String[] args) throws Exception {

        UrlAudioPlayer a = new UrlAudioPlayer();
        a.init();
        a.start(null);
        ArrayList<String> target = new ArrayList<>();
        target.add("https://audio.oxforddictionaries.com/en/mp3/cat_us_1.mp3");
        target.add("https://audio.oxforddictionaries.com/en/mp3/ant_us_1.mp3");
        a.play(target);
    }

    public UrlAudioPlayer() {
    }

    synchronized public void play(ArrayList<String> urls) throws IOException, InterruptedException {
        if (urls == null) return;
        if (urls.size() == 0) return;
        mediaPlayers = new ArrayList<>();
        index = 0;
        count = 0;
        for (String url : urls) {
            File file = File.createTempFile("OxfordAudio", null);
            FileDownloader downloader = new FileDownloader(url);
            FileOutputStream stream = new FileOutputStream(file);
            stream.write(downloader.doTask());
            stream.flush();
            stream.close();
            Media hit = new Media(file.toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(hit);
            mediaPlayer.setOnEndOfMedia(mediaPlayer::stop);
            mediaPlayer.setOnStopped(() -> {
                if (++count == countM) {
                    mediaPlayer.dispose();
                }else {
                    index = ++index % urls.size();
                    if (!mediaPlayers.get(index).getStatus().equals(MediaPlayer.Status.DISPOSED)) {
                        mediaPlayers.get(index).play();
                    }
                }
            });
            mediaPlayer.setOnPlaying(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mediaPlayer.getStatus();
            });
            mediaPlayers.add(mediaPlayer);
        }
        countM = mediaPlayers.size() * 3;
        mediaPlayers.get(index).play();
    }

    @Override
    public void start(Stage primaryStage) {

    }
}
