//primeiro entrar é o primeiro a sair
public class Fila<E> {
    private Node<E> inicio, fim;
    private int tamanho;

    //adicionar no fim
    public void enfileirar(E elemento){
        Node<E> novo = new Node<>(elemento);
        if (tamanho == 0){
            inicio = novo;
        }else {
            fim.setNext(novo);
        }
        fim = novo;
        tamanho++;
    }

    //sai o primeiro da fila
    public E desenfileirar(){
        if (tamanho == 0){return null;}
        E removido = inicio.getElemento();
        inicio = inicio.getNext();
        if (inicio == null){
            fim = null;
        }
        tamanho--;
        return removido;
    }

    //Olha o primeiro sem tirar.
    public E espiar() {
        return tamanho == 0 ? null : inicio.getElemento();
    }

    //Olha a posição indicada sem tirar (usado para prever as próximas senhas)
    public E pegar(int posicao){
        if (posicao < 0 || posicao >= tamanho){
            return null;
        }
        Node<E> atual = inicio;
        for (int i = 0; i < posicao; i++) {
            atual = atual.getNext();
        }
        return atual.getElemento();
    }

    //Remove um elemento do meio da fila (desistência)
    public boolean remover(E elemento) {
        if (tamanho == 0){return false;}

        if (inicio.getElemento().equals(elemento)){
            desenfileirar();
            return true;
        }

        Node<E> anterior = inicio;
        while (anterior.getNext() != null){
            Node<E> atual = anterior.getNext();
            if (atual.getElemento().equals(elemento)) {
                if (atual == fim) {
                    fim = anterior;
                }
                anterior.setNext(atual.getNext());
                tamanho--;
                return true;
            }
            anterior = atual;
        }
        return false;
    }

    public int tamanho() {
        return tamanho;
    }

    public boolean estaVazia() {
        return tamanho == 0;
    }

    @Override
    public String toString() {
        String texto = "[";
        Node<E> atual = inicio;
        while (atual != null) {
            texto += atual.getElemento();
            if (atual.getNext() != null) {
                texto += ", ";
            }
            atual = atual.getNext();
        }
        return texto + "]";
    }
}
