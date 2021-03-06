package ekia;

// Lo que esta clase va a tener es que al acabarse el tiempo genera unas particulas que dañan al oponente
import java.awt.Color;
import java.awt.Graphics;

// no al que la invoco
public class Bomba extends GameObject {

    private boolean apuntoExplotar = false;
    private Cronometro c = new Cronometro(4); // Tardara 4 segundos en explotar
    private int colores = 10; // La bomba empezara siendo gris y se hara blanca cuando vaya a explotar, sumaremos de uno en uno, para que le de tiempo a esta madre
    private float[] dirsX = new float[8];
    private float[] dirsY = new float[8];

    public Bomba(float x, float y, ID id, Handler handler, Color color, int ancho, int alto) {
        super(x, y, id, handler, color, ancho, alto);
        Sound clip = new Sound("res/Sonidos/timer.mp3");
        clip.play();

        for (int i = 0; i < 8; i++) {
            float dirX = r.nextInt(12) - 6;
            float dirY = r.nextInt(12) - 6;
            if (dirX == 0 && dirY == 0) {
                i--;
                continue;
            }
            dirsX[i] = dirX;
            dirsY[i] = dirY;
        }
    }

    @Override
    public void tick() {

        try {
            if (colores >= 150) {
                colores = 9;
                apuntoExplotar = true;
            } else if (colores == 9) {
                colores = 255;
            }
            color = new Color(colores, colores, colores);
        } catch (Exception ex) {
            System.out.print("");
        }

        c.tick();

        if (!apuntoExplotar) {
            colores++;
        }

        if (c.terminado()) { // Si ya se detono
            float bx = x + ancho / 3;
            float by = y + alto / 3;
            for (int i = 0; i < 8; i++) {
                handler.addObject(new Bala(bx, by, ID.BalasBomba, handler, Color.gray, 10, 10, dirsX[i], dirsY[i], ID.BalasBomba));
            }
            Sound clip = new Sound("res/Sonidos/Explosion.mp3");
            clip.play();
            handler.removeObject(this);
        }
    }

    @Override
    public void render(Graphics g) {
        g.setColor(color);
        g.fillRect((int) x, (int) y, ancho, alto);
    }

    public float[] getDirsX() {
        return dirsX;
    }

    public void setDirsX(float[] dirsX) {
        this.dirsX = dirsX;
    }

    public float[] getDirsY() {
        return dirsY;
    }

    public void setDirsY(float[] dirsY) {
        this.dirsY = dirsY;
    }

}
