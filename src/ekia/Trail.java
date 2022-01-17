package ekia;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class Trail extends GameObject {

    private float alpha = 1;
    private float life;

    public Trail(float x, float y, ID id, Handler handler, Color color, int ancho, int alto, float life) {
        super(x, y, id, handler, color, ancho, alto);
        this.life = life;
    }

    @Override
    public void tick() {
        if (alpha > life) {
            alpha -= (life - 0.0001f);
        } else {
            handler.removeObject(this);
        }
    }

    @Override
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(makeTransparent(alpha));
        g.setColor(color);
        g.fillRect((int) x, (int) y, ancho, alto);

        g2d.setComposite(makeTransparent(1));
    }

    private AlphaComposite makeTransparent(float alpha) {
        int type = AlphaComposite.SRC_OVER;
        return (AlphaComposite.getInstance(type, alpha));
    }
}
