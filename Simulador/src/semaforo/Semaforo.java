package semaforo;

import cidade.Edge;
import cidade.Node;

import java.util.List;
import java.util.Set;

public class Semaforo {
    private Node node;
    private Edge ruaControlada;  // a rua (aresta) que ele controla!
    private Estado estado;


    public Semaforo(Node node, Edge ruaControlada) {
        this.node = node;
        this.ruaControlada = ruaControlada;
        this.estado = Estado.VERMELHO;
    }

    public enum Estado {
        VERDE, AMARELO, VERMELHO
    }


    public void alternarEstado(int tempo) {
        int ciclo = tempo % 4; // ciclo total de 4 segundos

        if (ciclo == 0 || ciclo == 1) {  // segundos 0 e 1
            estado = Estado.VERDE;
        } else if (ciclo == 2) {          // segundo 2
            estado = Estado.AMARELO;
        } else {                         // segundo 3
            estado = Estado.VERMELHO;
        }
    }


    public static Edge encontrarRuaDisponivel(List<Edge> edges, Node node, Set<Edge> ruasJaControladas) {
        for (Edge e : edges) {
            if ((e.source.equals(node.getId()) || e.target.equals(node.getId())) && !ruasJaControladas.contains(e)) {
                return e;  // Rua disponível
            }
        }
        return null;  // Não há rua disponível
    }


    public Estado getEstado() {
        return estado;
    }


    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public Edge getRuaControlada() {
        return ruaControlada;
    }

    public void setRuaControlada(Edge ruaControlada) {
        this.ruaControlada = ruaControlada;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }
}
