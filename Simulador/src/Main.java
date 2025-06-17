import Interface.GrafoViewer;
import cidade.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileReader;
import java.util.*;
import javax.swing.*;
import javax.swing.Timer;

import com.google.gson.Gson;
import Estruturas.ListaEncadeada;
import semaforo.ControladorSemaforo;
import semaforo.Semaforo;
import veiculos.Carro;

public class Main {
    public static void main(String[] args) {
        try {
            Gson gson = new Gson();
            FileReader reader = new FileReader("Simulador/src/cidade/CentroTeresinaPiauíBrazil.json");
            GraphData graphData = gson.fromJson(reader, GraphData.class);
            reader.close();

            // ---------- 1. Semáforos ----------
            List<TrafficLight> trafficLights = graphData.trafficLights;
            List<Semaforo> semaforos = new ArrayList<>();
            Set<Edge> ruasJaControladas = new HashSet<>();

            if (trafficLights != null) {
                for (TrafficLight tl : trafficLights) {
                    Node nodeMaisProximo = encontrarNodeMaisProximo(graphData.nodes, tl);
                    if (nodeMaisProximo != null) {
                        Edge ruaDisponivel = Semaforo.encontrarRuaDisponivel(graphData.edges, nodeMaisProximo, ruasJaControladas);
                        if (ruaDisponivel != null) {
                            semaforos.add(new Semaforo(nodeMaisProximo, ruaDisponivel));
                            ruasJaControladas.add(ruaDisponivel);
                        } else {
                            System.out.println("Não há ruas disponíveis para semáforo no nó: " + nodeMaisProximo.getId());
                        }
                    }
                }
            }

            // ---------- 2. Grafo completo ----------
            Grafo<Node> grafo = new Grafo<>();
            for (Node node : graphData.nodes) {
                grafo.adicionarVertice(node);
            }
            for (Edge edge : graphData.edges) {
                Node sourceNode = getNodeById(graphData.nodes, edge.source);
                Node targetNode = getNodeById(graphData.nodes, edge.target);
                if (sourceNode != null && targetNode != null) {
                    grafo.adicionarAresta(edge.length, sourceNode, targetNode);
                }
            }

            // ---------- 3. Converte nodes e semáforos para ListaEncadeada ----------
            ListaEncadeada<Node> listaNodes = new ListaEncadeada<>();
            for (Node n : graphData.nodes) {
                listaNodes.adicionar(n);
            }

            ListaEncadeada<Semaforo> listaSemaforos = new ListaEncadeada<>();
            for (Semaforo s : semaforos) {
                listaSemaforos.adicionar(s);
            }

            // ---------- 4. Criação dos carros iniciais ----------
            List<Carro> carros = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                Carro carro = new Carro("C" + i, grafo, listaNodes);
                carros.add(carro);
            }

            // ---------- 5. Interface ----------
            GrafoViewer grafoViewer = new GrafoViewer(graphData.nodes, graphData.edges, semaforos);
            grafoViewer.setCarros(carros); // Adiciona os carros ao viewer

            JFrame frame = new JFrame("Simulação de Trânsito");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(grafoViewer);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // ---------- 6. Controladores e Simulador ----------
            ControladorSemaforo controlador = new ControladorSemaforo(semaforos, grafoViewer);
            Simulador simulador = new Simulador();
            simulador.registrarListener(grafoViewer);
            simulador.registrarListener(controlador);
            simulador.iniciar();

            // ---------- 7. Atualização suave dos carros ----------
            Timer timerCarros = new Timer(20, e -> {
                for (int i = 0; i < carros.size(); i++) {
                    Carro c = carros.get(i);
                    c.atualizar(listaSemaforos);

                    if (c.chegouAoDestino()) {
                        System.out.println("Removendo carro " + c.getId() + " (chegou ao destino)");
                        carros.remove(i);
                        i--; // Corrige o índice após remoção
                    }
                }
                grafoViewer.repaint(); // redesenha com novas posições
            });
            timerCarros.start();

            // ---------- 8. Geração automática de novos carros ----------
            Timer timerGeradorCarros = new Timer(1000, new ActionListener() {
                int contador = 10; // começa depois dos 10 iniciais

                @Override
                public void actionPerformed(ActionEvent e) {
                    Carro novo = new Carro("C" + contador++, grafo, listaNodes);
                    carros.add(novo);
                    System.out.println("Novo carro gerado: " + novo.getId());
                }
            });
            timerGeradorCarros.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -------------------- Funções auxiliares --------------------

    private static Node getNodeById(List<Node> nodes, String id) {
        for (Node node : nodes) {
            if (node.id.equals(id)) {
                return node;
            }
        }
        return null;
    }

    private static Node encontrarNodeMaisProximo(List<Node> nodes, TrafficLight tl) {
        Node maisProximo = null;
        double menorDistancia = Double.MAX_VALUE;

        for (Node node : nodes) {
            double distancia = calcularDistancia(node.getLatitude(), node.getLongitude(),
                    tl.getLatitude(), tl.getLongitude());
            if (distancia < menorDistancia) {
                menorDistancia = distancia;
                maisProximo = node;
            }
        }
        return maisProximo;
    }

    private static double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        double dLat = lat1 - lat2;
        double dLon = lon1 - lon2;
        return Math.sqrt(dLat * dLat + dLon * dLon);
    }
}
