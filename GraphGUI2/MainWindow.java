import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import javax.swing.*;

enum Mode {
    Creating,
    Deleting,
    Moving,
    Linking,
    None
}

public class MainWindow extends JFrame {

    static Graph graph = new Graph();

    // Режим редактирования.
    static Mode userMode = Mode.None;

    // Окно
    public MainWindow() {
        initUI();
    }

    // TODO: Вынести куда-то эти поля, они отвечают за хранение вершин, которые надо соединить.
    //  (Я не знаю где их по нормальному расположить в коде).
    static Graph.Node firstNode = null;
    static Graph.Node secondNode = null;
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

        creating.addActionListener(e -> userMode = Mode.Creating);

        deleting.addActionListener(e -> userMode = Mode.Deleting);

        moving.addActionListener(e -> userMode = Mode.Moving);

        linking.addActionListener(e -> userMode = Mode.Linking);

        setJMenuBar(jMenuBar);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                switch (userMode) {
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
                        if (firstNode == null) {
                            firstNode = graph.getNodeOnClicked(e.getX(), e.getY());
                        }
                        else if (secondNode == null) {
                            secondNode = graph.getNodeOnClicked(e.getX(), e.getY());
                        }

                        if (firstNode != null && secondNode != null) {
                            graph.addEdge(firstNode.name, secondNode.name);
                            firstNode = secondNode = null;
                        }
                        break;
                }
            }
        });

        // Перемещение вершин.
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (userMode == Mode.Moving) {
                    Graph.Node node = graph.getNodeOnClicked(e.getX(), e.getY());
                    if (node != null) {
                        node.move(e.getX(), e.getY());
                    }
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
                g2d.drawString(node.getValue().name, node.getValue().x - node.getValue().name.length() * 3.5f, node.getValue().y + 5);
            }
        }

        private void drawArrows(Graphics2D g2d) {
            // Выводим рёбра.
            g2d.setColor(Color.BLACK);
            // Установил сглаживание рёбер.
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING , RenderingHints.VALUE_ANTIALIAS_ON);

            for (Map.Entry<String, Graph.Node> node : graph.nodes.entrySet()) {
                for(Graph.Node child : node.getValue().childs) {
                    double angle1 = Math.atan2(child.y - node.getValue().y, child.x - node.getValue().x);
                    double cosx1 = Math.cos(angle1);
                    double siny1 = Math.sin(angle1);
                    double cosx2 = Math.cos(angle1 + Math.PI);
                    double siny2 = Math.sin(angle1 + Math.PI);

                    var fin1x = cosx1 * 10 + node.getValue().x;
                    var fin1y = siny1 * 10 + node.getValue().y;
                    var fin2x = cosx2 * 10 + child.x;
                    var fin2y = siny2 * 10 + child.y;

                    Line2D.Double line = new Line2D.Double(Math.round(fin1x), Math.round(fin1y), Math.round(fin2x), Math.round(fin2y));
                    AffineTransform tx = new AffineTransform();
                    AffineTransform oldTx = g2d.getTransform();
                    Polygon polygon = new Polygon();
                    polygon.addPoint(0,2);
                    polygon.addPoint( -5, -5);
                    polygon.addPoint( 5,-5);

                    double angle = Math.atan2(line.y2 - line.y1, line.x2 - line.x1);
                    tx.translate(line.x2, line.y2);
                    tx.rotate((angle-Math.PI / 2d));
                    g2d.setTransform(tx);
                    g2d.fill(polygon);
                    g2d.setTransform(oldTx);
                    g2d.draw(line);
                }
            }
        }

        private void render(Graphics g) {
            var g2d = (Graphics2D) g;

            drawArrows(g2d);
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

        public void addNode(int x, int y) {
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

        public Node getNodeOnClicked(int x, int y) {

            for (Map.Entry<String, Graph.Node> node : graph.nodes.entrySet()) {
                double currentX = ((double) x - node.getValue().x - 7);
                double currentY = ((double) y - node.getValue().y - 52);
                double currentR = ((double) 20 / 2);

                if ((Math.pow(currentX, 2f)) + (Math.pow(currentY, 2f)) <=  Math.pow(currentR, 2f)) {
                    return node.getValue();
                }
            }

            return null;
        }

        static class Node {

            int x;
            int y;
            String name;

            LinkedList<Node> childs = new LinkedList<>();

            Node (String name, int x, int y) {
                this.name = name;
                this.x = x;
                this.y = y;
            }

            public void addEdge(Node node) {
                childs.add(node);
            }

            public void move(int x, int y) {
                this.x = x - 7;
                this.y = y - 52;
            }

            public void deleteNode(Node node) {
                childs.remove(node);
            }
        }
    }

    public static void main(String[] args) {
        MainWindow mainWindow = new MainWindow();
    }
}