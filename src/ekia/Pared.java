package ekia;

import java.awt.Color;
import java.awt.Graphics;

// Esta clase servira para dar cobertura y hacer rebotar las balas, el jugador no la puede atravesar
public class Pared extends GameObject {

    private int green = 25, red = 25, blue = 25;
    private int aumentoGreen = r.nextInt(7) + 1, aumentoRed = r.nextInt(7) + 1, aumentoBlue = r.nextInt(7) + 1; // Estos 6 enteros es para que la pared cambie de color

    public Pared(float x, float y, ID id, Handler handler, Color color, int ancho, int alto) {
        super(x, y, id, handler, color, ancho, alto);
    }

    @Override
    public void tick() {

        green += aumentoGreen;
        red += aumentoRed;
        blue += aumentoBlue;

        if (green >= 255 || green < 25) {
            aumentoGreen *= -1;
        }
        if (red >= 255 || red < 25) {
            aumentoRed *= -1;
        }
        if (blue >= 255 || blue < 25) {
            aumentoBlue *= -1;
        }

        try {
            color = new Color(green, red, blue);
        } catch (Exception ex) {
            System.out.print("");
        }
    }

    @Override
    public void render(Graphics g) {
        g.setColor(color);
        g.fillRect((int) x, (int) y, ancho, alto);
    }

}
