package Interface;

import cidade.Edge;
import cidade.Node;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import listener.Listener;
import semaforo.Semaforo;
import veiculos.Carro;

public class GrafoViewer extends JPanel implements Listener {
    private List<Node> nodes;
    private List<Edge> edges;
    private List<Semaforo> semaforos;
    private List<Carro> carros = List.of();
    private int totalCarrosGerados = 10;


    private double minLat, maxLat, minLon, maxLon;

    public GrafoViewer(List<Node> nodes, List<Edge> edges, List<Semaforo> semaforos) {
        this.nodes = nodes;
        this.edges = edges;
        this.semaforos = semaforos;

        setPreferredSize(new Dimension(800, 600));

        minLat = Double.MAX_VALUE;
        maxLat = -Double.MAX_VALUE;
        minLon = Double.MAX_VALUE;
        maxLon = -Double.MAX_VALUE;

        for (Node node : nodes) {
            if (node.getLatitude() < minLat) minLat = node.getLatitude();
            if (node.getLatitude() > maxLat) maxLat = node.getLatitude();
            if (node.getLongitude() < minLon) minLon = node.getLongitude();
            if (node.getLongitude() > maxLon) maxLon = node.getLongitude();
        }
    }

    public void setTotalCarrosGerados(int total) {
        this.totalCarrosGerados = total;
    }


    public void setCarros(List<Carro> carros) {
        this.carros = carros;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int offsetX = 20;
        int offsetY = 20;

        double latRange = maxLat - minLat;
        double lonRange = maxLon - minLon;

        // Arestas
        for (Edge edge : edges) {
            Node source = getNodeById(edge.source);
            Node target = getNodeById(edge.target);
            if (source != null && target != null) {
                int x1 = (int)(((source.getLongitude() - minLon) / lonRange) * (panelWidth - 40)) + offsetX;
                int y1 = (int)(((maxLat - source.getLatitude()) / latRange) * (panelHeight - 40)) + offsetY;
                int x2 = (int)(((target.getLongitude() - minLon) / lonRange) * (panelWidth - 40)) + offsetX;
                int y2 = (int)(((maxLat - target.getLatitude()) / latRange) * (panelHeight - 40)) + offsetY;

                g.setColor(Color.GRAY);
                ((Graphics2D) g).setStroke(new BasicStroke(2));
                g.drawLine(x1, y1, x2, y2);
            }
        }

        // Nós
        /*for (Node node : nodes) {
            int x = (int)(((node.getLongitude() - minLon) / lonRange) * (panelWidth - 40)) + offsetX;
            int y = (int)(((node.getLatitude() - minLat) / latRange) * (panelHeight - 40)) + offsetY;

            g.setColor(Color.BLUE);
            g.fillOval(x - 5, y - 5, 10, 10);
            g.setColor(Color.BLACK);
            g.drawString(node.getId(), x + 5, y - 5);
        }*/

        // Semáforos
        for (Semaforo s : semaforos) {
            Edge rua = s.getRuaControlada();
            if (rua != null) {
                Node sourceNode = getNodeById(rua.getSource());
                Node targetNode = getNodeById(rua.getTarget());

                if (sourceNode != null && targetNode != null) {
                    int x1 = offsetX + (int)((sourceNode.getLongitude() - minLon) / lonRange * (panelWidth - 40));
                    int y1 = offsetY + (int)((maxLat - sourceNode.getLatitude()) / latRange * (panelHeight - 40));
                    int x2 = offsetX + (int)((targetNode.getLongitude() - minLon) / lonRange * (panelWidth - 40));
                    int y2 = offsetY + (int)((maxLat - targetNode.getLatitude()) / latRange * (panelHeight - 40));

                    int x = (int)(x2 - 10 * Math.cos(Math.atan2(y2 - y1, x2 - x1)));
                    int y = (int)(y2 - 10 * Math.sin(Math.atan2(y2 - y1, x2 - x1)));

                    Color color;
                    switch (s.getEstado()) {
                        case VERDE: color = Color.GREEN; break;
                        case AMARELO: color = Color.YELLOW; break;
                        case VERMELHO: color = Color.RED; break;
                        default: color = Color.GRAY; break;
                    }

                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setColor(color);
                    g2d.setStroke(new BasicStroke(2));

                    int tamanhoSeta = 15;
                    double angulo = Math.atan2(y2 - y1, x2 - x1);

// início da seta (um pouco recuado da extremidade da rua)
                    int deslocamento = 25;
                    int xInicio = (int)(x2 - deslocamento * Math.cos(angulo));
                    int yInicio = (int)(y2 - deslocamento * Math.sin(angulo));


// fim da seta (apontando para a rua controlada)
                    int xFim = (int)(xInicio + tamanhoSeta * Math.cos(angulo));
                    int yFim = (int)(yInicio + tamanhoSeta * Math.sin(angulo));

// corpo da seta
                    g2d.drawLine(xInicio, yInicio, xFim, yFim);

// cabeça da seta (duas linhas diagonais)
                    int arrowSize = 5;
                    int xArrow1 = (int)(xFim - arrowSize * Math.cos(angulo - Math.PI / 6));
                    int yArrow1 = (int)(yFim - arrowSize * Math.sin(angulo - Math.PI / 6));
                    int xArrow2 = (int)(xFim - arrowSize * Math.cos(angulo + Math.PI / 6));
                    int yArrow2 = (int)(yFim - arrowSize * Math.sin(angulo + Math.PI / 6));

                    g2d.drawLine(xFim, yFim, xArrow1, yArrow1);
                    g2d.drawLine(xFim, yFim, xArrow2, yArrow2);
//teste semaforos seta
                }
            }
        }

        // Carros com posição contínua
        for (Carro carro : carros) {
            double lon = carro.getLongitudeAtual();
            double lat = carro.getLatitudeAtual();

            int x = (int)(((lon - minLon) / lonRange) * (panelWidth - 40)) + offsetX;
            int y = (int)(((maxLat - lat) / latRange) * (panelHeight - 40)) + offsetY;

            g.setColor(Color.ORANGE);
            g.fillRect(x - 5, y - 5, 10, 10);
            g.setColor(Color.BLACK);
            g.drawString(carro.getId(), x + 6, y);
        }


        g.setColor(Color.BLACK);
        g.drawString("Carros gerados: " + totalCarrosGerados, 10, 20);

    }

    private Node getNodeById(String id) {
        for (Node node : nodes) {
            if (node.getId().equals(id)) {
                return node;
            }
        }
        return null;
    }

    @Override
    public void aoDispararEvento(String tipo, Object dados) {
        repaint();
    }
}
