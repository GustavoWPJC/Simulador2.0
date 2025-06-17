//package veiculos;
//
//import cidade.*;
//import listener.Listener;
//import Interface.GrafoViewer;
//
//import java.util.*;
//
//public class ControladorCarros implements Listener {
//    private List<Carro> carrosAtivos = new ArrayList<>();
//    private List<Node> nodes;
//    private Grafo<Node> grafo;
//    private GrafoViewer viewer;
//    private Random random = new Random();
//    private int tempoUltimoCarro = 0;
//    private final int intervaloCriacao = 5; // a cada 5 segundos
//
//    public ControladorCarros(List<Node> nodes, Grafo<Node> grafo, GrafoViewer viewer) {
//        this.nodes = nodes;
//        this.grafo = grafo;
//        this.viewer = viewer;
//    }
//
//    @Override
//    public void aoDispararEvento(String tipo, Object dados) {
//        if ("TICK".equals(tipo)) {
//            int tempoAtual = (int) dados;
//
//            // Criar novo carro a cada X segundos
//            if (tempoAtual - tempoUltimoCarro >= intervaloCriacao) {
//                tempoUltimoCarro = tempoAtual;
//                criarCarroAleatorio();
//            }
//
//            // Atualizar carros
//            List<Carro> carrosParaRemover = new ArrayList<>();
//            for (Carro carro : carrosAtivos) {
//                carro.atualizarPosicao();
//
//                if (carro.chegouAoDestino()) {
//                    carrosParaRemover.add(carro);
//                }
//            }
//            carrosAtivos.removeAll(carrosParaRemover);
//
//            viewer.setCarros(carrosAtivos); // atualizar a visualização
//            viewer.repaint();
//        }
//    }
//
//    private void criarCarroAleatorio() {
//        Node origem = nodes.get(random.nextInt(nodes.size()));
//        Node destino = nodes.get(random.nextInt(nodes.size()));
//
//        while (destino.equals(origem)) {
//            destino = nodes.get(random.nextInt(nodes.size()));
//        }
//
//        List<Node> caminho = grafo.dijkstra(origem, destino);
//        if (caminho != null && !caminho.isEmpty()) {
//            Carro carro = new Carro(caminho);
//            carrosAtivos.add(carro);
//        }
//    }
//}
