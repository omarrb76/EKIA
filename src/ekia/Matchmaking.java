package ekia;

import java.awt.Color;
import java.util.Random;

// Esta clase va a servir para las rondas en el juego, las vidas de cada jugador, spawnear objetos como botiquines
// Balas, etc... Asimismo puede llamar a un string de cronometro o que tenga uno interno
public class Matchmaking {

    public Conexion[] conexion = null; // Para dar muerte a la conexion cuando se acaba el juego

    private Handler handler;
    private Random r = new Random(System.currentTimeMillis());
    public static int vidasP1 = 3, vidasP2 = 3;
    public Cronometro partida = new Cronometro(180);

    public static enum SPAWN {
        botiquin, escudo, balaSmart, bomba
    }
    public static Cronometro c1 = new Cronometro(0.5f); // Para el jugador 1
    public static Cronometro c2 = new Cronometro(0.5f); // Para el jugador 2
    private double posible = 0, aumento = 1e-80;
    private SPAWN nuevo = null;

    public Matchmaking(Handler handler) {
        this.handler = handler;
    }

    public void tick() {
        // Si estamos en el juego
        if (EKIA.estadoActual == EstadoJuego.Juego) {
            // Si NO estamos en pausa
            if (EKIA.pausado == false) {
                if (partida.tick()) { // Si ya se acabo la partida

                    EKIA.estadoActual = EstadoJuego.Fin;
                    handler.object.clear();

                    // Para que se puedan crear las particulas de fin del juego
                    Menu.primeraVezMenu = true;

                    if (vidasP1 > vidasP2) {
                        EKIA.ganador = 1; // Si el jugador uno tuvo mas vidas que el 2, gana el 1
                    } else if (vidasP1 < vidasP2) {
                        EKIA.ganador = 2;
                    } else {
                        EKIA.ganador = 0;
                    }
                } else { // aun no acaba
                    c1.tick();
                    c2.tick();
                    posible = posible + aumento; // Para que de mas posibilidades
                    // Aqui creamos los nuevos items
                    if (generarNuevo(posible)) {
                        posible = 0;

                        nuevo = getNuevo(nuevo); // Se manda null porque es la primera vez que se ejecuta
                        crearObjeto(nuevo);
                    }

                }
            }
        }
    }

    public Conexion[] getConexion() {
        return conexion;
    }

    public void setConexion(Conexion[] conexion) {
        this.conexion = conexion;
    }

    private boolean generarNuevo(double posible) {
        double res = r.nextInt(1000);
        return res <= posible;
    }

    private SPAWN getNuevo(SPAWN anterior) { // regresara lo nuevo a crear, recibe un anterior para intentar no crear lo mismo dos veces

        SPAWN res = SPAWN.botiquin;
        int n = r.nextInt(100);
        boolean otraVez = false;

        do {
            if (n >= 0 && n < 35) {
                res = SPAWN.botiquin; // El botiquin tiene 35% de probabilidad de salir
            } else if (n >= 35 && n < 50) {
                res = SPAWN.escudo; // El escudo tiene 15% de salir
            } else if (n >= 50 && n < 75) {
                res = SPAWN.balaSmart; // Balas smart 25%
            } else if (n >= 75 && n <= 100) {
                res = SPAWN.bomba; // Bomba 25%
            }
            if (res == anterior) { // Si ya habia salido antes
                if (otraVez) {
                    break; // Si ya lo habia repetido una vez
                }
                otraVez = true;
            }
        } while (otraVez);

        return res;

    }

    private void crearObjeto(SPAWN nuevo) {

        // Solo uno de los dos puede añadir objetos
        if (!EKIA.host) {

            GameObject objeto = null;

            switch (nuevo) {
                case botiquin:
                    objeto = new Botiquin(r.nextInt(EKIA.ANCHO - 40), r.nextInt(EKIA.ALTO - 40), ID.Botiquin, handler, Color.green, 20, 20);
                    break;
                case escudo:
                    objeto = new Escudo(r.nextInt(EKIA.ANCHO - 40), r.nextInt(EKIA.ALTO - 40), ID.Escudo, handler, Color.yellow, 20, 20);
                    break;
                case balaSmart:
                    objeto = new SpawnerSmart(r.nextInt(EKIA.ANCHO - 40), r.nextInt(EKIA.ALTO - 40), ID.SpawnerSmart, handler, Color.magenta, 20, 20);
                    break;
                case bomba:
                    objeto = new BombaSpawner(r.nextInt(EKIA.ANCHO - 40), r.nextInt(EKIA.ALTO - 40), ID.SpawnerBomba, handler, Color.white, 20, 20);
                    break;
            }

            handler.addObject(objeto);
            // Si esta online mandamos esto al oponente
            if (EKIA.conectadoOnline) {
                conexion[0].addObject(objeto);
            }
        }
    }

    public void reiniciar() { // Para las revanchas o empezar un juego nuevo
        vidasP1 = 3;
        vidasP2 = 3;
        partida.reiniciar();
        c1.reiniciar();
        c2.reiniciar();
        nuevo = null;
        posible = 0;
    }

}
