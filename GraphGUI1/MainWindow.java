import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
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
                        System.out.println(e.getX() + " " + e.getY());
                        graph.addNode(e.getX() - 7, e.getY() - 52);
                        break;
                    case Deleting:
                        Graph.Node node = graph.getNodeOnClicked(e.getX(), e.getY());
                        if (node != null) {
                            System.out.println(node.name);
                            graph.deleteNode(node.name);
                        }
                        break;
                    case Linking:

                        // TODO: создание рёбер
                        break;
                }
            }
        });

        addMouseListener(new MouseAdapter() {
        });

        // Создание панели для отображения графа.
        DrawPanel drawPanel = new DrawPanel();
        add(drawPanel);

        revalidate();
    }

    // Панель для рисования
    static class DrawPanel extends JPanel {

        private void render(Graphics g) {
            var g2d = (Graphics2D) g;

            // Выводим круги.
            g2d.setColor(Color.blue);
            // Установил сглаживание фигур.
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING , RenderingHints.VALUE_ANTIALIAS_ON);

            for (Map.Entry<String, Graph.Node> node : graph.nodes.entrySet()) {
                Ellipse2D ellipse2D = new Ellipse2D.Double(node.getValue().x - 10, node.getValue().y - 10, 20,  20);
                g2d.fill(ellipse2D);
                g2d.draw(ellipse2D);
            }

            // Выводим имена.
            g2d.setColor(Color.white);
            // Установил сглаживание текста.
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            for (Map.Entry<String, Graph.Node> node : graph.nodes.entrySet()) {
                g2d.drawString(node.getValue().name, node.getValue().x - node.getValue().name.length() * 3.5f, node.getValue().y + 5);
            }

            repaint();
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            render(g);
        }
    }

    // Граф
    static public class Graph {

        static int counter = 0;
        private final HashMap<String, Node> nodes = new HashMap<>();

        public void addNode(int x, int y) {
            nodes.put(Integer.toString(counter), new Node(Integer.toString(counter), x, y));
            counter++;
        }

        public void deleteNode(String name) {
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
                System.out.println("Node name: " + this.name);
            }

            public void addEdge(Node node) {
                childs.add(node);
            }

        }
    }

    public static void main(String[] args) {
        MainWindow mainWindow = new MainWindow();
    }
}