package Estruturas;

public class PilhaEncadeada<T> {
    private No<T> topo;
    private int tamanho;

    public PilhaEncadeada() {
        this.topo = null;
        this.tamanho = 0;
    }

    public void empilhar(T valor) {
        No<T> novoNo = new No<>(valor);
        novoNo.setProximo(topo);
        topo = novoNo;
        tamanho++;
    }

    public T desempilhar() {
        if (estaVazia()) {
            throw new IllegalStateException("Pilha vazia! Não é possível desempilhar.");
        }

        T valorRemovido = topo.getValor();
        topo = topo.getProximo();
        tamanho--;
        return valorRemovido;
    }

    // Retorna o elemento no topo sem remover
    public T topo() {
        if (estaVazia()) {
            throw new IllegalStateException("Pilha vazia! Não há elementos para retornar.");
        }
        return topo.getValor();
    }

    public boolean estaVazia() {
        return topo == null;
    }

    public int tamanho() {
        return tamanho;
    }

    public void limpar() {
        topo = null;
        tamanho = 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        No<T> atual = topo;

        while (atual != null) {
            sb.append(atual.getValor());
            if (atual.getProximo() != null) {
                sb.append(", ");
            }
            atual = atual.getProximo();
        }

        sb.append("]");
        return sb.toString();
    }
}