package ekia;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;

public class HUD {

    private final Handler handler;
    private final Cronometro partida;
    private Font fnt, fnt2;

    public HUD(Handler handler, Cronometro partida) {
        this.handler = handler;
        this.partida = partida;
        fnt = fuente("res/square.ttf", 1, 16);
        fnt2 = fuente("res/square.ttf", 1, 10);
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
        g.fillRect(10, 20, (int) vida1, 30);

        float vida2 = (float) (150f * (float) P2.getVida() / 100f);
        vida2 = EKIA.clamp(vida2, 0, 150);
        g.fillRect(EKIA.ANCHO - 30 - (int) vida2, 20, (int) vida2, 30);

        g.setColor(Color.GRAY);

        g.drawRect(10, 20, 150, 30);
        g.drawRect(EKIA.ANCHO - 180, 20, 150, 30);

        g.setFont(fnt2);
        g.drawString("Jugador 1", 10, 70);
        g.drawString("Jugador 2", EKIA.ANCHO - 115, 70);

        Color c;

        // Si somos el cliente o el host se dibuja de manera diferente la vida
        // SI ESTAMOS ONLINE Y SOMOS EL CLIENTE
        if (EKIA.conectadoOnline && !EKIA.host) {

            c = P1.getColor();
            for (int i = 0; i < Matchmaking.vidasP2; i++) {
                g.setColor(c);
                g.fillRect(175 + i * 20, 35, 16, 16);
            }

            c = P2.getColor();
            for (int i = 0; i < Matchmaking.vidasP1; i++) {
                g.setColor(c);
                g.fillRect(EKIA.ANCHO - 205 - i * 20, 35, 16, 16);
            }

        } else {

            // SI ESTAMOS OFFLINE O SOMOS EL HOST
            c = P1.getColor();
            for (int i = 0; i < Matchmaking.vidasP1; i++) {
                g.setColor(c);
                g.fillRect(175 + i * 20, 35, 16, 16);
            }

            c = P2.getColor();
            for (int i = 0; i < Matchmaking.vidasP2; i++) {

                g.setColor(c);
                g.fillRect(EKIA.ANCHO - 205 - i * 20, 35, 16, 16);
            }

        }

        g.setColor(Color.gray);

        if (((Jugador) P1).getSmart() > 0) { // Si si tiene balas smart, dibuja las balas smart del jugador 1, un contador
            g.setColor(Color.gray);
            g.drawString("Smart Bullet: ", 10, 85);
            g.setColor(Color.orange);
            g.fillRect(150, 75, 10, 10);
            g.setColor(Color.white);
            g.drawRect(150, 75, 10, 10);
            String cont = "x " + ((Jugador) P1).getSmart();
            g.drawString(cont, 165, 85);
        }

        if (((Jugador) P2).getSmart() > 0) { // Si si tiene balas smart, dibuja las balas smart del jugador 2, un contador
            g.setColor(Color.gray);
            g.drawString("Smart Bullet: ", EKIA.ANCHO - 200, 85);
            g.setColor(Color.cyan);
            g.fillRect(EKIA.ANCHO - 65, 75, 10, 10);
            g.setColor(Color.white);
            g.drawRect(EKIA.ANCHO - 65, 75, 10, 10);
            String cont = "x " + ((Jugador) P2).getSmart();
            g.drawString(cont, EKIA.ANCHO - 50, 85);
        }

        if (((Jugador) P1).getBomba()) {
            g.setColor(Color.gray);
            g.drawString("Bomba: ", 10, 100);
            g.setColor(Color.white);
            g.fillRect(80, 90, 10, 10);
            g.setColor(Color.gray);
            g.drawRect(80, 90, 10, 10);
        }

        if (((Jugador) P2).getBomba()) {
            g.setColor(Color.gray);
            g.drawString("Bomba: ", EKIA.ANCHO - 110, 100);
            g.setColor(Color.white);
            g.fillRect(EKIA.ANCHO - 40, 90, 10, 10);
            g.setColor(Color.gray);
            g.drawRect(EKIA.ANCHO - 40, 90, 10, 10);
        }

        g.setColor(Color.gray);
        g.drawString("Vidas:", 175, 30);
        g.drawString("Vidas:", EKIA.ANCHO - 245, 30);

        for (int i = 0; i < 3; i++) {
            g.drawRect(175 + i * 20, 35, 16, 16);
            g.drawRect(EKIA.ANCHO - 205 - i * 20, 35, 16, 16);
        }

        g.setColor(Color.white);
        g.drawString("Tiempo :", 260, 25);

        g.setFont(fnt);

        g.drawString(partida.toString(), 250, 45);

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

}
