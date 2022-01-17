package ekia;

// Esta clase te permite recuperar vida si la agarras, tenemos que asegurarnos de que
import java.awt.Color;
import java.awt.Graphics;

// si aparezca dentro del mapa, y que no aparezca dentro de las paredes, o sea, que si se
// pueda agarrar
public class Botiquin extends GameObject {

    public Botiquin(float x, float y, ID id, Handler handler, Color color, int ancho, int alto) {
        super(x, y, id, handler, color, ancho, alto);
        comprobarAparicion();
    }

    @Override
    public void tick() {
        for (int i = 0; i < handler.object.size(); i++) {
            GameObject tempObject = handler.object.get(i);

            if (tempObject instanceof Jugador) {
                if (this.getBounds().intersects(tempObject.getBounds())) {
                    if (!(tempObject.getVida() >= 100)) { // si el jugador tiene menos de 100 de vida si puede recoger el botiquin, de lo contrario no
                        tempObject.setVida(tempObject.getVida() + 30);
                        Sound sonido = new Sound("res/Sonidos/Healing.mp3");
                        sonido.play();
                        handler.removeObject(this);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void render(Graphics g) {
        g.setColor(color);
        g.fillRect((int) x, (int) y, ancho, alto);
        g.setColor(Color.white);
        g.drawRect((int) x, (int) y, ancho, alto);
        int xb = (int) (x + 9);
        int yb = (int) (y + 3);
        g.fillRect(xb, yb, 4, 14);
        xb = (int) (x + 4);
        yb = (int) (y + 8);
        g.fillRect(xb, yb, 14, 4);
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
