package ekia;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;

public class Menu extends MouseAdapter {

    // CONTROLA LOS EVENTOS DE LA PARTIDA
    public Matchmaking m;
    private Handler handler;

    // CONEXION PARA EL ONLINE
    Conexion conexion;

    // DISEÑO DEL NIVEL
    public static DISEÑO diseño = DISEÑO.libre;
    private DiseñoMapa diseñoMap = null;
    private int diseñoInt = 0; // Para mostrar que diseño esta eligiendo

    // VARIABLES PARA LÓGICA DE RENDERIZADO EN LAS DISTINTAS PANTALLAS
    public static boolean primeraVezMenu = true; // Para solo agregar una vez las particulas del menu
    private Random r = new Random(System.currentTimeMillis());
    String[] cancionesJuego = {"res/Canciones/Divide.mp3", "res/Canciones/Genesis Justice.mp3", "res/Canciones/LED Spirals.mp3", "res/Canciones/Fake.mp3", "res/Canciones/Semi.mp3", "res/Canciones/Infinite Ammo.mp3"}; // Son las canciones para durante el juego
    String[] cancionesMenu = {"res/Canciones/Hydrogen Moon.mp3", "res/Canciones/John Wick Mode.mp3", "res/Canciones/FIGHT.mp3", "res/Canciones/Dust.mp3"};
    private Canciones playlistGame = new Canciones(cancionesJuego);
    private Canciones playlistMenu = new Canciones(cancionesMenu);
    private BufferedImage image;
    private BufferedImage image2;
    private BufferedImage[] nivImg = new BufferedImage[3];
    private BufferedImage ayu, ayu2, ayu3;
    boolean portadaAct;
    int portadaCont;
    private boolean portPrimeraVez, portSegundaVez;

    // PARA MOSTRAR CENTRADOS LOS TEXTOS
    Font fnt, fnt2;
    Rectangle2D rectangle;
    int x, y;

    public Menu(Matchmaking m, Handler handler) {

        this.m = m;
        this.handler = handler;
        diseñoMap = new DiseñoMapa(null, handler); // Creamos nuestro diseñador de mapas, le mandamos un null porque lo eligiremos mas tarde
        fnt = fuente("res/square.ttf", 1, 25);
        fnt2 = fuente("res/square.ttf", 1, 18);

        try {
            image = ImageIO.read(new File("res/Imagenes/portada.png"));
            image2 = ImageIO.read(new File("res/Imagenes/portada2.png"));
            nivImg[0] = ImageIO.read(new File("res/Imagenes/nv1.png"));
            nivImg[1] = ImageIO.read(new File("res/Imagenes/nv2.png"));
            nivImg[2] = ImageIO.read(new File("res/Imagenes/nv3.png"));
            ayu = ImageIO.read(new File("res/Imagenes/ay.png"));
            ayu2 = ImageIO.read(new File("res/Imagenes/ay2.png"));
            ayu3 = ImageIO.read(new File("res/Imagenes/ay3.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        portadaAct = true;
        portadaCont = 0;
        portPrimeraVez = true;
        portSegundaVez = false;
    }

    public void nuevaConexion() {
        if (conexion == null) {
            conexion = new Conexion(handler, EKIA.ip, EKIA.myPort, EKIA.opponentPort, this);
            Conexion [] nuevaConexion = {conexion};
            m.setConexion(nuevaConexion);
        }
    }

    public void terminaConexion() {
        if (conexion != null) {
            System.out.println("Mande a cerrar los sockets");
            EKIA.conectadoOnline = false;
            conexion.setRunning(false);
            conexion = null;
        }
    }

    public boolean getPrimeraVezMenu() {
        return primeraVezMenu;
    }

    public void setPrimeraVezMenu(boolean primeraVezMenu) {
        this.primeraVezMenu = primeraVezMenu;
    }

    public void tick() {
        if (portadaAct) {
            portadaCont++;
            if (portadaCont == 465) {
                portSegundaVez = true;
            }
            if (portadaCont >= 585) {
                portadaAct = false;
                EKIA.estadoActual = EstadoJuego.Menu;
                playlistMenu.detenerCancion();
            }
        }
    }

    public void render(Graphics g) {

        if (null != EKIA.estadoActual) {
            switch (EKIA.estadoActual) {
                case Fin:
                    
                    if (primeraVezMenu) {
                        handler.object.clear();
                        generarParticulas();
                        primeraVezMenu = false;
                    }

                    // Ya gano alguien
                    switch (EKIA.ganador) {
                        case 1:
                            g.setFont(fnt);
                            g.setColor(Color.white);
                            if (EKIA.host || !EKIA.conectadoOnline) {
                                x = g.getFontMetrics(fnt).stringWidth("ha ganado el jugador 1") / 2;
                                g.drawString("ha ganado el jugador 1", EKIA.ANCHO / 2 - x, 100);
                            } else {
                                x = g.getFontMetrics(fnt).stringWidth("ha ganado el jugador 2") / 2;
                                g.drawString("ha ganado el jugador 2", EKIA.ANCHO / 2 - x, 100);
                            }
                            break;
                        case 2:
                            g.setFont(fnt);
                            g.setColor(Color.white);
                            if (EKIA.host || !EKIA.conectadoOnline) {
                                x = g.getFontMetrics(fnt).stringWidth("ha ganado el jugador 2") / 2;
                                g.drawString("ha ganado el jugador 2", EKIA.ANCHO / 2 - x, 100);
                            } else {
                                x = g.getFontMetrics(fnt).stringWidth("ha ganado el jugador 1") / 2;
                                g.drawString("ha ganado el jugador 1", EKIA.ANCHO / 2 - x, 100);
                            }
                            break;
                        default:
                            // un empate
                            g.setFont(fnt);
                            g.setColor(Color.white);
                            x = g.getFontMetrics(fnt).stringWidth("Ha quedado en EMPATE") / 2;
                            g.drawString("Ha quedado en EMPATE", EKIA.ANCHO / 2 - x, 100);
                            break;
                    }

                    g.setFont(fnt2);

                    // SINO ESTA ONLINE, QUE PUEDA REVANCHA
                    if (!EKIA.conectadoOnline) {

                        // REVANCHA
                        g.drawRect(EKIA.ANCHO / 2 - 100, 250, 200, 64);
                        rectangle = g.getFontMetrics(fnt2).getStringBounds("revancha", g);
                        x = (int) rectangle.getWidth() / 2;
                        y = (64 - (int) rectangle.getHeight()) / 2 + g.getFontMetrics(fnt2).getAscent();
                        g.drawString("revancha", EKIA.ANCHO / 2 - x, 250 + y);

                    }

                    // MENU
                    g.drawRect(EKIA.ANCHO / 2 - 100, 350, 200, 64);
                    rectangle = g.getFontMetrics(fnt2).getStringBounds("menu", g);
                    x = (int) rectangle.getWidth() / 2;
                    y = (64 - (int) rectangle.getHeight()) / 2 + g.getFontMetrics(fnt2).getAscent();
                    g.drawString("menu", EKIA.ANCHO / 2 - x, 350 + y);

                    break;

                case ErrorOnline:

                    // Si se perdio la conexion con el oponente
                    if (primeraVezMenu) {
                        handler.object.clear();
                        generarParticulas();
                        primeraVezMenu = false;
                    }

                    // TITULO SE PERDIO LA CONEXION
                    g.setColor(Color.white);
                    g.setFont(fnt);
                    rectangle = g.getFontMetrics(fnt).getStringBounds("Se perdio la conexion", g);
                    x = (int) rectangle.getWidth() / 2;
                    g.drawString("Se perdio la conexion", EKIA.ANCHO / 2 - x, 100);

                    // ATRAS
                    g.setFont(fnt2);
                    g.drawRect(EKIA.ANCHO / 2 - 100, 350, 200, 64);
                    rectangle = g.getFontMetrics(fnt2).getStringBounds("atras", g);
                    x = (int) rectangle.getWidth() / 2;
                    y = (64 - (int) rectangle.getHeight()) / 2 + g.getFontMetrics(fnt2).getAscent();
                    g.drawString("atras", EKIA.ANCHO / 2 - x, 350 + y);
                    break;

                case Menu:

                    if (primeraVezMenu) { // Si es la primera vez que se abre el menu agrega nueva particulas
                        generarParticulas();
                        playlistMenu.elegirCancionAzar();
                        playlistMenu.reproducirCancion();
                        primeraVezMenu = false; // Para que no vuelva a entrar
                    }

                    // EKIA
                    g.setColor(Color.white);
                    g.setFont(fnt);
                    rectangle = g.getFontMetrics(fnt).getStringBounds("e.k.i.a.", g);
                    x = (int) rectangle.getWidth() / 2;
                    g.drawString("e.k.i.a.", EKIA.ANCHO / 2 - x, 100);

                    // JUGAR
                    g.setFont(fnt2);
                    g.drawRect(EKIA.ANCHO / 4 - 100, 150, 200, 64);
                    rectangle = g.getFontMetrics(fnt2).getStringBounds("jugar", g);
                    x = (int) rectangle.getWidth() / 2;
                    y = (64 - (int) rectangle.getHeight()) / 2 + g.getFontMetrics(fnt2).getAscent();
                    g.drawString("jugar", EKIA.ANCHO / 4 - x, 150 + y);

                    // ONLINE
                    g.drawRect(EKIA.ANCHO / 4 - 100, 250, 200, 64);
                    rectangle = g.getFontMetrics(fnt2).getStringBounds("online", g);
                    x = (int) rectangle.getWidth() / 2;
                    y = (64 - (int) rectangle.getHeight()) / 2 + g.getFontMetrics(fnt2).getAscent();
                    g.drawString("online", EKIA.ANCHO / 4 - x, 250 + y);

                    // AYUDA
                    g.drawRect(EKIA.ANCHO / 4 * 3 - 100, 150, 200, 64);
                    rectangle = g.getFontMetrics(fnt2).getStringBounds("ayuda", g);
                    x = (int) rectangle.getWidth() / 2;
                    y = (64 - (int) rectangle.getHeight()) / 2 + g.getFontMetrics(fnt2).getAscent();
                    g.drawString("ayuda", EKIA.ANCHO / 4 * 3 - x, 150 + y);

                    // SALIR
                    g.drawRect(EKIA.ANCHO / 4 * 3 - 100, 250, 200, 64);
                    rectangle = g.getFontMetrics(fnt2).getStringBounds("salir", g);
                    x = (int) rectangle.getWidth() / 2;
                    y = (64 - (int) rectangle.getHeight()) / 2 + g.getFontMetrics(fnt2).getAscent();
                    g.drawString("salir", EKIA.ANCHO / 4 * 3 - x, 250 + y);
                    break;

                case SeleccionPersonaje:

                    // TITULO SELECCIONE DESPLIEGUE
                    g.setColor(Color.white);
                    g.setFont(fnt);
                    x = g.getFontMetrics(fnt).stringWidth("seleccione despliegue") / 2;
                    g.drawString("seleccione despliegue", EKIA.ANCHO / 2 - x, 50);

                    // BOTONES DERECHA E IZQUIERDA
                    g.drawRect(15, 170, 50, 150); // IZQUIERDA
                    g.drawLine(45, 207, 25, 245);
                    g.drawLine(25, 245, 45, 282);
                    g.drawRect(EKIA.ANCHO - 70, 170, 50, 150); // DERECHA
                    g.drawLine(EKIA.ANCHO - 50, 207, EKIA.ANCHO - 30, 245);
                    g.drawLine(EKIA.ANCHO - 30, 245, EKIA.ANCHO - 50, 282);

                    // ATRAS
                    g.setFont(fnt2);
                    g.drawRect(EKIA.ANCHO / 4 - 100, EKIA.ALTO - 120, 200, 64);
                    rectangle = g.getFontMetrics(fnt2).getStringBounds("atras", g);
                    x = (int) rectangle.getWidth() / 2;
                    y = (64 - (int) rectangle.getHeight()) / 2 + g.getFontMetrics(fnt2).getAscent();
                    g.drawString("atras", EKIA.ANCHO / 4 - x, EKIA.ALTO - 120 + y);

                    // ACEPTAR
                    g.drawRect(EKIA.ANCHO / 4 * 3 - 100, EKIA.ALTO - 120, 200, 64);
                    rectangle = g.getFontMetrics(fnt2).getStringBounds("aceptar", g);
                    x = (int) rectangle.getWidth() / 2;
                    y = (64 - (int) rectangle.getHeight()) / 2 + g.getFontMetrics(fnt2).getAscent();
                    g.drawString("aceptar", EKIA.ANCHO / 4 * 3 - x, EKIA.ALTO - 120 + y);

                    // TITULOS DE NIVELES
                    if (diseñoInt == DISEÑO.libre.ordinal()) {

                        rectangle = g.getFontMetrics(fnt2).getStringBounds("libre", g);
                        x = (int) rectangle.getWidth() / 2;
                        g.drawString("libre", EKIA.ANCHO / 2 - x, 100);
                        g.drawImage(nivImg[0], 176, 145, 284, 200, null);

                    } else if (diseñoInt == DISEÑO.intermedio.ordinal()) {

                        rectangle = g.getFontMetrics(fnt2).getStringBounds("intermedio", g);
                        x = (int) rectangle.getWidth() / 2;
                        g.drawString("intermedio", EKIA.ANCHO / 2 - x, 100);
                        g.drawImage(nivImg[1], 176, 145, 284, 200, null);

                    } else if (diseñoInt == DISEÑO.laberinto.ordinal()) {

                        rectangle = g.getFontMetrics(fnt2).getStringBounds("sala de batalla", g);
                        x = (int) rectangle.getWidth() / 2;
                        g.drawString("sala de batalla", EKIA.ANCHO / 2 - x, 100);
                        g.drawImage(nivImg[2], 176, 145, 284, 200, null);

                    }
                    g.drawRect(176, 145, 284, 200);
                    break;

                case Online:

                    if (!EKIA.conectadoOnline) {

                        // TITULO SELECCIONE DESPLIEGUE
                        g.setColor(Color.white);
                        g.setFont(fnt);
                        x = g.getFontMetrics(fnt).stringWidth("Online") / 2;
                        g.drawString("Online", EKIA.ANCHO / 2 - x, 50);

                        // HOSTEAR
                        g.setFont(fnt2);
                        g.drawRect(EKIA.ANCHO / 4 - 100, 150, 200, 64);
                        rectangle = g.getFontMetrics(fnt2).getStringBounds("hostear", g);
                        x = (int) rectangle.getWidth() / 2;
                        y = (64 - (int) rectangle.getHeight()) / 2 + g.getFontMetrics(fnt2).getAscent();
                        g.drawString("hostear", EKIA.ANCHO / 4 - x, 150 + y);

                        // UNIRSE
                        g.setFont(fnt2);
                        g.drawRect(EKIA.ANCHO / 4 * 3 - 100, 150, 200, 64);
                        rectangle = g.getFontMetrics(fnt2).getStringBounds("unirse", g);
                        x = (int) rectangle.getWidth() / 2;
                        y = (64 - (int) rectangle.getHeight()) / 2 + g.getFontMetrics(fnt2).getAscent();
                        g.drawString("unirse", EKIA.ANCHO / 4 * 3 - x, 150 + y);

                        // ATRAS
                        g.setFont(fnt2);
                        g.drawRect(EKIA.ANCHO / 2 - 100, 350, 200, 64);
                        rectangle = g.getFontMetrics(fnt2).getStringBounds("atras", g);
                        x = (int) rectangle.getWidth() / 2;
                        y = (64 - (int) rectangle.getHeight()) / 2 + g.getFontMetrics(fnt2).getAscent();
                        g.drawString("atras", EKIA.ANCHO / 2 - x, 350 + y);
                    } else {

                    }
                    break;

                case Host:

                    // TITULO SELECCIONE DESPLIEGUE
                    g.setColor(Color.white);
                    g.setFont(fnt);
                    x = g.getFontMetrics(fnt).stringWidth("Esperando Oponente") / 2;
                    g.drawString("Esperando Oponente", EKIA.ANCHO / 2 - x, 50);

                    // CANCELAR
                    g.setFont(fnt2);
                    g.drawRect(EKIA.ANCHO / 2 - 100, 350, 200, 64);
                    rectangle = g.getFontMetrics(fnt2).getStringBounds("cancelar", g);
                    x = (int) rectangle.getWidth() / 2;
                    y = (64 - (int) rectangle.getHeight()) / 2 + g.getFontMetrics(fnt2).getAscent();
                    g.drawString("cancelar", EKIA.ANCHO / 2 - x, 350 + y);
                    break;

                case Client:

                    // TITULO SELECCIONE DESPLIEGUE
                    g.setColor(Color.white);
                    g.setFont(fnt);
                    x = g.getFontMetrics(fnt).stringWidth("Uniendose a Oponente") / 2;
                    g.drawString("Uniendose a Oponente", EKIA.ANCHO / 2 - x, 50);

                    // ERROR ONLINE
                    g.setFont(fnt2);
                    if (EKIA.errorOnline) {
                        rectangle = g.getFontMetrics(fnt2).getStringBounds("no se pudo conectar con oponente", g);
                        x = (int) rectangle.getWidth() / 2;
                        y = (64 - (int) rectangle.getHeight()) / 2 + g.getFontMetrics(fnt2).getAscent();
                        g.drawString("no se pudo conectar con oponente", EKIA.ANCHO / 2 - x, 150 + y);
                    }

                    // CANCELAR
                    g.drawRect(EKIA.ANCHO / 2 - 100, 350, 200, 64);
                    rectangle = g.getFontMetrics(fnt2).getStringBounds("cancelar", g);
                    x = (int) rectangle.getWidth() / 2;
                    y = (64 - (int) rectangle.getHeight()) / 2 + g.getFontMetrics(fnt2).getAscent();
                    g.drawString("cancelar", EKIA.ANCHO / 2 - x, 350 + y);
                    break;

                case Ayuda:

                    // TITULO AYUDA
                    g.setFont(fnt);
                    g.setColor(Color.white);
                    rectangle = g.getFontMetrics(fnt).getStringBounds("ayuda", g);
                    x = (int) rectangle.getWidth() / 2;
                    g.drawString("ayuda", EKIA.ANCHO / 2 - x, 70);
                    g.drawImage(ayu, 20, 50, 169, 301, null);
                    g.drawImage(ayu2, 448, 50, 168, 301, null);
                    g.drawImage(ayu3, 258, 100, 120, 173, null);

                    // ATRAS
                    g.setFont(fnt2);
                    g.drawRect(EKIA.ANCHO / 2 - 100, 350, 200, 64);
                    rectangle = g.getFontMetrics(fnt2).getStringBounds("atras", g);
                    x = (int) rectangle.getWidth() / 2;
                    y = (64 - (int) rectangle.getHeight()) / 2 + g.getFontMetrics(fnt2).getAscent();
                    g.drawString("atras", EKIA.ANCHO / 2 - x, 350 + y);
                    break;

                case Portada:

                    if (!portSegundaVez) {
                        g.drawImage(image, 0, 0, EKIA.ANCHO, EKIA.ALTO, null);
                    } else {
                        g.drawImage(image2, 32 - 1 * (portadaCont - 465) / 3, 24 - 4 * (portadaCont - 465) / 9, EKIA.ANCHO * 9 / 10 + 2 * (portadaCont - 465) / 3, EKIA.ALTO * 9 / 10 + 8 * (portadaCont - 465) / 9, null);
                    }
                    if (portPrimeraVez) {
                        portPrimeraVez = false;
                        playlistMenu.portCancion();
                        playlistMenu.reproducirCancion();
                    }
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

        // MOUSE X AND Y POSITION
        int mx = e.getX();
        int my = e.getY();

        if (null != EKIA.estadoActual) {
            switch (EKIA.estadoActual) {
                case Portada:

                    if (mouseOver(mx, my, 0, 0, EKIA.ANCHO, EKIA.ALTO)) { // Saltar portada Button
                        Sound clip = new Sound("res/Sonidos/menuShot.mp3");
                        clip.play();
                        portadaAct = false;
                        EKIA.estadoActual = EstadoJuego.Menu;
                        playlistMenu.detenerCancion();
                    }

                    break;

                case Fin:

                    // REVANCHA SOLO SI ESTA OFFLINE
                    if (!EKIA.conectadoOnline) {

                        if (mouseOver(mx, my, EKIA.ANCHO / 2 - 100, 250, 200, 64)) { // Revancha Button
                            handler.object.clear();
                            handler.addObject(new Jugador(50, 200, ID.Jugador1, handler, Color.red, 32, 32));
                            handler.addObject(new Jugador(EKIA.ANCHO - 82, 200, ID.Jugador2, handler, Color.red, 32, 32));
                            EKIA.estadoActual = EstadoJuego.Juego;
                            Sound clip = new Sound("res/Sonidos/menuShot.mp3");
                            clip.play();
                            diseñoMap.diseñar();
                            handler = diseñoMap.getDiseño();
                            return;
                        }

                    }

                    if (mouseOver(mx, my, EKIA.ANCHO / 2 - 100, 350, 200, 64)) { // Menu button
                        System.out.println("Estado del socket: " + conexion);
                        EKIA.estadoActual = EstadoJuego.Menu;
                        terminaConexion();
                        playlistGame.detenerCancion();
                        Sound clip = new Sound("res/Sonidos/menuShot.mp3");
                        clip.play();
                        playlistMenu.elegirCancionAzar();
                        playlistMenu.reproducirCancion();
                        EKIA.errorOnline = false;
                        EKIA.host = false;
                        
                    }
                    break;

                case ErrorOnline:
                    if (mouseOver(mx, my, EKIA.ANCHO / 2 - 100, 350, 200, 64)) { // REGRESAR
                        EKIA.estadoActual = EstadoJuego.Menu;
                        terminaConexion();
                        playlistGame.detenerCancion();
                        Sound clip = new Sound("res/Sonidos/menuShot.mp3");
                        clip.play();
                        playlistMenu.elegirCancionAzar();
                        playlistMenu.reproducirCancion();
                        EKIA.errorOnline = false;
                        EKIA.host = false;
                    }
                    break;

                case Menu:

                    // Si esta en el menu
                    if (mouseOver(mx, my, EKIA.ANCHO / 4 - 100, 150, 200, 64)) { // Play button
                        Sound clip = new Sound("res/Sonidos/menuShot.mp3");
                        clip.play();
                        EKIA.estadoActual = EstadoJuego.SeleccionPersonaje;
                        return;
                    }
                    if (mouseOver(mx, my, EKIA.ANCHO / 4 - 100, 250, 200, 64)) { // Online Button, inicializa la conexion
                        Sound clip = new Sound("res/Sonidos/menuShot.mp3");
                        clip.play();
                        EKIA.conectadoOnline = false;
                        EKIA.estadoActual = EstadoJuego.Online;
                        return;
                    }
                    if (mouseOver(mx, my, EKIA.ANCHO / 4 * 3 - 100, 150, 200, 64)) { // Help Button
                        Sound clip = new Sound("res/Sonidos/menuShot.mp3");
                        clip.play();
                        EKIA.estadoActual = EstadoJuego.Ayuda;
                        return;
                    }
                    if (mouseOver(mx, my, EKIA.ANCHO / 4 * 3 - 100, 250, 200, 64)) { // Quit button
                        Sound clip = new Sound("res/Sonidos/menuShot.mp3");
                        clip.play();
                        try {
                            Thread.sleep(550);
                        } catch (InterruptedException ex) {
                            System.out.println(ex.getMessage());
                        }
                        System.exit(0);
                    }
                    break;

                case SeleccionPersonaje:

                    // Estando en seleccion de personaje que en este caso es seleccion de nivel
                    if (mouseOver(mx, my, EKIA.ANCHO / 2 - 250, EKIA.ALTO - 120, 200, 64)) { // Atras
                        EKIA.estadoActual = EstadoJuego.Menu;
                        Sound clip = new Sound("res/Sonidos/menuShot.mp3");
                        clip.play();
                        return;
                    }
                    if (mouseOver(mx, my, EKIA.ANCHO / 2 + 50, EKIA.ALTO - 120, 200, 64)) { // Aceptar

                        iniciarPartida();
                        return;

                    }
                    if (mouseOver(mx, my, 15, 170, 50, 150)) { // MAPA IZQUIERDA

                        Sound clip = new Sound("res/Sonidos/menuShot.mp3");
                        clip.play();
                        diseñoInt--;
                        if (diseñoInt < 0) {
                            diseñoInt = 2; // Por el momento solo hay tres modos
                        } else if (diseñoInt > 2) {
                            diseñoInt = 0; // Para hacer un loop
                        }
                        return;

                    }
                    if (mouseOver(mx, my, EKIA.ANCHO - 70, 170, 50, 150)) { // MAPA DERECHA

                        Sound clip = new Sound("res/Sonidos/menuShot.mp3");
                        clip.play();
                        diseñoInt++;
                        if (diseñoInt < 0) {
                            diseñoInt = 2; // Por el momento solo hay tres modos
                        } else if (diseñoInt > 2) {
                            diseñoInt = 0; // Para hacer un loop
                        }

                    }
                    break;

                case Ayuda:

                    if (mouseOver(mx, my, EKIA.ANCHO / 2 - 100, 350, 200, 64)) { // REGRESAR
                        Sound clip = new Sound("res/Sonidos/menuShot.mp3");
                        clip.play();
                        EKIA.estadoActual = EstadoJuego.Menu;
                    }
                    break;

                case Online:

                    if (mouseOver(mx, my, EKIA.ANCHO / 4 - 100, 150, 200, 64)) { // HOST
                        Sound clip = new Sound("res/Sonidos/menuShot.mp3");
                        clip.play();
                        nuevaConexion();
                        EKIA.host = true;
                        conexion.start();
                        EKIA.estadoActual = EstadoJuego.Host;
                    }

                    if (mouseOver(mx, my, EKIA.ANCHO / 4 * 3 - 100, 150, 200, 64)) { // CLIENT
                        Sound clip = new Sound("res/Sonidos/menuShot.mp3");
                        clip.play();
                        nuevaConexion();
                        EKIA.host = false;
                        conexion.start();
                        EKIA.estadoActual = EstadoJuego.Client;
                    }

                    if (mouseOver(mx, my, EKIA.ANCHO / 2 - 100, 350, 200, 64)) { // REGRESAR
                        Sound clip = new Sound("res/Sonidos/menuShot.mp3");
                        clip.play();
                        EKIA.estadoActual = EstadoJuego.Menu;
                    }

                    break;

                case Host:
                case Client:
                    if (mouseOver(mx, my, EKIA.ANCHO / 2 - 100, 350, 200, 64)) { // REGRESAR
                        Sound clip = new Sound("res/Sonidos/menuShot.mp3");
                        clip.play();
                        EKIA.estadoActual = EstadoJuego.Online;
                        terminaConexion();
                        EKIA.errorOnline = false;
                        EKIA.host = false;
                    }
                    break;

                default:
                    break;
            }
        }
    }

    // Método a parte para que lo pueda llamar la conexion
    public void iniciarPartida() {

        // PREPARA TODO PARA INICIAR PARTIDA
        m.reiniciar();
        playlistMenu.detenerCancion();
        definirDiseño();
        handler.object.clear();
        EKIA.estadoActual = EstadoJuego.Juego;
        Sound clip = new Sound("res/Sonidos/menuShot.mp3");
        clip.play();
        diseñoMap.setDiseño(diseño); // Para que tenga otro diseño
        diseñoMap.diseñar(); // Se diseña el mapa
        handler = diseñoMap.getDiseño();

        // SI somos el host, vamos a ser el rojo, sino el azul y cambiamos de posicion
        if (EKIA.host || !EKIA.conectadoOnline) {
            handler.addObject(new Jugador(50, 208, ID.Jugador1, handler, Color.red, 32, 32));
            handler.addObject(new Jugador(EKIA.ANCHO - 90, 208, ID.Jugador2, handler, Color.blue, 32, 32));
        } else {
            handler.addObject(new Jugador(50, 208, ID.Jugador2, handler, Color.red, 32, 32));
            handler.addObject(new Jugador(EKIA.ANCHO - 90, 208, ID.Jugador1, handler, Color.blue, 32, 32));
        }

        playlistGame.elegirCancionAzar();

        // PONEMOS LA CANCION
        playlistGame.reproducirCancion();
    }

    private void definirDiseño() { // Esta clase se manda a llamar para cambiar el valor de diseño
        switch (diseñoInt) {
            case 0:
                diseño = DISEÑO.libre;
                break;
            case 1:
                diseño = DISEÑO.intermedio;
                break;
            case 2:
                diseño = DISEÑO.laberinto;
                break;
        }
    }

    private boolean mouseOver(int mx, int my, int x, int y, int widht, int height) {
        if (mx > x && mx < x + widht) {
            if (my > y && my < y + height) {
                return true;
            }
        }
        return false;
    }

    public Font fuente(String nombre, int estilo, float tamanio) {
        Font font;
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, new File(nombre));
        } catch (FontFormatException | IOException ex) {
            System.err.println(nombre + " No se cargo la fuente");
            font = new Font("Arial", Font.PLAIN, 14);
        }
        font = font.deriveFont(estilo, tamanio);
        return font;
    }

    public Conexion getConexion() {
        return conexion;
    }

    public void setConexion(Conexion conexion) {
        this.conexion = conexion;
    }

    // Las 10 particulas del inicio
    private void generarParticulas() {
        handler.object.clear(); // por si acaso
        for (int i = 0; i < 10; i++) {
            float bx = (float) (r.nextInt(EKIA.ANCHO - 40));
            float by = (float) (r.nextInt(EKIA.ALTO - 60));
            Color nuevo = new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255));
            handler.addObject(new Bala(bx, by, ID.BalaMenu, handler, nuevo, 16, 16, r.nextInt(5) - 5, r.nextInt(5) - 5, ID.Jugador1));
        }
    }
}
