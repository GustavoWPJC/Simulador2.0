package semaforo;

import Interface.GrafoViewer;
import cidade.Edge;
import cidade.Node;
import listener.Listener;
import semaforo.Semaforo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ControladorSemaforo implements Listener {
    private List<Semaforo> semaforos;
    private GrafoViewer grafoViewer;
    private Map<String, List<Semaforo>> gruposPorNode;
    private List<Node> nodes; // adicione isso como atributo

    public ControladorSemaforo(List<Semaforo> semaforos, List<Node> nodes, GrafoViewer grafoViewer) {
        this.semaforos = semaforos;
        this.nodes = nodes;
        this.grafoViewer = grafoViewer;
        this.gruposPorNode = agruparPorNode(semaforos);
    }

    @Override
    public void aoDispararEvento(String tipoEvento, Object dados) {
        if (!"TICK".equals(tipoEvento)) return;
        int tempo = (int) dados;

        int faseTotal = 8; // duração do ciclo completo
        int faseAtual = tempo % faseTotal;

        boolean grupoAVerde = faseAtual < 4;
        boolean usandoAmarelo = faseAtual == 3 || faseAtual == 7;

        for (Semaforo s : semaforos) {
            boolean pertenceAoGrupoA = eVertical(s.getRuaControlada());

            if ((pertenceAoGrupoA && grupoAVerde) || (!pertenceAoGrupoA && !grupoAVerde)) {
                if (usandoAmarelo) {
                    s.setEstado(Semaforo.Estado.AMARELO);
                } else {
                    s.setEstado(Semaforo.Estado.VERDE);
                }
            } else {
                s.setEstado(Semaforo.Estado.VERMELHO);
            }
        }

        grafoViewer.repaint();
    }


    private Map<String, List<Semaforo>> agruparPorNode(List<Semaforo> semaforos) {
        Map<String, List<Semaforo>> mapa = new HashMap<>();
        for (Semaforo s : semaforos) {
            String nodeId = s.getNode().getId();
            mapa.computeIfAbsent(nodeId, k -> new ArrayList<>()).add(s);
        }
        return mapa;
    }

    private boolean eVertical(Edge rua) {
        Node origem = getNodeById(rua.getSource());
        Node destino = getNodeById(rua.getTarget());

        if (origem == null || destino == null) return false;

        double dx = Math.abs(destino.getLongitude() - origem.getLongitude());
        double dy = Math.abs(destino.getLatitude() - origem.getLatitude());

        return dy >= dx;
    }



    private Node getNodeById(String id) {
        for (Node n : nodes) {
            if (n.getId().equals(id)) return n;
        }
        return null;
    }

}
