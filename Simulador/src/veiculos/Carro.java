package veiculos;

import Estruturas.Dijkstra;
import Estruturas.FilaEncadeada;
import Estruturas.ListaEncadeada;
import cidade.Grafo;
import cidade.Node;
import semaforo.Semaforo;

import java.util.Random;

public class Carro {
    private final String id;
    private Node localizacaoAtual; // nó atual (para referência)
    private Node destino;
    private FilaEncadeada<Node> rota;
    private int tempoEspera;
    private boolean esperandoSemaforo;

    // Posição contínua do carro (latitude e longitude)
    private double posX, posY;

    private static final int TEMPO_ESPERA = 15;
    private static final double VELOCIDADE = 0.00002; // ajuste essa velocidade para o movimento

    public Carro(String id, Grafo<Node> grafo, ListaEncadeada<Node> nodes) {
        this.id = id;
        this.esperandoSemaforo = false;
        this.tempoEspera = 0;
        selecionarRotaAleatoria(grafo, nodes);
        this.posX = localizacaoAtual.getLongitude();
        this.posY = localizacaoAtual.getLatitude();
    }

    private void selecionarRotaAleatoria(Grafo<Node> grafo, ListaEncadeada<Node> nodes) {
        Random rand = new Random();
        this.localizacaoAtual = nodes.pegar(rand.nextInt(nodes.getTamanho()));
        do {
            this.destino = nodes.pegar(rand.nextInt(nodes.getTamanho()));
        } while (destino.equals(localizacaoAtual));

        this.rota = Dijkstra.encontrarMenorCaminho(grafo, localizacaoAtual, destino);
        if (!rota.estaVazia() && rota.primeiro().equals(localizacaoAtual)) {
            rota.desenfileirar();
        }
    }

    public void atualizar(ListaEncadeada<Semaforo> semaforos) {
        if (rota.estaVazia()) return;

        if (esperandoSemaforo) {
            tempoEspera++;
            if (tempoEspera >= TEMPO_ESPERA || podeAvancar(semaforos)) {
                esperandoSemaforo = false;
                tempoEspera = 0;
                mover();
            }
        } else {
            if (deveEsperar(semaforos)) {
                esperandoSemaforo = true;
            } else {
                mover();
            }
        }
    }

    private boolean podeAvancar(ListaEncadeada<Semaforo> semaforos) {
        Node proximo = rota.primeiro();
        for (int i = 0; i < semaforos.getTamanho(); i++) {
            Semaforo s = semaforos.pegar(i);
            if (s.getNode().equals(proximo)) {
                return s.getEstado() == Semaforo.Estado.VERDE;
            }
        }
        return true;
    }

    private boolean deveEsperar(ListaEncadeada<Semaforo> semaforos) {
        Node proximo = rota.primeiro();
        for (int i = 0; i < semaforos.getTamanho(); i++) {
            Semaforo s = semaforos.pegar(i);
            if (s.getNode().equals(proximo) && s.getEstado() == Semaforo.Estado.VERMELHO) {
                return true;
            }
        }
        return false;
    }

    private void mover() {
        if (rota.estaVazia()) return;

        Node proximo = rota.primeiro();

        // Vetor direção para o próximo nó
        double dirX = proximo.getLongitude() - posX;
        double dirY = proximo.getLatitude() - posY;
        double distancia = Math.sqrt(dirX * dirX + dirY * dirY);

        if (distancia < VELOCIDADE) {
            // Chegou perto do próximo nó: atualiza posição e remove da rota
            posX = proximo.getLongitude();
            posY = proximo.getLatitude();
            localizacaoAtual = proximo;
            rota.desenfileirar();
            System.out.println("Carro " + id + " chegou no nó: " + localizacaoAtual.getId());
        } else {
            // Move uma fração na direção do próximo nó
            posX += (dirX / distancia) * VELOCIDADE;
            posY += (dirY / distancia) * VELOCIDADE;
        }
    }

    public boolean chegouAoDestino(){
        return rota.estaVazia() && localizacaoAtual.equals(destino);
    }

    public double getLatitudeAtual() {
        return posY;
    }

    public double getLongitudeAtual() {
        return posX;
    }

    public String getId() { return id; }
    public Node getLocalizacaoAtual() { return localizacaoAtual; }
    public FilaEncadeada<Node> getRota() { return rota; }
}
