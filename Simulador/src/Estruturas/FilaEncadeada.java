package Estruturas;

public class FilaEncadeada<T> {
    private No<T> inicio;
    private No<T> fim;
    private int tamanho;

    public FilaEncadeada() {
        this.inicio = null;
        this.fim = null;
        this.tamanho = 0;
    }

    // Enfileira um elemento no final da fila
    public void enfileirar(T valor) {
        No<T> novoNo = new No<>(valor);

        if (estaVazia()) {
            inicio = novoNo;
        } else {
            fim.setProximo(novoNo);
        }
        fim = novoNo;
        tamanho++;
    }

    public T desenfileirar() {
        if (estaVazia()) {
            throw new IllegalStateException("Fila vazia! Não é possível desenfileirar.");
        }

        T valorRemovido = inicio.getValor();
        inicio = inicio.getProximo();

        if (inicio == null) {
            fim = null;
        }

        tamanho--;
        return valorRemovido;
    }

    public T primeiro() {
        if (estaVazia()) {
            throw new IllegalStateException("Fila vazia! Não há elementos para retornar.");
        }
        return inicio.getValor();
    }

    public boolean estaVazia() {
        return inicio == null;
    }

    public int tamanho() {
        return tamanho;
    }

    public void limpar() {
        inicio = null;
        fim = null;
        tamanho = 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        No<T> atual = inicio;

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

    public boolean contem(T valor) {
        No<T> atual = inicio;

        while (atual != null) {
            if (atual.getValor().equals(valor)) {
                return true;
            }
            atual = atual.getProximo();
        }

        return false;
    }
}