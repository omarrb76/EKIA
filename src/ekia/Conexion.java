package ekia;

import java.awt.Color;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Conexion extends Thread {

    private Handler handler;
    private Menu menu;
    private boolean running;

    // VARIABLES PARA LOS SOCKETS
    private DatagramSocket socket;
    private InetAddress address;
    private byte[] buf = new byte[64];
    private String ip;
    private int myPort, opponentPort, errores = 0;
    private boolean error = false;
    private boolean listo = false;

    // Objetos que se añaden dinámicamente sobre la red
    // Para seleccion de mapa
    private int diseñoInt = 0; // Diseño del mapa
    private int diseñoIntAnt = 0; // Para detectar si hubo algun cambio en el diseño y mandar el nuevo paquete
    // Para la partida
    private int x = 0, y = 0;
    private Jugador jugador1, jugador2;
    private GameObject objeto = null;

    public Conexion(Handler handler, String ip, int myPort, int opponentPort, Menu menu) {
        this.handler = handler;
        this.ip = ip;
        this.myPort = myPort;
        this.opponentPort = opponentPort;
        this.menu = menu;
    }

    @Override
    public void run() {

        running = true;

        if (!initializeSockets()) {
            return;
        }

        // COMUNICACION INICIAL
        while (running) {
            try {

                // Si tenemos que recibir o mandar paquete primero
                if (EKIA.host) {

                    // Recibimos respuesta
                    buf = new byte[64];
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);
                    String received = new String(packet.getData(), 0, packet.getLength());
                    address = packet.getAddress();
                    System.out.println("Client Address: " + address);

                    // Si el paquete dice listo
                    if (received.contains("listo")) {
                        // Mandamos una respuesta
                        EKIA.conectadoOnline = true;
                        String temp = "listo";
                        buf = temp.getBytes();
                        packet = new DatagramPacket(buf, buf.length, address, opponentPort);
                        socket.send(packet);
                        // Mandamos a elegir el mapa
                        EKIA.conectadoOnline = true;
                        EKIA.estadoActual = EstadoJuego.SeleccionPersonajeOnline;
                        break;
                    }

                } else {

                    // Mandamos el primer mensaje
                    String temp = "listo";
                    buf = temp.getBytes();
                    DatagramPacket packet = new DatagramPacket(buf, buf.length, address, opponentPort);
                    socket.send(packet);

                    // Recibimos respuesta
                    buf = new byte[64];
                    packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);
                    String received = new String(packet.getData(), 0, packet.getLength());

                    // Si el paquete dice listo
                    if (received.contains("listo")) {
                        // Ya estan conectados, mandamos a elegir el mapa
                        EKIA.conectadoOnline = true;
                        EKIA.estadoActual = EstadoJuego.SeleccionPersonajeOnline;
                        break;
                    }
                }

            } catch (IOException ex) {
                // En teoría se hizo un timeout
                // Si el usuario sigue esperando
                //System.out.println("Ya se acabo el tiempo del socket");
                if (EKIA.estadoActual == EstadoJuego.Client) {
                    // Termina el thread
                    EKIA.conectadoOnline = false;
                    EKIA.errorOnline = true;
                    closeSockets();
                    return;
                }
            }
        }

        // COMUNICACION DEL MENU, ELEGIR MAPA
        while (EKIA.estadoActual == EstadoJuego.SeleccionPersonajeOnline) {
            if (!communicationMenu()) {
                break;
            }
            // SI AMBOS JUGADORES ESTAN LISTOS
            if (listo && menu.isListo()) {
                System.out.println("iniciamos la partida");
                break;
            }
        }

        // PARTIDA ONLINE
        if (EKIA.conectadoOnline) {

            menu.iniciarPartida();

            jugador1 = (Jugador) handler.getObject(ID.Jugador1);
            jugador2 = (Jugador) handler.getObject(ID.Jugador2);

            long lastTime = System.nanoTime();
            double amountOfTicks = 60.0;
            double ns = 1000000000 / amountOfTicks;
            double delta = 0;
            while (EKIA.conectadoOnline) {
                long now = System.nanoTime();
                delta += (now - lastTime) / ns;
                lastTime = now;
                while (delta >= 1) {
                    error = !communication();
                    if (error) {
                        break;
                    }
                    delta--;
                }
            }

        }

        closeSockets();

    }

    public boolean getRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void closeSockets() {
        if (socket != null) {
            socket.close();
            socket = null;
            System.out.println("Cerre el socket");
        }
    }

    // Inicializa la conexión
    public boolean initializeSockets() {
        try {
            // Creamos el servidor si no existe
            address = InetAddress.getByName(EKIA.ip);
            System.out.println("Address: " + address.toString());
            socket = new DatagramSocket(myPort);
            socket.setSoTimeout(1000);
            System.out.println("Servidor creado " + myPort);
            return true;
        } catch (SocketException ex) {
            System.out.println("No se pudo crear el servidor");
        } catch (UnknownHostException ex) {
            Logger.getLogger(EKIA.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean communicationMenu() {
        // Detectamos si hubo algun cambio en el diseño
        // MANDAMOS EL PAQUETE
        // Inicialmente el paquete contiene si el jugador esta listo o no
        String temp = "F";
        if (menu.isListo()) {
            temp = "T";
        }

        // SI se cambio le diseño se reemplaza el contenido del paquete
        if (diseñoInt != diseñoIntAnt) {
            temp = "DISENO?" + diseñoInt;
            diseñoIntAnt = diseñoInt;
        }

        buf = temp.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, opponentPort);
        try {
            socket.send(packet);
        } catch (IOException ex) {
            Logger.getLogger(EKIA.class.getName()).log(Level.SEVERE, null, ex);
        }

        // RECIBIMOS EL PAQUETE
        buf = new byte[16];
        packet = new DatagramPacket(buf, buf.length);
        try {

            socket.receive(packet);
            String received = new String(packet.getData(), 0, packet.getLength());

            // Separamos la informacion del archivo
            String[] result = received.split("\\?");

            switch (result[0]) {
                case "DISENO":
                    setDiseñoInt(Integer.parseInt(result[1]));
                    menu.setDiseñoInt(diseñoInt);
                    break;
                case "T":
                    if (!listo) {
                        listo = true;
                        Sound clip = new Sound("res/Sonidos/menuShot.mp3");
                        clip.play();
                    }
                    break;
                case "F":
                    if (listo) {
                        listo = false;
                        Sound clip = new Sound("res/Sonidos/menuShot.mp3");
                        clip.play();
                    }
                    break;
                default:
                    listo = true;
                    break;
            }

            // Reiniciamos los errores
            errores = 0;

        } catch (IOException | NumberFormatException ex) {
            System.out.println("No he recibido paquete");
            errores++;
            // Lo desconectamos sin serror, porque el es el que se salio
            if (!EKIA.conectadoOnline) {
                return false;
            }
            if (errores >= 5 && !EKIA.errorOnline) {
                EKIA.errorOnline = true;
                EKIA.estadoActual = EstadoJuego.ErrorOnline;
                EKIA.estadoAnterior = EstadoJuego.Online;
                EKIA.conectadoOnline = false;
                running = false;
                // Fracaso
                return false;
            }
        }
        return true;
    }

    // Este método se encarga de mandar y recibir los paquetes
    public boolean communication() {

        // MANDAMOS EL PAQUETE
        // LA ESTRUCTURA ES LA SIGUIENTE
        // POSICION DEL JUGADOR: JugadorPosX, JugadorPosY
        // NUEVOS POWERUPS: JugadorPosX, JugadorPosY, ID, posX, posY
        // UNA BALA: JugadorPosX, JugadorPosY, ID, posX, posY, velX, velY
        // UNA BOMBA: JugadorPosX, JugadorPosY, ID, posX, posY, puntosX[8], puntosY[8] // con un total de 21 argumentos
        // del [5] al [12] son dirsX ... del [13] al [21] son dirsY
        String temp = Math.round(jugador1.getX()) + "?" + Math.round(jugador1.getY());
        // Si metemos una bala nueva, tenemos que notificar al otro
        if (objeto != null) {
            temp = temp + "?" + objeto.getId() + "?" + objeto.getX() + "?" + objeto.getY();
            if (objeto instanceof Bala) {
                temp = temp + "?" + ((Bala) objeto).getVelX() + "?" + ((Bala) objeto).getVelY();
            } else if (objeto instanceof Bomba) {
                Bomba bomba = (Bomba) objeto;
                float[] dirsX = bomba.getDirsX();
                float[] dirsY = bomba.getDirsY();
                // Lo hice en dos fors para que el paquete este mejor ordenado
                // Agregamos las direcciones en X
                for (int i = 0; i < 8; i++) {
                    temp = temp + "?" + dirsX[i];
                }
                // Agregamos las direcciones en Y
                for (int i = 0; i < 8; i++) {
                    temp = temp + "?" + dirsY[i];
                }
                System.out.println("Tamaño del paquete: " + temp.getBytes().length);
            }
            objeto = null;
        }
        buf = temp.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, opponentPort);
        try {
            socket.send(packet);
        } catch (IOException ex) {
            Logger.getLogger(EKIA.class.getName()).log(Level.SEVERE, null, ex);
        }

        // RECIBIMOS EL PAQUETE
        buf = new byte[100];
        packet = new DatagramPacket(buf, buf.length);
        try {

            socket.receive(packet);
            String received = new String(packet.getData(), 0, packet.getLength());

            // Separamos la informacion del archivo
            String[] result = received.split("\\?");

            // Conseguimos coordenadas del jugador 2
            x = Integer.parseInt(result[0]);
            y = Integer.parseInt(result[1]);

            // Significa que nos mandaron un objeto
            if (result.length > 2) {
                ID id;
                float posX = Float.parseFloat(result[3]);
                float posY = Float.parseFloat(result[4]);
                switch (result[2]) {
                    case "Botiquin":
                        id = ID.Botiquin;
                        handler.addObject(new Botiquin(posX, posY, id, handler, Color.green, 20, 20));
                        break;
                    case "Escudo":
                        id = ID.Escudo;
                        handler.addObject(new Escudo(posX, posY, id, handler, Color.yellow, 20, 20));
                        break;
                    case "SpawnerSmart":
                        id = ID.SpawnerSmart;
                        handler.addObject(new SpawnerSmart(posX, posY, id, handler, Color.magenta, 20, 20));
                        break;
                    case "BalaSmart":
                        id = ID.BalaSmart;
                        Color c = Color.cyan;
                        if (!EKIA.host) {
                            c = Color.orange;
                        }
                        handler.addObject(new BalaSmart(posX, posY, id, handler, c, 10, 10, ID.Jugador2));
                        int n = jugador2.getSmart();
                        if (n > 0) {
                            n--;
                        }
                        jugador2.setBalasSmart(n);
                        break;
                    case "SpawnerBomba":
                        id = ID.SpawnerBomba;
                        handler.addObject(new BombaSpawner(posX, posY, id, handler, Color.white, 20, 20));
                        break;
                    case "Bomba":
                        id = ID.Bomba;
                        Bomba bomba = new Bomba(posX, posY, id, handler, Color.white, 32, 32);
                        // Direcciones de los perdigones de la bomba
                        float[] dirsX = new float[8];
                        float[] dirsY = new float[8];
                        // Conseguimos las direcciones de los perdigones
                        for (int i = 5, j = 0, x = 13; i < 13; i++, j++, x++) {
                            dirsX[j] = Float.parseFloat(result[i]);
                            dirsY[j] = Float.parseFloat(result[x]);
                        }
                        // Los agregamos a la bomba
                        bomba.setDirsX(dirsX);
                        bomba.setDirsY(dirsY);
                        // Agregamos el objeto e informamos que se lo gasto
                        handler.addObject(bomba);
                        jugador2.setBomba(false); // Ya se la gasto
                        break;
                    case "Bala":
                        float dirX = Float.parseFloat(result[5]);
                        float dirY = Float.parseFloat(result[6]);
                        jugador2.defensaBalaPropia.reiniciar();
                        c = Color.cyan;
                        if (!EKIA.host) {
                            c = Color.orange;
                        }
                        handler.addObject(new Bala(posX, posY, ID.Bala, handler, c, 5, 5, dirX, dirY, ID.Jugador2));
                        break;
                    default:
                        break;
                }
            }

            // Actualizamos posicion del jugador 2
            jugador2.setX(x);
            jugador2.setY(y);

            // Reiniciamos los errores
            errores = 0;

        } catch (IOException | NumberFormatException ex) {
            System.out.println("No he recibido paquete");
            errores++;
            // Lo desconectamos sin serror, porque el es elque se salio
            if (!EKIA.conectadoOnline) {
                return false;
            }
            if (errores >= 5 && !EKIA.errorOnline) {
                EKIA.errorOnline = true;
                if (EKIA.estadoActual == EstadoJuego.Juego) {
                    menu.setPrimeraVezMenu(true);
                }
                EKIA.estadoActual = EstadoJuego.ErrorOnline;
                EKIA.conectadoOnline = false;
                running = false;
                // Fracaso
                return false;
            }
        }

        // Exito
        return true;

    }

    // Indica al juego que tenemos un nuevo objeto, para que se mande en el proximo paquete
    public void addObject(GameObject objeto) {
        this.objeto = objeto;
    }

    public int getDiseñoInt() {
        return diseñoInt;
    }

    public void setDiseñoInt(int diseñoInt) {
        this.diseñoIntAnt = this.diseñoInt;
        this.diseñoInt = diseñoInt;
    }

    public boolean isListo() {
        return listo;
    }

    public void setListo(boolean listo) {
        this.listo = listo;
    }

}
