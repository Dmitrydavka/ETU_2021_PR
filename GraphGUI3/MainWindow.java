import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import javax.swing.*;

class UserMeta {

    enum Mode {
        Creating,
        Deleting,
        Moving,
        Linking,
        None
    }

    Mode mode = Mode.None;

    Point2D.Double startMousePosition = null;
    Point2D.Double finishMousePosition = null;

    public UserMeta () {
    }
}

public class MainWindow extends JFrame {

    static Graph graph = new Graph();

    private static final UserMeta userMeta = new UserMeta();

    // Окно
    public MainWindow() {
        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension appSize = new Dimension(toolkit.getScreenSize().width / 2, toolkit.getScreenSize().height / 2);
        Dimension screenSize = toolkit.getScreenSize();

        // Устанавливаем размеры окна.
        setBounds(screenSize.width / 2 - appSize.width / 2, screenSize.height / 2 - appSize.height / 2, appSize.width, appSize.height);

        // Cоздаём меню.
        JMenuBar jMenuBar = new JMenuBar();

        JMenu jMenu = new JMenu("Создание");
        jMenuBar.add(jMenu);

        JMenuItem creating = new JMenuItem("Добавление вершин");
        JMenuItem deleting = new JMenuItem("Удаление вершин");
        JMenuItem moving = new JMenuItem("Перемещение вершин");
        JMenuItem linking = new JMenuItem("Добавление рёбер");

        jMenu.add(creating);
        jMenu.add(deleting);
        jMenu.add(moving);
        jMenu.add(linking);

        creating.addActionListener(e -> userMeta.mode = UserMeta.Mode.Creating);
        deleting.addActionListener(e -> userMeta.mode = UserMeta.Mode.Deleting);
        moving.addActionListener(e -> userMeta.mode = UserMeta.Mode.Moving);
        linking.addActionListener(e -> userMeta.mode = UserMeta.Mode.Linking);

        setJMenuBar(jMenuBar);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                switch (userMeta.mode) {
                    case None:
                        break;
                    case Creating:
                        graph.addNode(e.getX() - 7, e.getY() - 52);
                        break;
                    case Deleting:
                        Graph.Node node = graph.getNodeOnClicked(e.getX(), e.getY());
                        if (node != null) {
                            graph.deleteNode(node.name);
                        }
                        break;
                    case Linking:
                        Graph.Node firstNode = graph.getNodeOnClicked(userMeta.startMousePosition.x, userMeta.startMousePosition.y);
                        Graph.Node secondNode = graph.getNodeOnClicked(userMeta.finishMousePosition.x, userMeta.finishMousePosition.y);
                        if (firstNode != null && secondNode != null) {
                            graph.addEdge(firstNode.name, secondNode.name);
                        }
                        userMeta.startMousePosition = null;
                        userMeta.finishMousePosition = null;
                        break;
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                switch (userMeta.mode) {
                    case Creating, Deleting, Moving, None -> {
                    }
                    case Linking -> userMeta.startMousePosition = new Point2D.Double(e.getX(), e.getY());
                }
            }
        });

        // Перемещение вершин.
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                switch (userMeta.mode) {
                    case Moving -> {
                        Graph.Node node = graph.getNodeOnClicked(e.getX(), e.getY());
                        if (node != null) {
                            node.move(e.getX(), e.getY());
                        }
                    }
                    case Linking -> userMeta.finishMousePosition = new Point2D.Double(e.getX(), e.getY());
                }
            }
        });

        // Создание панели для отображения графа.
        DrawPanel drawPanel = new DrawPanel();
        add(drawPanel);

        revalidate();
    }

    // Панель для отображения графа.
    static class DrawPanel extends JPanel {

        private void drawNodes(Graphics2D g2d) {
            // Выводим круги.
            g2d.setColor(Color.blue);
            // Установил сглаживание фигур.
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING , RenderingHints.VALUE_ANTIALIAS_ON);

            for (Map.Entry<String, Graph.Node> node : graph.nodes.entrySet()) {
                Ellipse2D ellipse2D = new Ellipse2D.Double(node.getValue().x - 10, node.getValue().y - 10, 20d,  20d);
                g2d.fill(ellipse2D);
                g2d.draw(ellipse2D);
            }
        }

        private void drawNames(Graphics2D g2d) {
            // Выводим имена.
            g2d.setColor(Color.white);
            // Установил сглаживание текста.
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            for (Map.Entry<String, Graph.Node> node : graph.nodes.entrySet()) {
                g2d.drawString(node.getValue().name, (float) (node.getValue().x - node.getValue().name.length() * 3.5f), (float) (node.getValue().y + 5f));
            }
        }

        private void drawArrow(Graphics2D g2d, Point2D.Double start, Point2D.Double finish, double radius) {
            double angle1 = Math.atan2(finish.getY() - start.getY(), finish.getX() - start.getX());
            double cosx1 = Math.cos(angle1);
            double siny1 = Math.sin(angle1);
            double cosx2 = Math.cos(angle1 + Math.PI);
            double siny2 = Math.sin(angle1 + Math.PI);

            var fin1x = cosx1 * radius + start.getX();
            var fin1y = siny1 * radius + start.getY();
            var fin2x = cosx2 * radius + finish.getX();
            var fin2y = siny2 * radius + finish.getY();

            Line2D.Double line = new Line2D.Double(Math.round(fin1x), Math.round(fin1y), Math.round(fin2x), Math.round(fin2y));
            AffineTransform tx = new AffineTransform();
            AffineTransform oldTx = g2d.getTransform();
            Polygon polygon = new Polygon();
            polygon.addPoint(0, 2);
            polygon.addPoint(-5, -5);
            polygon.addPoint(5, -5);

            double angle = Math.atan2(line.y2 - line.y1, line.x2 - line.x1);
            tx.translate(line.x2, line.y2);
            tx.rotate((angle - Math.PI / 2d));
            g2d.setTransform(tx);
            g2d.fill(polygon);
            g2d.setTransform(oldTx);
            g2d.draw(line);
        }

        private void drawEdges(Graphics2D g2d) {
            // Выводим рёбра.
            g2d.setColor(Color.BLACK);
            // Установил сглаживание рёбер.
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING , RenderingHints.VALUE_ANTIALIAS_ON);

            if (userMeta.startMousePosition != null && userMeta.finishMousePosition != null) {
                drawArrow(g2d, new Point2D.Double(userMeta.startMousePosition.x - 7, userMeta.startMousePosition.y - 52), new Point2D.Double(userMeta.finishMousePosition.x - 7, userMeta.finishMousePosition.y - 52), 0d);
            }

            for (Map.Entry<String, Graph.Node> node : graph.nodes.entrySet()) {
                for(Graph.Node child : node.getValue().childs) {
                    drawArrow(g2d, new Point2D.Double(node.getValue().x, node.getValue().y), new Point2D.Double(child.x, child.y), 10d);
                }
            }
        }

        private void render(Graphics g) {
            var g2d = (Graphics2D) g;

            drawEdges(g2d);
            drawNodes(g2d);
            drawNames(g2d);

            repaint();
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            render(g);
        }
    }

    static public class Graph {

        static int counter = 0;
        private final HashMap<String, Node> nodes = new HashMap<>();

        public void addNode(double x, double y) {
            nodes.put(Integer.toString(counter), new Node(Integer.toString(counter), x, y));
            counter++;
        }

        public void deleteNode(String name) {
            for (Map.Entry<String, Graph.Node> node : graph.nodes.entrySet()) {
                node.getValue().deleteNode(nodes.get(name));
            }
            nodes.remove(name);
        }

        public void addEdge(String firstNode, String secondNode) {
            nodes.get(firstNode).addEdge(nodes.get(secondNode));
        }

        public Node getNodeOnClicked(double x, double y) {

            for (Map.Entry<String, Graph.Node> node : graph.nodes.entrySet()) {
                double currentX = (x - node.getValue().x - 7);
                double currentY = (y - node.getValue().y - 52);
                double currentR = (10.0);

                if ((Math.pow(currentX, 2f)) + (Math.pow(currentY, 2f)) <=  Math.pow(currentR, 2f)) {
                    return node.getValue();
                }
            }

            return null;
        }

        static class Node {

            public double x;
            public double y;
            public String name;

            LinkedList<Node> childs = new LinkedList<>();

            Node (String name, double x, double y) {
                this.name = name;
                this.x = x;
                this.y = y;
            }

            public void addEdge(Node node) {
                childs.add(node);
            }

            public void move(double x, double y) {
                this.x = x - 7;
                this.y = y - 52;
            }

            public void deleteNode(Node node) {
                childs.remove(node);
            }
        }
    }

    public static void main(String[] args) {
        var mainWindow = new MainWindow();
    }
}