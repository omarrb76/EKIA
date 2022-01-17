package ekia;

import java.awt.Color;
import java.awt.Graphics;

// Esta bala sigue al jugador enemigo, desde el jugador que lo haya disparado
public class BalaSmart extends GameObject {

    private ID dueño;
    private GameObject player;
    private int time;

    public BalaSmart(float x, float y, ID id, Handler handler, Color color, int ancho, int alto, ID dueño) {
        super(x, y, id, handler, color, ancho, alto);
        this.dueño = dueño; // Para que la bala no siga a su dueño, si no que siga a su enemigo
        time = 0;
        buscarJugador();
    }

    @Override
    public void tick() {
        x += velX;
        y += velY;

        float diffX = x - player.getX() - 2;
        float diffY = y - player.getY() - 2;
        float distance = (float) Math.sqrt(Math.pow(x - player.getX(), 2) + Math.pow(y - player.getY(), 2));

        velX = ((-1 / distance) * diffX) * 2.5f;
        velY = ((-1 / distance) * diffY) * 2.5f;

        //if (y < 0 || y > EKIA.ANCHO) velY *= -1;
        //if (x < 0 || x > EKIA.ALTO ) velX *= -1;
        handler.addObject(new Trail(x, y, ID.Trail, handler, color, ancho, alto, 0.010f)); // Agregar un trail

        time++;
        if (time > 900) {
            handler.removeObject(this);
        }
    }

    @Override
    public void render(Graphics g) {
        g.setColor(color);
        g.fillRect((int) x, (int) y, ancho, alto);
    }

    private void buscarJugador() { // Busca al jugador para tenerlo en la mira
        for (int i = 0; i < handler.object.size(); i++) {
            GameObject temp = handler.object.get(i);

            if (temp instanceof Jugador) {
                if (temp.getId() != dueño) { // Osea que el es el enemigo
                    player = temp;
                    break; // No queremos que siga buscando
                }
            }
        }
    }

    public ID getDueño() {
        return dueño;
    }

    public GameObject getPlayer() {
        return player;
    }

    public void setDueño(ID dueño) {
        this.dueño = dueño;
    }

    public void setPlayer(GameObject player) {
        this.player = player;
    }

}
