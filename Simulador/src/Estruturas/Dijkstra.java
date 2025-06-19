package Estruturas;

import cidade.Grafo;
import cidade.Node;
import cidade.Aresta;
import cidade.Vertice;

import java.util.HashMap;
import java.util.HashSet;

public class Dijkstra {

    public static FilaEncadeada<Node> encontrarMenorCaminho(Grafo<Node> grafo, Node origem, Node destino) {
        HashMap<Node, Integer> distancias = new HashMap<>();
        HashMap<Node, Node> anteriores = new HashMap<>();
        HashSet<Node> visitados = new HashSet<>();

        ListaEncadeada<Vertice<Node>> vertices = grafo.getVertices();
        for (int i = 0; i < vertices.getTamanho(); i++) {
            Node n = vertices.pegar(i).getDado();
            distancias.put(n, Integer.MAX_VALUE);
            anteriores.put(n, null);
        }
        distancias.put(origem, 0);

        while (visitados.size() < vertices.getTamanho()) {
            Node atual = encontrarMenorNode(distancias, visitados);
            if (atual == null) break;
            visitados.add(atual);

            Vertice<Node> verticeAtual = grafo.getVertice(atual);
            ListaEncadeada<Aresta<Node>> adjacentes = verticeAtual.getArestasSaida();

            for (int i = 0; i < adjacentes.getTamanho(); i++) {
                Aresta<Node> aresta = adjacentes.pegar(i);
                Node vizinho = aresta.getFim().getDado();
                if (!visitados.contains(vizinho)) {
                    int novaDist = (int) (distancias.get(atual) + aresta.getPeso());
                    if (novaDist < distancias.get(vizinho)) {
                        distancias.put(vizinho, novaDist);
                        anteriores.put(vizinho, atual);
                    }
                }
            }
        }

        return construirCaminho(anteriores, origem, destino);
    }

    private static Node encontrarMenorNode(HashMap<Node, Integer> distancias, HashSet<Node> visitados) {
        Node menor = null;
        int menorDistancia = Integer.MAX_VALUE;
        for (Node n : distancias.keySet()) {
            if (!visitados.contains(n) && distancias.get(n) < menorDistancia) {
                menor = n;
                menorDistancia = distancias.get(n);
            }
        }
        return menor;
    }

    private static FilaEncadeada<Node> construirCaminho(HashMap<Node, Node> anteriores, Node origem, Node destino) {
        PilhaEncadeada<Node> pilha = new PilhaEncadeada<>();
        Node atual = destino;

        while (atual != null) {
            pilha.empilhar(atual);
            atual = anteriores.get(atual);
        }

        FilaEncadeada<Node> caminho = new FilaEncadeada<>();
        while (!pilha.estaVazia()) {
            caminho.enfileirar(pilha.desempilhar());
        }

        return caminho;
    }
}