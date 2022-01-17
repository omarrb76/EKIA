package ekia;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Random;

public abstract class GameObject {

    protected Handler handler; // Para que todos puedan agregar y borrar desde sus metodos
    protected Random r; // Para generar aleatorios en caso de que sea necesario
    protected float x, y; // Posicion en el mapa
    protected ID id; // Para los ciclos de tick y render
    protected float velX, velY; // Velocidad
    protected int ancho, alto; // Para las dimensiones
    protected Color color; // El color de nuestro cuadrito en dado caso de que tenga
    protected int vida; // Para los jugadores o las balas que rebotan
    protected Sound sonido; // Para agregar sonido de muerte

    public GameObject(float x, float y, ID id, Handler handler, Color color, int ancho, int alto) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.handler = handler;
        this.color = color;
        this.alto = alto;
        this.ancho = ancho;
        vida = 100;
        r = new Random(System.currentTimeMillis());
    }

    public abstract void tick();

    public abstract void render(Graphics g);

    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, ancho, alto);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public ID getId() {
        return id;
    }

    public float getVelX() {
        return velX;
    }

    public float getVelY() {
        return velY;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setId(ID id) {
        this.id = id;
    }

    public void setVelX(float velX) {
        this.velX = velX;
    }

    public void setVelY(float velY) {
        this.velY = velY;
    }

    public Handler getHandler() {
        return handler;
    }

    public Random getR() {
        return r;
    }

    public int getAncho() {
        return ancho;
    }

    public int getAlto() {
        return alto;
    }

    public Color getColor() {
        return color;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void setR(Random r) {
        this.r = r;
    }

    public void setAncho(int ancho) {
        this.ancho = ancho;
    }

    public void setAlto(int alto) {
        this.alto = alto;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getVida() {
        return vida;
    }

    public void setVida(int vida) {
        this.vida = vida;
    }

    public Sound getSonido() {
        return sonido;
    }

    public void setSonido(Sound sonido) {
        this.sonido = sonido;
    }
}
