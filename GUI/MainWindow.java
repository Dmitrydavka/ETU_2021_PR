import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import javax.swing.*;

public class MainWindow extends JFrame {

    static Graph graph = new Graph();
    static int counter = 0;

    public MainWindow() {
        initUI();
    }

    private void initUI() {
        DrawPanel drawPanel = new DrawPanel();
        add(drawPanel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension appSize = new Dimension(toolkit.getScreenSize().width / 2, toolkit.getScreenSize().height / 2);
        Dimension screenSize = toolkit.getScreenSize();

        setBounds(screenSize.width / 2 - appSize.width / 2, screenSize.height / 2 - appSize.height / 2, appSize.width, appSize.height);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                graph.addNode(Integer.toString(counter++), e.getX(), e.getY());
            }
        });
    }

    public static void main(String[] args) {
        MainWindow mainWindow = new MainWindow();

        graph.addNode("a", 100, 50);
        graph.addNode("b", 100, 100);
        graph.addNode("c", 50, 100);
        graph.addNode("d", 50, 50);

        graph.addEdge("a", "b");
        graph.addEdge("b", "c");
        graph.addEdge("c", "d");
        graph.addEdge("d", "b");
        graph.addEdge("a", "c");
        graph.print();
    }

    class DrawPanel extends JPanel {

        private void render(Graphics g) {

            var g2d = (Graphics2D) g;
            g2d.setColor(Color.GREEN);

            for (Map.Entry<String, Graph.Node> node : graph.nodes.entrySet()) {
                Ellipse2D ellipse2D = new Ellipse2D.Double(node.getValue().x - 25, node.getValue().y - 25, 50,  50);
                g2d.fill(ellipse2D);
                g2d.draw(ellipse2D);
            }

            repaint();
        }

        @Override
        public void paintComponent(Graphics g) {
            render(g);
        }
    }

    static public class Graph {

        private HashMap<String, Node> nodes = new HashMap<String, Node>();

        public void addNode(String name, int x, int y) {
            if(!nodes.containsKey(name)) {
                nodes.put(name, new Node(name, x, y));
            }
        }

        public void addEdge(Node firstNode, Node secondNode) {
            nodes.get(firstNode).addEdge(secondNode);
        }

        public void addEdge(String firstNode, String secondNode) {
            nodes.get(firstNode).addEdge(nodes.get(secondNode));
        }

        public void print() {
            for (Map.Entry<String, Node> node : nodes.entrySet()) {
                node.getValue().print();
            }
        }

        class Node {

            int x;
            int y;
            String name;
            LinkedList<Node> childs = new LinkedList<Node>();

            Node (String name, int x, int y) {
                this.name = name;
                this.x = x;
                this.y = y;
            }

            public void addEdge(Node node) {
                childs.add(node);
            }

            public void print() {
                for (Node child : childs) {
                    System.out.println(this.name + "->" + child.name);
                }
            }
        }
    }
}