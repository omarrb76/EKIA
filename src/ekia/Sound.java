package ekia;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class Sound extends Thread {

    private String filepath;
    File file;
    FileInputStream fis;
    BufferedInputStream bis;
    Player player;

    public Sound(String filepath) {

        try {

            this.filepath = filepath;

            String absolutePath = FileSystems.getDefault().getPath(filepath).normalize().toAbsolutePath().toString();

            file = new File(absolutePath);
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            player = new Player(bis);

        } catch (IOException | JavaLayerException e) {

            System.out.println("Error al crear archivo de sonido: " + filepath);

        }

    }

    @Override
    public void run() {
        try {
            player.play();
        } catch (JavaLayerException e) {
            System.out.println("Error reproduciendo sonido " + filepath);
        }
    }

    public void play() {
        this.start();
    }

    public void close() {
        player.close();
    }

}
