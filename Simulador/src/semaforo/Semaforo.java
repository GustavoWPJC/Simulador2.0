package semaforo;

import cidade.Edge;
import cidade.Node;

import java.util.List;
import java.util.Set;

public class Semaforo {
    private Node node;
    private Edge ruaControlada;
    private Estado estado;

    public Semaforo(Node node, Edge ruaControlada) {
        this.node = node;
        this.ruaControlada = ruaControlada;
        this.estado = Estado.VERMELHO;
    }

    public enum Estado {
        VERDE, AMARELO, VERMELHO
    }

    public static Edge encontrarRuaPorDirecaoPreferencial(List<Edge> edges, Node node,
                                                          Set<Edge> ruasJaControladas,
                                                          String direcaoDesejada,
                                                          List<Node> todosOsNodes) {
        double alvo = direcaoEmGraus(direcaoDesejada);
        if (alvo < 0) return encontrarRuaDisponivel(edges, node, ruasJaControladas); // fallback

        Edge melhorCandidata = null;
        double menorDiferenca = 360;

        for (Edge e : edges) {
            if (!ruasJaControladas.contains(e) &&
                    (e.source.equals(node.getId()) || e.target.equals(node.getId()))) {

                Node outro = e.source.equals(node.getId()) ?
                        getNodeById(todosOsNodes, e.target) :
                        getNodeById(todosOsNodes, e.source);

                if (outro == null) continue;

                double angulo = calcularDirecao(node, outro);
                double diff = Math.abs(angulo - alvo);
                if (diff > 180) diff = 360 - diff;

                if (diff < menorDiferenca) {
                    menorDiferenca = diff;
                    melhorCandidata = e;
                }
            }
        }
        return melhorCandidata;
    }

    private static double calcularDirecao(Node a, Node b) {
        double dx = b.getLongitude() - a.getLongitude();
        double dy = b.getLatitude() - a.getLatitude();
        double angulo = Math.toDegrees(Math.atan2(dy, dx));
        return (angulo + 360) % 360;
    }

    private static double direcaoEmGraus(String dir) {
        if (dir == null) return -1;
        switch (dir.toLowerCase()) {
            case "north": return 0;
            case "east": return 90;
            case "south": return 180;
            case "west": return 270;
            case "forward": return -1; // Ignorado por enquanto
            default: return -1;
        }
    }

    private static Node getNodeById(List<Node> nodes, String id) {
        for (Node node : nodes) {
            if (node.id.equals(id)) {
                return node;
            }
        }
        return null;
    }

    public static Edge encontrarRuaDisponivel(List<Edge> edges, Node node, Set<Edge> ruasJaControladas) {
        for (Edge e : edges) {
            if ((e.source.equals(node.getId()) || e.target.equals(node.getId())) &&
                    !ruasJaControladas.contains(e)) {
                return e;
            }
        }
        return null;
    }

    // Getters e setters
    public Estado getEstado() { return estado; }
    public Node getNode() { return node; }
    public void setNode(Node node) { this.node = node; }
    public Edge getRuaControlada() { return ruaControlada; }
    public void setRuaControlada(Edge ruaControlada) { this.ruaControlada = ruaControlada; }
    public void setEstado(Estado estado) { this.estado = estado; }
}
