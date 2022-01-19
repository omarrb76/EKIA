package ekia;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.util.Random;
import java.util.Scanner;

public class EKIA extends Canvas implements Runnable {

    public static final int ANCHO = 640, ALTO = ANCHO / 12 * 9; // 480 Estas son nuestras dimensiones de la pantalla en la que estara nuestro juego

    // VARIABLES PARA EL FUNCIONAMIENTO
    private Thread thread;
    private boolean running = false; // Empieza en falso hasta que lo inciciemos
    private Handler handler;
    private Random r = new Random(System.currentTimeMillis());
    private Matchmaking match; // Para cuando ya se este jugando
    private HUD hud;
    private Menu menu;

    // CONEXION, LAS VOY A BORRAR
    public static String ip = "localhost";
    public static int myPort = 4545, opponentPort = 4545;

    public static EstadoJuego estadoActual = EstadoJuego.Portada; // Es estatica para que sea global y empezamos en el menu
    public static EstadoJuego estadoAnterior = EstadoJuego.Portada; // Esta es una variable de control para el errorOnline que nos puede aventar, saber cuando detener la musica del juego o del menu

    public static boolean pausado; // me indica si el juego esta en pausa o no

    public static int ganador = 0; // si es 1 es el jugador 1 y 2 pues el 2

    // ONLINE
    public static boolean conectadoOnline = false; // Una vez que se hizo la conexion lo cambiamos a true
    public static boolean errorOnline = false; // Por si no se pudo conectar el cliente
    public static boolean host = false; // Para saber si somos el host o no

    public EKIA() {

        Scanner scanner = new Scanner(System.in);
        /*System.out.println("Por favor introduzca la dirección ip de la máquina a la que se desea unir: ");
        ip = scanner.nextLine();*/
        System.out.println("Introduzca el puerto de la máquina a la que se desea unir: ");
        opponentPort = scanner.nextInt();
        System.out.println("Introduzca el puerto de su máquina: ");
        myPort = scanner.nextInt();

        handler = new Handler();

        match = new Matchmaking(handler);

        hud = new HUD(handler, match.partida);

        menu = new Menu(match, handler);

        this.addKeyListener(new KeyInput(handler, menu));

        this.addMouseListener(menu);

        Window window = new Window(ANCHO, ALTO, "E.K.I.A.", this);
    }

    public static void main(String[] args) {
        EKIA ekia = new EKIA();
    }

    @Override
    public void run() {
        this.requestFocus();
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while (delta >= 1) {
                tick();
                delta--;
            }
            if (running) {
                render();
            }
        }
        stop();
    }

    private void tick() {
        // Si no esta pausado
        if (!pausado) {
            try {
                handler.tick();
                if (estadoActual == EstadoJuego.Juego) {
                    match.tick(); // Solo cuando este en el juego se ejecute lo de match
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        if (menu.portadaAct) {
            menu.tick();
        }
    }

    private void render() {
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null) {
            this.createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();

        g.setColor(Color.black);
        g.fillRect(0, 0, ANCHO, ALTO);

        try {
            handler.render(g);
            if (estadoActual == EstadoJuego.Juego) {
                hud.render(g); // Que solo se haga render mientras este en el juego
            }
        } catch (Exception e) {
            //System.out.println("ERROR en el render");
            System.out.print("");
        }

        menu.render(g); // este ya tiene condicionales bien puestas

        if (pausado) {
            Rectangle2D rectangle = g.getFontMetrics().getStringBounds("PAUSADO", g);
            int x = (int) rectangle.getWidth() / 2;
            g.drawString("PAUSADO", EKIA.ANCHO / 2 - x, 150);
        }

        g.dispose();
        bs.show();
    }

    public synchronized void start() {
        thread = new Thread(this);
        thread.start();
        running = true;
    }

    public synchronized void stop() {
        try {
            thread.join();
            running = false;
        } catch (InterruptedException ex) {
            System.out.println(ex.getMessage());
        }
    }

    // Esta funcion nos sirve para mantener en sus limites minimos y maximos a las variables que mandemos aqui
    public static float clamp(float var, float min, float max) {
        if (var < min) {
            return min;
        } else if (var > max) {
            return max;
        } else {
            return var;
        }
    }

}
