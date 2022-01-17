package ekia;

import java.awt.Dimension;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;

public class Window {

    public Window(int width, int height, String title, EKIA game) {
        JFrame frame = new JFrame(title);

        ImageIcon image = new ImageIcon("res/Imagenes/logoEKIA.png");

        frame.setIconImage(image.getImage());

        frame.setPreferredSize(new Dimension(width, height));
        frame.setMaximumSize(new Dimension(width, height));
        frame.setMinimumSize(new Dimension(width, height));

        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.add(game);

        game.start();
    }
}
