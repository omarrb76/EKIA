package ekia;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class HUD {

    private Handler handler;
    private Cronometro partida;

    public HUD(Handler handler, Cronometro partida) {
        this.handler = handler;
        this.partida = partida;
    }

    public void tick() {

    }

    public void render(Graphics g) {
        GameObject P1 = null;
        GameObject P2 = null;

        for (int i = 0; i < handler.object.size(); i++) {
            if (handler.object.get(i).getId() == ID.Jugador1) {
                if (EKIA.host || !EKIA.conectadoOnline) {
                    P1 = handler.object.get(i);
                } else {
                    P2 = handler.object.get(i);
                }
            } else if (handler.object.get(i).getId() == ID.Jugador2) {
                if (EKIA.host || !EKIA.conectadoOnline) {
                    P2 = handler.object.get(i);
                } else {
                    P1 = handler.object.get(i);
                }
            }
        }

        g.setColor(Color.GREEN);

        float vida1 = (float) (150f * (float) P1.getVida() / 100f);
        vida1 = EKIA.clamp(vida1, 0, 150);
        g.fillRect(20, 20, (int) vida1, 30);

        float vida2 = (float) (150f * (float) P2.getVida() / 100f);
        vida2 = EKIA.clamp(vida2, 0, 150);
        g.fillRect(EKIA.ANCHO - 20 - (int) vida2, 20, (int) vida2, 30);

        g.setColor(Color.GRAY);

        g.drawRect(20, 20, 150, 30);
        g.drawRect(EKIA.ANCHO - 170, 20, 150, 30);

        g.drawString("Jugador 1", 20, 70);
        g.drawString("Jugador 2", EKIA.ANCHO - 75, 70);

        Color c;

        // Si somos el cliente o el host se dibuja de manera diferente la vida
        // SI ESTAMOS ONLINE Y SOMOS EL CLIENTE
        if (EKIA.conectadoOnline && !EKIA.host) {

            c = P1.getColor();
            for (int i = 0; i < Matchmaking.vidasP2; i++) {
                g.setColor(c);
                g.fillRect(185 + i * 20, 35, 16, 16);
            }

            c = P2.getColor();
            for (int i = 0; i < Matchmaking.vidasP1; i++) {
                g.setColor(c);
                g.fillRect(EKIA.ANCHO - 195 - i * 20, 35, 16, 16);
            }

        } else {

            // SI ESTAMOS OFFLINE O SOMOS EL HOST
            c = P1.getColor();
            for (int i = 0; i < Matchmaking.vidasP1; i++) {
                g.setColor(c);
                g.fillRect(185 + i * 20, 35, 16, 16);
            }

            c = P2.getColor();
            for (int i = 0; i < Matchmaking.vidasP2; i++) {

                g.setColor(c);
                g.fillRect(EKIA.ANCHO - 195 - i * 20, 35, 16, 16);
            }

        }

        g.setColor(Color.gray);

        if (((Jugador) P1).getSmart() > 0) { // Si si tiene balas smart, dibuja las balas smart del jugador 1, un contador
            g.drawString("Smart Bullet: ", 20, 80);
            g.setColor(Color.orange);
            g.fillRect(95, 70, 10, 10);
            g.setColor(Color.white);
            g.drawRect(95, 70, 10, 10);
            String cont = "x " + ((Jugador) P1).getSmart();
            g.drawString(cont, 110, 80);
        }

        if (((Jugador) P2).getSmart() > 0) { // Si si tiene balas smart, dibuja las balas smart del jugador 2, un contador
            g.drawString("Smart Bullet: ", EKIA.ANCHO - 130, 81);
            g.setColor(Color.cyan);
            g.fillRect(EKIA.ANCHO - 50, 71, 10, 10);
            g.setColor(Color.white);
            g.drawRect(EKIA.ANCHO - 50, 71, 10, 10);
            String cont = "x " + ((Jugador) P2).getSmart();
            g.drawString(cont, EKIA.ANCHO - 35, 80);
        }

        if (((Jugador) P1).getBomba()) {
            g.drawString("Bomba disponible: ", 20, 90);
            g.setColor(Color.white);
            g.fillRect(135, 80, 10, 10);
            g.setColor(Color.gray);
            g.drawRect(135, 80, 10, 10);
        }

        if (((Jugador) P2).getBomba()) {
            g.drawString("Bomba disponible: ", EKIA.ANCHO - 140, 90);
            g.setColor(Color.white);
            g.fillRect(EKIA.ANCHO - 30, 80, 10, 10);
            g.setColor(Color.gray);
            g.drawRect(EKIA.ANCHO - 30, 80, 10, 10);
        }

        g.setColor(Color.gray);
        g.drawString("Vidas:", 185, 30);
        g.drawString("Vidas:", EKIA.ANCHO - 235, 30);

        for (int i = 0; i < 3; i++) {
            g.drawRect(185 + i * 20, 35, 16, 16);
            g.drawRect(EKIA.ANCHO - 195 - i * 20, 35, 16, 16);
        }

        Font fnt = new Font("arial", 1, 30);

        g.setColor(Color.white);
        g.drawString("Tiempo Restante: ", 280, 15);

        g.setFont(fnt);

        g.drawString(partida.toString(), 280, 45);

    }

}
