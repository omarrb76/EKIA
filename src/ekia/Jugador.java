package ekia;

import java.awt.Color;
import java.awt.Graphics;

public class Jugador extends GameObject {

    public Cronometro defensaSpawn = new Cronometro(1.5f); // 1.5 segundos de inmunidad despues de morir 
    // Va a servir para que tengas un poco de tiempo de inmunidad despues de morir

    public Cronometro defensaBalaPropia = new Cronometro(1); // Tienes un segundo para quitarte de donde disparo tu bala

    public boolean invulnerable = false; // Para saber si tiene el escudo activado

    public int balasSmart = 0; // Cuando tenga balas smart se sube este numero a 3

    public boolean bomba = false; // Para saber si tiene bombas

    private Color colorInvencible;

    public Jugador(float x, float y, ID id, Handler handler, Color color, int ancho, int alto) {
        super(x, y, id, handler, color, ancho, alto);
    }

    @Override
    public void tick() {

        defensaSpawn.tick(); // Para que ejecute el tiempo
        defensaBalaPropia.tick();

        x += velX;
        y += velY;

        handler.addObject(new Trail(x, y, ID.Trail, handler, color, ancho, alto, 0.095f)); // Agregar un trail

        x = EKIA.clamp(x, 0, (float) EKIA.ANCHO - 40);
        y = EKIA.clamp(y, 0, (float) EKIA.ALTO - 60);

        collision();

        // Lógica de reaparición y fin de juego
        if (vida <= 0) {
            Sound a = new Sound("res/Sonidos/deathSound.mp3");
            a.play();
            defensaSpawn.reiniciar();
            if (id == ID.Jugador1) {

                // Lógica de reaparción
                if (EKIA.host) {
                    x = 50;
                    y = 208; // Los devolvemos a su spawn
                } else if (EKIA.conectadoOnline) {
                    x = EKIA.ANCHO - 90;
                    y = 208; // Los devolvemos a su spawn
                } else {
                    x = 50;
                    y = 208; // Los devolvemos a su spawn
                }

                Matchmaking.vidasP1--;
                if (Matchmaking.vidasP1 <= 0) {
                    EKIA.ganador = 2; // Porque es el jugador 1
                    System.out.println("Se acabo la partida");
                    EKIA.estadoActual = EstadoJuego.Fin; // Se acabo el juego
                    Menu.primeraVezMenu = true;
                }

            } else if (id == ID.Jugador2) {

                if (EKIA.host) {
                    x = 50;
                    y = 208; // Los devolvemos a su spawn
                } else {
                    x = EKIA.ANCHO - 90;
                    y = 208; // Los devolvemos a su spawn
                }

                // Lógica de reaparción
                if (EKIA.host) {
                    x = EKIA.ANCHO - 90;
                    y = 208; // Los devolvemos a su spawn
                } else if (EKIA.conectadoOnline) {
                    x = 50;
                    y = 208; // Los devolvemos a su spawn
                } else {
                    x = EKIA.ANCHO - 90;
                    y = 208; // Los devolvemos a su spawn
                }

                Matchmaking.vidasP2--;
                if (Matchmaking.vidasP2 <= 0) {
                    EKIA.ganador = 1;
                    System.out.println("Se acabo la partida");
                    EKIA.estadoActual = EstadoJuego.Fin;
                    Menu.primeraVezMenu = true;
                }

            }
            vida = 100;
        }
    }

    private void collision() {

        for (int i = 0; i < handler.object.size(); i++) {

            GameObject tempObject = handler.object.get(i);

            if (tempObject.getId() == ID.Bala || tempObject.getId() == ID.BalasBomba || tempObject.getId() == ID.BalaSmart) {
                // Es un objeto que le puede hacer daño?
                if (getBounds().intersects(tempObject.getBounds())) {
                    continuacionColission(tempObject);
                }
            } else if (tempObject.getId() == ID.ParedR) {
                colisionPared(tempObject);
            }
        }
    }

    private void continuacionColission(GameObject tempObject) { // Esta clase es la continuacion de colision porque es un desmadre

        if (defensaSpawn.terminado()) { // Si ya se le acabo su defensa de spawn

            if (tempObject instanceof Bala && (!invulnerable)) { // Si es una bala y ya es vulnerable

                if (((Bala) tempObject).getDueño() == id) { // Si es su bala
                    if (defensaBalaPropia.terminado()) { // Si ya termino su timer
                        ejecutarHitmarker();
                        vida -= 15;
                        handler.removeObject(tempObject);
                    } else {
                        if (((Bala) (tempObject)).terminoDefensa()) { // Esta bala ya la habia disparado antes
                            ejecutarHitmarker();
                            vida -= 15;
                            handler.removeObject(tempObject);
                        }
                    }
                } else { // Si no es su bala
                    ejecutarHitmarker();
                    vida -= 15;
                    handler.removeObject(tempObject);
                }

            } else if (tempObject.getId() == ID.BalaSmart && (!invulnerable)) {
                if (((BalaSmart) tempObject).getDueño() != this.id) { // Si la bala no es de este dueño
                    ejecutarHitmarker();
                    vida -= 40;
                    handler.removeObject(tempObject);
                }
            }
        }
    }

    private void ejecutarHitmarker() {
        Sound clip = new Sound("res/Sonidos/hitmarker.mp3");
        clip.play();
    }

    @Override
    public void render(Graphics g) {

        // Lógica de colores
        if (invulnerable) {
            if (id == ID.Jugador1) {
                if (EKIA.host) {
                    color = Color.yellow;
                } else if (EKIA.conectadoOnline) {
                    color = new Color(148, 0, 211);
                } else {
                    color = Color.yellow;
                }
            } else {
                if (EKIA.host) {
                    color = new Color(148, 0, 211);
                } else if (EKIA.conectadoOnline) {
                    color = Color.yellow;
                } else {
                    color = new Color(148, 0, 211);
                }
            }
        } else {
            if (id == ID.Jugador1) {
                if (EKIA.host) {
                    color = Color.red;
                } else if (EKIA.conectadoOnline) {
                    color = Color.blue;
                } else {
                    color = Color.red;
                }
            } else {
                if (EKIA.host) {
                    color = Color.blue;
                } else if (EKIA.conectadoOnline) {
                    color = Color.red;
                } else {
                    color = Color.blue;
                }
            }
        }

        g.setColor(color);

        g.fillRect((int) x, (int) y, ancho, alto);
    }

    public void colisionPared(GameObject tempObject) {
        if (this.getBounds().intersects(tempObject.getBounds())) {
            x = x - velX;
            y = y - velY;
            int ancho = tempObject.getAncho();
            int alto = tempObject.getAlto();
            float xt = tempObject.getX();
            float yt = tempObject.getY();

            if ((x - velX) >= (xt + ancho)) {
                velX = 0; // Choco del lado derecho
            } else if ((x - velX) <= (xt)) {
                velX = 0; // Choco del lado izquierdo
            } else if ((y - velY) >= (yt + alto)) {
                velY = 0; // Choco por abajo
            } else if ((y - velY) <= (yt)) {
                velY = 0; // Choco por arriba
            }
        }
    }

    public Cronometro getDefensaSpawn() {
        return defensaSpawn;
    }

    public Cronometro getDefensaBalaPropia() {
        return defensaBalaPropia;
    }

    public boolean isInvulnerable() {
        return invulnerable;
    }

    public void setDefensaSpawn(Cronometro defensaSpawn) {
        this.defensaSpawn = defensaSpawn;
    }

    public void setDefensaBalaPropia(Cronometro defensaBalaPropia) {
        this.defensaBalaPropia = defensaBalaPropia;
    }

    public void setInvulnerable(boolean invulnerable) {
        this.invulnerable = invulnerable;
    }

    public void setBalasSmart(int n) {
        this.balasSmart = n;
    }

    public int getSmart() {
        return balasSmart;
    }

    public void setBomba(boolean bomba) {
        this.bomba = bomba;
    }

    public boolean getBomba() {
        return bomba;
    }

}
