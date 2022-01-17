package ekia;

import java.awt.Color;
import java.awt.Graphics;

public class BombaSpawner extends GameObject {

    public BombaSpawner(float x, float y, ID id, Handler handler, Color color, int ancho, int alto) {
        super(x, y, id, handler, color, ancho, alto);
        color = new Color(14, 238, 133);
        comprobarAparicion();
    }

    @Override
    public void tick() {
        for (int i = 0; i < handler.object.size(); i++) {
            GameObject tempObject = handler.object.get(i);

            if (tempObject instanceof Jugador) {
                if (this.getBounds().intersects(tempObject.getBounds())) {
                    Sound clip = new Sound("res/Sonidos/PickBomb.mp3");
                    clip.play();
                    ((Jugador) tempObject).setBomba(true); // Le activa una bomba
                    handler.removeObject(this);
                    break;
                }
            }
        }
    }

    @Override
    public void render(Graphics g) {
        g.setColor(color);
        g.fillRect((int) x, (int) y, ancho, alto);

        int sx = (int) (x + ancho);
        int sy = (int) (y + alto);

        g.setColor(Color.gray);

        g.drawLine((int) x, (int) y, sx, sy);
        g.drawLine(sx, (int) y, (int) x, sy);

        g.setColor(Color.white);
        g.drawRect((int) x, (int) y, ancho, alto);
    }

    public void comprobarAparicion() { // Comprueba que el botiquin no vaya a aparecer dentro de un jugador, fuera del mapa o en una pared
        for (int i = 0; i < handler.object.size(); i++) {
            GameObject tempObject = handler.object.get(i);

            ID idTemp = tempObject.getId(); // Para no escribir mucho en el if

            if (idTemp == ID.Jugador1 || idTemp == ID.Jugador2 || idTemp == ID.ParedR) {

                if (this.getBounds().intersects(tempObject.getBounds())) { // Si su posicion si es igual
                    x = r.nextInt(EKIA.ANCHO - 40); // le damos nuevos valores a x y y
                    y = r.nextInt(EKIA.ALTO - 60);
                    comprobarAparicion();
                    return; // Para que no se haga bucle
                }

            }
        }
    }

}
