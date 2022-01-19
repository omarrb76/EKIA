package ekia;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Canciones {

    public ArrayList<String> playlist;
    public ArrayList<String> playlistEG;
    public String portadaSong;
    private Random r = new Random(System.currentTimeMillis());
    public Sound cancionActual = null; // Es la cancion que se esta reproduciendo

    public Canciones(String[] nombres) {
        playlist = new ArrayList();
        // Mete todas las canciones en la playlist
        playlist.addAll(Arrays.asList(nombres));
        portadaSong = "res/Canciones/Crystals.mp3";
    }

    public void elegirCancionAzar() {
        int azar = r.nextInt(playlist.size()); // elige una cancion de la playlist
        String name = playlist.get(azar);
        cancionActual = new Sound(name);
    }

    public void portCancion() {
        cancionActual = new Sound(portadaSong);
    }

    public void reproducirCancion() { // Reproduce la cancion actual
        cancionActual.play();
    }

    public void detenerCancion() { // Detiene la cancion actual
        cancionActual.close();
    }
}
