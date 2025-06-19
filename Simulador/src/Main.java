import Estruturas.No;
import Interface.GrafoViewer;
import cidade.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
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
            FileReader reader = new FileReader("Simulador/src/cidade/JoqueiTeresinaPiauíBrazil.json");
            GraphData graphData = gson.fromJson(reader, GraphData.class);
            reader.close();

            // ---------- 1. Converte nodes e edges para ListaEncadeada ----------
            ListaEncadeada<Node> listaNodes = new ListaEncadeada<>();
            for (Node n : graphData.nodes) {
                listaNodes.adicionar(n);
            }

            ListaEncadeada<Edge> listaEdges = new ListaEncadeada<>();
            for (Edge e : graphData.edges) {
                listaEdges.adicionar(e);
            }



            // ---------- 2. Semáforos ----------
            List<TrafficLight> trafficLights = graphData.trafficLights;
            ListaEncadeada<Semaforo> semaforos = new ListaEncadeada<>();
            Set<Edge> ruasControladas = new HashSet<>();

            final double comprimentoMinimo = 15.0;


            if (trafficLights != null) {
                for (TrafficLight tl : trafficLights) {
                    Node intersecao = encontrarNodeMaisProximo(listaNodes, tl);
                    if (intersecao != null) {
                        No<Edge> atual = listaEdges.getHead();
                        while (atual != null) {
                            Edge rua = atual.getValor();

                            boolean chegaNaIntersecao = rua.target.equals(intersecao.getId());
                            boolean aindaNaoTemSemaforo = !ruasControladas.contains(rua);
                            boolean ruaNaoEMuitoCurta = rua.length >= comprimentoMinimo;

                            if (chegaNaIntersecao && aindaNaoTemSemaforo && ruaNaoEMuitoCurta) {
                                semaforos.adicionar(new Semaforo(intersecao, rua));
                                ruasControladas.add(rua);
                            }

                            atual = atual.getProximo();
                        }
                    }
                }
            }


            AtomicInteger contador = new AtomicInteger(10); // ou só int se não usar lambda


            // ---------- 3. Grafo completo ----------
            Grafo<Node> grafo = new Grafo<>();
            for (Node node : graphData.nodes) {
                grafo.adicionarVertice(node);
            }

            for (Edge edge : graphData.edges) {
                Node sourceNode = getNodeById(listaNodes, edge.source);
                Node targetNode = getNodeById(listaNodes, edge.target);
                if (sourceNode != null && targetNode != null) {
                    grafo.adicionarAresta(edge.length, sourceNode, targetNode);
                }
            }


// ---------- 4. Criação dos carros iniciais ----------
            ListaEncadeada<Carro> carros = new ListaEncadeada<>();
            for (int i = 0; i < 10; i++) {
                Carro carro = new Carro("C" + i, grafo, listaNodes);
                carros.adicionar(carro);
            }


            // ---------- 5. Interface ----------
            GrafoViewer grafoViewer = new GrafoViewer(listaNodes, listaEdges, semaforos);
            grafoViewer.setCarros(carros); // Adiciona os carros ao viewer

            JFrame frame = new JFrame("Simulação de Trânsito");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(grafoViewer);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // ---------- 6. Controladores e Simulador ----------
            ControladorSemaforo controlador = new ControladorSemaforo(semaforos, graphData.nodes, grafoViewer);
            Simulador simulador = new Simulador();
            simulador.registrarListener(grafoViewer);
            simulador.registrarListener(controlador);
            simulador.iniciar();

            // ---------- 7. Atualização suave dos carros ----------
            Timer timerCarros = new Timer(20, e -> {
                No<Carro> atual = carros.getHead();
                No<Carro> anterior = null;

                while (atual != null) {
                    Carro c = atual.getValor();
                    c.atualizar(semaforos);
                    if (c.chegouAoDestino()) {
                        System.out.println("Removendo carro " + c.getId() + " (chegou ao destino)");
                        carros.remover(c);
                        // Reajusta o ponteiro corretamente após remoção
                        if (anterior == null) {
                            atual = carros.getHead(); // recomeça da nova head
                        } else {
                            atual = anterior.getProximo(); // continua após a remoção
                        }
                    } else {
                        anterior = atual;
                        atual = atual.getProximo();
                    }
                }

                grafoViewer.repaint();
            });
            timerCarros.start();

            // ---------- 8. Geração automática de novos carros ----------
            Timer timerGeradorCarros = new Timer(100, e -> {
                int id = contador.getAndIncrement(); // esse é o valor sequencial correto
                Carro novo = new Carro("C" + id, grafo, listaNodes);
                carros.adicionar(novo);
                grafoViewer.setTotalCarrosGerados(id + 1); // atualiza o número visível
                System.out.println("Novo carro gerado: " + novo.getId());
            });

            timerGeradorCarros.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -------------------- Funções auxiliares --------------------

    private static Node getNodeById(ListaEncadeada<Node> nodes, String id) {
        No<Node> atual = nodes.getHead();
        while (atual != null) {
            Node node = atual.getValor();
            if (node.id.equals(id)) {
                return node;
            }
            atual = atual.getProximo();
        }
        return null;
    }


    private static Node encontrarNodeMaisProximo(ListaEncadeada<Node> nodes, TrafficLight tl) {
        Node maisProximo = null;
        double menorDistancia = Double.MAX_VALUE;

        No<Node> atual = nodes.getHead();
        while (atual != null) {
            Node node = atual.getValor();
            double distancia = calcularDistancia(
                    node.getLatitude(), node.getLongitude(),
                    tl.getLatitude(), tl.getLongitude()
            );
            if (distancia < menorDistancia) {
                menorDistancia = distancia;
                maisProximo = node;
            }
            atual = atual.getProximo();
        }

        return maisProximo;
    }


    private static double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        double dLat = lat1 - lat2;
        double dLon = lon1 - lon2;
        return Math.sqrt(dLat * dLat + dLon * dLon);
    }
}
