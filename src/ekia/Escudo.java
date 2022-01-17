package ekia;

// Esta clase le dara un escudo al jugador que lo agarre, lo protegera durante cierto tiempo
import java.awt.Color;
import java.awt.Graphics;

public class Escudo extends GameObject {

    private boolean recogido = false;
    private GameObject player;

    public Cronometro esc = new Cronometro(10); // EL escudo durara diez segundos

    public Escudo(float x, float y, ID id, Handler handler, Color color, int ancho, int alto) {
        super(x, y, id, handler, color, ancho, alto);
        comprobarAparicion();
    }

    @Override
    public void tick() {

        if (recogido) {
            esc.tick(); // Si ya lo reogieron entonces hace una cuenta regresiva
        }
        for (int i = 0; i < handler.object.size(); i++) {
            GameObject tempObject = handler.object.get(i);

            if (tempObject instanceof Jugador) {
                if (this.getBounds().intersects(tempObject.getBounds())) {
                    if (!recogido) { // si no lo han recogido
                        ((Jugador) tempObject).setInvulnerable(true); // Lo hace invulnerable
                        player = tempObject; // Asigna el jugador para no tenerlo que buscar
                        Sound sonido = new Sound("res/Sonidos/Shield.mp3");
                        sonido.play();
                        recogido = true;
                        break;
                    }
                }
            }
        }

        if (esc.terminado()) { // Ya se acabo el escudo, lo quitamos
            ((Jugador) player).setInvulnerable(false); // Ya no es invulnerable
            handler.removeObject(this);
        }
    }

    @Override
    public void render(Graphics g) {

        if (!recogido) {
            g.setColor(color);
            g.fillRect((int) x, (int) y, ancho, alto);

            int sx = (int) (x + ancho);
            int sy = (int) (y + alto);

            g.setColor(Color.DARK_GRAY);

            g.drawLine((int) x, (int) y, sx, sy);
            g.drawLine(sx, (int) y, (int) x, sy);

            g.setColor(Color.white);
            g.drawRect((int) x, (int) y, ancho, alto);
        }

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

    public boolean isRecogido() {
        return recogido;
    }

    public Cronometro getEsc() {
        return esc;
    }

    public void setRecogido(boolean recogido) {
        this.recogido = recogido;
    }

    public void setEsc(Cronometro esc) {
        this.esc = esc;
    }

}
