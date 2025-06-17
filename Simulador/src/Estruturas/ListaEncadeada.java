package Estruturas;

public class ListaEncadeada<TIPO> {
    private No<TIPO> head;
    private No<TIPO> tail;
    private int tamanho;

    public ListaEncadeada() {
        this.tamanho = 0;
    }

    public No<TIPO> getHead() {
        return head;
    }

    public void setHead(No<TIPO> head) {
        this.head = head;
    }

    public No<TIPO> getTail() {
        return tail;
    }

    public void setTail(No<TIPO> tail) {
        this.tail = tail;
    }

    public int getTamanho() {
        return tamanho;
    }

    public void setTamanho(int tamanho) {
        this.tamanho = tamanho;
    }

    public void adicionar(TIPO valor) {
        No<TIPO> novoElemento = new No<>(valor);
        if (head == null) {
            head = novoElemento;
            tail = novoElemento;
        } else {
            tail.setProximo(novoElemento);
            tail = novoElemento;
        }
        tamanho++;
    }
    public void remover(TIPO valorProcurado){
        No <TIPO> anterior = null;
        No <TIPO> atual = head;

        while(atual != null){
            if(atual.getValor().equals(valorProcurado)){
                if(tamanho == 1){
                    head = null;
                    tail = null;
                }else if(atual == head){
                    head = atual.getProximo();
                    atual.setProximo(null);
                }else if(atual == tail) {
                    tail = anterior;
                    anterior.setProximo(null);
                }else{
                    anterior.setProximo(atual.getProximo());
                    atual.setProximo(null);
                }
                tamanho--;
                break;
            }
            anterior = atual;
            atual = atual.getProximo();
        }
    }

    public void adicionarInicio(TIPO valor) {
        No<TIPO> novo = new No<>(valor);
        if (head == null) {
            head = tail = novo;
        } else {
            novo.setProximo(head);
            head = novo;
        }
        tamanho++;
    }

    public void setar(int indice, TIPO novoValor) {
        if (indice < 0 || indice >= tamanho) {
            throw new IndexOutOfBoundsException("Índice inválido");
        }

        No<TIPO> atual = head;
        for (int i = 0; i < indice; i++) {
            atual = atual.getProximo();
        }
        atual.setValor(novoValor);
    }



    public TIPO pegar(int indice) {
        if (indice < 0 || indice >= tamanho) {
            throw new IndexOutOfBoundsException("Índice inválido");
        }

        No<TIPO> atual = head;
        for (int i = 0; i < indice; i++) {
            atual = atual.proximo;
        }
        return atual.valor;
    }

    public void mostrar(int indice) {
        No<TIPO> atual = head;
        while (atual != null){
            System.out.println(atual.getValor());
            atual = atual.getProximo();
        }
    }


    public boolean vazia() {
        return tamanho == 0;
    }
}
