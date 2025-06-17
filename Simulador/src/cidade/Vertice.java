package cidade;

import Estruturas.ListaEncadeada;

public class Vertice <TIPO>{
    private TIPO dado;
    private ListaEncadeada<Aresta<TIPO>> arestaEntrada;
    private ListaEncadeada<Aresta<TIPO>> arestaSaida;

    public Vertice(TIPO valor){
        this.dado = valor;
        this.arestaEntrada = new ListaEncadeada<Aresta<TIPO>>();
        this.arestaSaida = new ListaEncadeada<Aresta<TIPO>>();

    }

    public ListaEncadeada<Aresta<TIPO>> getArestasSaida() {
        return arestaSaida;
    }

    public ListaEncadeada<Aresta<TIPO>> getArestasEntrada() {
        return arestaEntrada;
    }

    public TIPO getDado() {
        return dado;
    }

    public void setDado(TIPO dado) {
        this.dado = dado;
    }
    public void adicionarArestaEntrada(Aresta<TIPO> aresta){
        arestaEntrada.adicionar(aresta);
    }

    public void adicionarArestaSaida(Aresta<TIPO> aresta){
        arestaSaida.adicionar(aresta);
    }

}
