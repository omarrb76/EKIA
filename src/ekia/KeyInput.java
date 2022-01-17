package ekia;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class KeyInput extends KeyAdapter {

    Handler handler;
    Conexion conexion;
    Random r = new Random(System.nanoTime());
    private boolean[] keyDown1 = new boolean[4]; // para el jugador 1
    private boolean[] keyDown2 = new boolean[4]; // para el jugador 2
    Menu menu;

    public KeyInput(Handler handler, Menu menu) {
        this.handler = handler;
        this.menu = menu;
        for (int i = 0; i < 4; i++) {
            keyDown1[i] = false;
            keyDown2[i] = false;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        try {
            int key = e.getKeyCode();

            if (key == KeyEvent.VK_ESCAPE) {
                System.exit(0); // Se sale del juego
            }
            if (menu.portadaAct) {
                if (key == KeyEvent.VK_S) {
                    menu.portadaCont = 10000;
                }
            }

            if (key == KeyEvent.VK_P) { // Lo pone en pausa únicamente si esta jugando y de manera local
                if (EKIA.estadoActual == EstadoJuego.Juego && !EKIA.conectadoOnline) {
                    EKIA.pausado = !EKIA.pausado; // Cambiamos entre pausa y no
                }
            }

            for (int i = 0; i < handler.object.size(); i++) {

                GameObject tempObject = handler.object.get(i);

                if (tempObject.getId() == ID.Jugador1) {

                    // All the eventes for player one
                    if (key == KeyEvent.VK_W) {
                        tempObject.setVelY(-5);
                        keyDown1[0] = true;
                    }

                    if (key == KeyEvent.VK_S) {
                        tempObject.setVelY(5);
                        keyDown1[1] = true;
                    }

                    if (key == KeyEvent.VK_A) {
                        tempObject.setVelX(-5);
                        keyDown1[2] = true;
                    }

                    if (key == KeyEvent.VK_D) {
                        tempObject.setVelX(5);
                        keyDown1[3] = true;
                    }

                    if (key == KeyEvent.VK_SPACE) {
                        if (Matchmaking.c1.terminado()) { // Si el jugador ya puede disparar otra vez
                            crearBala(tempObject, ID.Bala);
                        }
                    }

                    if (key == KeyEvent.VK_T) {
                        if (Matchmaking.c1.terminado()) {
                            if (((Jugador) tempObject).getSmart() > 0) { // Todavia tiene balas smart
                                crearBala(tempObject, ID.BalaSmart);
                                ((Jugador) tempObject).setBalasSmart(((Jugador) tempObject).getSmart() - 1); // Restamos en uno sus balas smart
                            }
                        }
                    }

                    if (key == KeyEvent.VK_Y) {
                        if (Matchmaking.c1.terminado()) {
                            if (((Jugador) tempObject).getBomba()) { // Si tiene bomba
                                crearBala(tempObject, ID.Bomba);
                                ((Jugador) tempObject).setBomba(false); // Ya se la gasto
                            }
                        }
                    }

                    if (key == KeyEvent.VK_R && EKIA.estadoActual == EstadoJuego.Juego && !EKIA.conectadoOnline) { // Lo mandamos al menu, solo si estamos en el juego y no esta en línea
                        EKIA.pausado = false;
                        handler.object.clear();
                        EKIA.ganador = 0;
                        for (int j = 0; j < 10; j++) {
                            float bx = (float) (r.nextInt(EKIA.ANCHO - 40));
                            float by = (float) (r.nextInt(EKIA.ALTO - 60));
                            Color nuevo = new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255));
                            handler.addObject(new Bala(bx, by, ID.BalaMenu, handler, nuevo, 16, 16, r.nextInt(5) - 5, r.nextInt(5) - 5, ID.Jugador1));
                        }
                        EKIA.estadoActual = EstadoJuego.Fin;
                    }

                } else if (tempObject.getId() == ID.Jugador2 && EKIA.estadoActual == EstadoJuego.Juego && !EKIA.conectadoOnline) {
                    if (key == KeyEvent.VK_NUMPAD8) {
                        tempObject.setVelY(-5);
                        keyDown2[0] = true;
                    }
                    if (key == KeyEvent.VK_NUMPAD5) {
                        tempObject.setVelY(5);
                        keyDown2[1] = true;
                    }
                    if (key == KeyEvent.VK_NUMPAD4) {
                        tempObject.setVelX(-5);
                        keyDown2[2] = true;
                    }
                    if (key == KeyEvent.VK_NUMPAD6) {
                        tempObject.setVelX(5);
                        keyDown2[3] = true;
                    }
                    if (key == KeyEvent.VK_ENTER) {
                        if (Matchmaking.c2.terminado()) { // Si el jugador ya puede disparar otra vez
                            crearBala(tempObject, ID.Bala);
                        }
                    }
                    if (key == KeyEvent.VK_ADD) {
                        if (Matchmaking.c2.terminado()) {
                            if (((Jugador) tempObject).getSmart() > 0) { // Todavia tiene balas smart
                                crearBala(tempObject, ID.BalaSmart);
                                ((Jugador) tempObject).setBalasSmart(((Jugador) tempObject).getSmart() - 1); // Restamos en uno sus balas smart
                            }
                        }
                    }
                    if (key == KeyEvent.VK_L) {
                        if (Matchmaking.c2.terminado()) {
                            if (((Jugador) tempObject).getBomba()) { // Si tiene bomba
                                crearBala(tempObject, ID.Bomba);
                                ((Jugador) tempObject).setBomba(false); // Ya se la gasto
                            }
                        }
                    }
                }
            }

        } catch (Exception ex) {
            System.out.print("");
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        try {
            for (int i = 0; i < handler.object.size(); i++) {
                GameObject tempObject = handler.object.get(i);

                if (tempObject.getId() == ID.Jugador1) {
                    // All the eventes for player one
                    if (key == KeyEvent.VK_W) {
                        keyDown1[0] = false;//tempObject.setVelY(0);
                    }
                    if (key == KeyEvent.VK_S) {
                        keyDown1[1] = false;//tempObject.setVelY(0);
                    }
                    if (key == KeyEvent.VK_A) {
                        keyDown1[2] = false;//tempObject.setVelX(0);
                    }
                    if (key == KeyEvent.VK_D) {
                        keyDown1[3] = false;//tempObject.setVelX(0);
                    }
                    // Verical movement
                    if (!keyDown1[0] && !keyDown1[1]) {
                        tempObject.setVelY(0);
                    }

                    // Horizontal movement
                    if (!keyDown1[2] && !keyDown1[3]) {
                        tempObject.setVelX(0);
                    }

                } else if (tempObject.getId() == ID.Jugador2) {

                    // All the eventes for player TWO
                    if (key == KeyEvent.VK_NUMPAD8) {
                        keyDown2[0] = false;//tempObject.setVelY(0);
                    }
                    if (key == KeyEvent.VK_NUMPAD5) {
                        keyDown2[1] = false;//tempObject.setVelY(0);
                    }
                    if (key == KeyEvent.VK_NUMPAD4) {
                        keyDown2[2] = false;//tempObject.setVelX(0);
                    }
                    if (key == KeyEvent.VK_NUMPAD6) {
                        keyDown2[3] = false;//tempObject.setVelX(0);
                    }
                    // Verical movement
                    if (!keyDown2[0] && !keyDown2[1]) {
                        tempObject.setVelY(0);
                    }

                    // Horizontal movement
                    if (!keyDown2[2] && !keyDown2[3]) {
                        tempObject.setVelX(0);
                    }

                }
            }
        } catch (Exception ex) {
            System.out.print("");
        }
    }

    private void crearBala(GameObject tempObject, ID tipo) {

        if (tempObject.getVelX() != 0 || tempObject.velY != 0 || tipo == ID.BalaSmart || tipo == ID.Bomba) { // Si se esta moviendo el jugador o lo que pone es lo siguiente

            float dirX = 0, dirY = 0;

            if (tempObject.getVelX() != 0) {
                if (tempObject.getVelX() < 0) {
                    dirX = tempObject.getVelX() - 1;
                } else {
                    dirX = tempObject.getVelX() + 1;
                }
            }

            if (tempObject.velY != 0) {
                if (tempObject.getVelY() < 0) {
                    dirY = tempObject.getVelY() - 1;
                } else {
                    dirY = tempObject.getVelY() + 1;
                }
            }

            float x = tempObject.getX() + tempObject.getAncho() / 3;
            float y = tempObject.getY() + tempObject.getAlto() / 3;

            ((Jugador) tempObject).defensaBalaPropia.reiniciar();

            Color color;

            if (tempObject.getId() == ID.Jugador1) {
                if (EKIA.host) {
                    color = Color.orange;
                } else if (EKIA.conectadoOnline) {
                    color = Color.cyan;
                } else {
                    color = Color.orange;
                }
            } else {
                if (EKIA.host) {
                    color = Color.cyan;
                } else if (EKIA.conectadoOnline) {
                    color = Color.orange;
                } else {
                    color = Color.cyan;
                }
            }

            GameObject objeto = null;

            if (null != tipo) {
                switch (tipo) {
                    case BalaSmart:
                        objeto = new BalaSmart(x, y, ID.BalaSmart, handler, color, 10, 10, tempObject.getId());
                        break;
                    case Bala:
                        objeto = new Bala(x, y, ID.Bala, handler, color, 5, 5, dirX, dirY, tempObject.getId());
                        break;
                    case Bomba:
                        objeto = new Bomba(x, y, ID.Bomba, handler, null, 32, 32);
                        break;
                    default:
                        break;
                }
            }

            handler.addObject(objeto);

            // Si esta online y hay una conexion valida
            if (EKIA.conectadoOnline && menu.getConexion() != null) {
                menu.getConexion().addObject(objeto);
            }

            if (tipo != ID.Bomba) {
                Sound clip = new Sound("res/Sonidos/BeamPlayer1.mp3");
                clip.play();
            } else {
                Sound clip = new Sound("res/Sonidos/leftMine.mp3");
                clip.play();
            }

            if (tempObject.getId() == ID.Jugador1) {
                Matchmaking.c1.reiniciar();
            }

            if (tempObject.getId() == ID.Jugador2) {
                Matchmaking.c2.reiniciar();
            }
        }
    }
}
