import kotlin.Pair;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.*;

class Circle extends JComponent {
    String name;
    int x;
    int y;
    int size;

    public Circle(String name,int x, int y, int size) {
        this.name = name;
        this.x = x - 7;
        this.y = y - 31;
        this.size = size;
    }
}

public class MainWindow extends JFrame {
    static public Graph graph = new Graph();

    public static void main(String[] args) {
        MainWindow mainWindow = new MainWindow();
    }

    class DrawPanel extends JPanel {

        LinkedList<Circle> circles = new LinkedList<Circle>();

        private void render(Graphics g) {
            var g2d = (Graphics2D) g;
            g2d.setColor(Color.GREEN);

            for (Circle circle : circles) {
                graph.addEdge(new Circle("A",circle.x - circle.size/2, circle.y - circle.size/2,circle.size),new Circle("B",circle.x - circle.size/2 + 100, circle.y - circle.size/2 + 100,circle.size), 3f);

                Ellipse2D ellipse2D = new Ellipse2D.Double(circle.x - circle.size/2, circle.y - circle.size/2, circle.size,  circle.size);
                Ellipse2D ellipse2D1 = new Ellipse2D.Double(circle.x - circle.size/2 + 100, circle.y - circle.size/2 + 100, circle.size,  circle.size);
                graph.addNode("A",circle.x - circle.size/2, circle.y - circle.size/2,circle.size);
                graph.addNode("B",circle.x - circle.size/2 + 100, circle.y - circle.size/2 + 100,circle.size);
                graph.print();
                graph.addEdge(new Circle("A",circle.x - circle.size/2, circle.y - circle.size/2,circle.size),new Circle("B",circle.x - circle.size/2 + 100, circle.y - circle.size/2 + 100,circle.size), 3f);

                g2d.fill(ellipse2D);
                g2d.fill(ellipse2D1);

            }

            repaint();
        }

        @Override
        public void paintComponent(Graphics g) {
            render(g);
        }
    }

    static public class Graph {

        private final HashMap<Circle, java.util.List<Pair<Circle, Float>>> nodes = new HashMap<Circle, java.util.List<Pair<Circle, Float>>>();

        public void addNode(String name, int CoordX, int CoordY, int size) {
            if(!nodes.containsKey(new Circle(name,CoordX,CoordY,size))) {
                System.out.println(name+" x: "+ CoordX + " y: "+ CoordY+" size: "+size);
                nodes.put(new Circle(name,CoordX,CoordY,size), new ArrayList<Pair<Circle, Float>>());
            }
        }

        public void addEdge(Circle firstNode, Circle secondNode, Float distance) {

            nodes.get(firstNode).add(new Pair<Circle, Float>(secondNode, distance));
        }

        public void print() {
            for (Map.Entry<Circle, List<Pair<Circle, Float>>> node : nodes.entrySet()) {
                for (Pair<Circle, Float> pair : node.getValue()) {
                    System.out.println(node.getKey() + "->" + pair.component1().name + " : " + pair.component2());
                }
            }
        }
    }


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
            public void mouseReleased(MouseEvent e) {
                System.out.println(""+e.getX() +" "+ e.getY());
                drawPanel.circles.add(new Circle("A",e.getX(), e.getY(), 50));
            }
        });
    }


}