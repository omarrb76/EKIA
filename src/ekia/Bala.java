package ekia;

import java.awt.Color;
import java.awt.Graphics;

public class Bala extends GameObject {

    private ID dueño;
    private Cronometro c = new Cronometro(1); // Para que el jugador no se haga inmune a sus balas permanentemente

    public Bala(float x, float y, ID id, Handler handler, Color color, int ancho, int alto, float velX, float velY, ID dueño) {
        // Este constructor recibe tres parametros mas que indican hacia donde va a ir la bala, esta dependera de hacia
        // donde esta llendo el jugador a la hora de dispararla, y su dueño para que no le haga daño al dispararla
        super(x, y, id, handler, color, ancho, alto);
        this.velX = velX;
        this.velY = velY;
        this.dueño = dueño;
    }

    @Override
    public void tick() {

        x += velX;
        y += velY;

        c.tick();

        if (y < 0 || y + 44 > EKIA.ALTO) {
            velY *= -1;
        }
        if (x < 0 || x + 24 > EKIA.ANCHO) {
            velX *= -1;
        }

        impactoPared();

        if (id != ID.BalasBomba && id != ID.BalaMenu) {
            handler.addObject(new Trail(x, y, ID.Trail, handler, color, ancho, alto, 0.05f));
        } else if (id == ID.BalaMenu) {
            handler.addObject(new Trail(x, y, ID.Trail, handler, color, ancho, alto, 0.02f));
        }
    }

    @Override
    public void render(Graphics g) {
        g.setColor(color);
        g.fillRect((int) x, (int) y, ancho, alto);
    }

    public ID getDueño() {
        return dueño;
    }

    public boolean terminoDefensa() { // regresa si el cronometro ya acabo
        return c.terminado();
    }

    public void impactoPared() { // Cambia su sentido si choco con una pared

        for (int i = 0; i < handler.object.size(); i++) {

            GameObject tempObject = handler.object.get(i); // Obtenemos el objeto

            if (tempObject.getId() == ID.ParedR) { // Si es una pared

                if (this.getBounds().intersects(tempObject.getBounds())) { // Si la pared y la bala se intersectan

                    int ancho = tempObject.getAncho();
                    int alto = tempObject.getAlto();
                    float xt = tempObject.getX();
                    float yt = tempObject.getY();

                    if ((x - velX) >= (xt + ancho)) {
                        velX *= -1; // Choco del lado derecho
                    } else if ((x - velX) <= (xt)) {
                        velX *= -1; // Choco del lado izquierdo
                    } else if ((y - velY) >= (yt + alto)) {
                        velY *= -1; // Choco por abajo
                    } else if ((y - velY) <= (yt)) {
                        velY *= -1; // Choco por arriba
                    }
                }

            }
        }

    }

}
