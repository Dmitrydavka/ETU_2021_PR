import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.swing.*;

class Circle extends JComponent {
    int x;
    int y;
    int size;

    public Circle(int x, int y, int size) {
        this.x = x;
        this.y = y;
        this.size = size;
    }
}

class DrawPanel extends JPanel {

    LinkedList<Circle> circles = new LinkedList<Circle>();

    private void render(Graphics g) {
        var g2d = (Graphics2D) g;
        g2d.setColor(Color.GREEN);

        for (Circle circle : circles) {
            Ellipse2D ellipse2D = new Ellipse2D.Double(circle.x, circle.y, circle.size,  circle.size);
            g2d.draw(ellipse2D);
        }

        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        render(g);
    }
}

public class MainWindow extends JFrame {

    public MainWindow() {
        initUI();
    }

    private void initUI() {
        var drawPanel = new DrawPanel();
        add(drawPanel);

        setTitle("...");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension appSize = new Dimension(toolkit.getScreenSize().width / 2, toolkit.getScreenSize().height /2 );
        Dimension screenSize = toolkit.getScreenSize();

        setBounds(screenSize.width / 2 - appSize.width / 2, screenSize.height / 2 - appSize.height / 2, appSize.width, appSize.height);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                drawPanel.circles.add(new Circle(e.getX(), e.getY(), 100));
            }
        });
    }

    public static void main(String[] args) {
        MainWindow mainWindow = new MainWindow();
    }
}