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
    private Node localizacaoAtual;
    private Node destino;
    private FilaEncadeada<Node> rota;
    private int tempoEspera;
    private boolean esperandoSemaforo;

    private double posX, posY;

    private static final int TEMPO_ESPERA = 15;
    private static final double VELOCIDADE = 0.00002;

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
                System.out.println("Carro " + id + " parou no semáforo VERMELHO no nó " + rota.primeiro().getId());
            } else {
                mover();
            }
        }
    }

    private boolean podeAvancar(ListaEncadeada<Semaforo> semaforos) {
        if (rota.estaVazia()) return true;

        Node proximo = rota.primeiro();

        for (int i = 0; i < semaforos.getTamanho(); i++) {
            Semaforo s = semaforos.pegar(i);

            if (s.getNode().equals(proximo)) {
                // Verifica se a rua que conecta o nó atual ao próximo está controlada pelo semáforo
                if (s.getRuaControlada().getTarget().equals(proximo.getId()) &&
                        s.getRuaControlada().getSource().equals(localizacaoAtual.getId())) {
                    return s.getEstado() == Semaforo.Estado.VERDE;
                }

                if (s.getRuaControlada().getSource().equals(proximo.getId()) &&
                        s.getRuaControlada().getTarget().equals(localizacaoAtual.getId())) {
                    return s.getEstado() == Semaforo.Estado.VERDE;
                }
            }
        }

        return true;
    }

    private boolean deveEsperar(ListaEncadeada<Semaforo> semaforos) {
        if (rota.estaVazia()) return false;

        Node proximo = rota.primeiro();
        double distanciaAteProximo = calcularDistancia(posX, posY, proximo.getLongitude(), proximo.getLatitude());

        // Só para se estiver próximo do nó com semáforo vermelho
        if (distanciaAteProximo > 0.009) return false;

        for (int i = 0; i < semaforos.getTamanho(); i++) {
            Semaforo s = semaforos.pegar(i);
            if (s.getNode().equals(proximo) && s.getEstado() == Semaforo.Estado.VERMELHO) {
                // Verifica se o semáforo controla a rua pela qual o carro está entrando no nó
                if (s.getRuaControlada().getTarget().equals(proximo.getId()) &&
                        s.getRuaControlada().getSource().equals(localizacaoAtual.getId())) {
                    return true;
                }

                if (s.getRuaControlada().getSource().equals(proximo.getId()) &&
                        s.getRuaControlada().getTarget().equals(localizacaoAtual.getId())) {
                    return true;
                }
            }
        }

        return false;
    }

    private double calcularDistancia(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        return Math.sqrt(dx * dx + dy * dy);
    }

    private void mover() {
        if (rota.estaVazia()) return;

        Node proximo = rota.primeiro();

        double dirX = proximo.getLongitude() - posX;
        double dirY = proximo.getLatitude() - posY;
        double distancia = Math.sqrt(dirX * dirX + dirY * dirY);

        if (distancia < VELOCIDADE) {
            posX = proximo.getLongitude();
            posY = proximo.getLatitude();
            localizacaoAtual = proximo;
            rota.desenfileirar();
            System.out.println("Carro " + id + " passou pelo nó " + localizacaoAtual.getId());
        } else {
            posX += (dirX / distancia) * VELOCIDADE;
            posY += (dirY / distancia) * VELOCIDADE;
        }
    }

    public boolean chegouAoDestino() {
        return rota.estaVazia() && localizacaoAtual.equals(destino);
    }

    public double getLatitudeAtual() {
        return posY;
    }

    public double getLongitudeAtual() {
        return posX;
    }

    public String getId() {
        return id;
    }

    public Node getLocalizacaoAtual() {
        return localizacaoAtual;
    }

    public FilaEncadeada<Node> getRota() {
        return rota;
    }
}
