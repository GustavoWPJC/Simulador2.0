package cidade;

import Estruturas.ListaEncadeada;

public class Grafo <TIPO>{
    private ListaEncadeada<Vertice<TIPO>> vertices;
    private ListaEncadeada<Aresta<TIPO>> arestas;

    public Grafo(){
        this.vertices = new ListaEncadeada<Vertice<TIPO>>();
        this.arestas = new ListaEncadeada<Aresta<TIPO>>();
    }

    public void adicionarVertice(TIPO dado){
        Vertice<TIPO> novoVertice = new Vertice<TIPO>(dado);
        vertices.adicionar(novoVertice);
    }
    public void adicionarAresta(Double peso, TIPO dadoInicio, TIPO dadoFim){
        Vertice<TIPO> inicio = getVertice(dadoInicio);
        Vertice<TIPO> fim = getVertice(dadoFim);
        Aresta<TIPO> aresta = new Aresta<TIPO>(peso, inicio,fim);
        inicio.adicionarArestaSaida(aresta);
        fim.adicionarArestaEntrada(aresta);
        arestas.adicionar(aresta);
    }

    public ListaEncadeada<Aresta<TIPO>> getArestasSaida(Vertice<TIPO> vertice) {
        if (vertice == null) {
            throw new IllegalArgumentException("Vértice não pode ser nulo");
        }
        return vertice.getArestasSaida();
    }

    public Vertice<TIPO> getVertice(TIPO dado){
        Vertice<TIPO> vertice = null;
        for(int i = 0; i < vertices.getTamanho(); i++){
            if(vertices.pegar(i).getDado().equals(dado)){
                vertice = vertices.pegar(i);
                break;
            }
        }
        return vertice;
    }


    public ListaEncadeada<Node> imprimir() {
        System.out.println("Vertices:");
        for (int i = 0; i < vertices.getTamanho(); i++) {
            System.out.println(vertices.pegar(i).getDado());
        }
        return null;
    }

    public ListaEncadeada<Vertice<TIPO>> getVertices() {
        return this.vertices;
    }

    public ListaEncadeada<Aresta<TIPO>> getArestas() {
        return this.arestas;
    }

}
