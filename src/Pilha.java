//o último a entrar é o primeiro a sair
public class Pilha<E> {
    private Node<E> topo;
    private int tamanho;

    //coloca no topo
    public void empilhar(E elemento) {
        Node<E> novo = new Node<>(elemento);
        novo.setNext(topo);   //sequencia do apontamento é de cima pra baixo
        topo = novo;
        tamanho++;
    }

    //tira do topo
    public E desempilhar() {
        if (topo == null) {
            return null;
        }
        E removido = topo.getElemento();
        topo = topo.getNext();
        tamanho--;
        return removido;
    }

    public E espiar() {
        return topo == null ? null : topo.getElemento();
    }

    public int tamanho() {
        return tamanho;
    }

    public boolean estaVazia() {
        return topo == null;
    }

    //Vetor do topo até a base, sem desempilhar (não altera a pilha)
    public Object[] paraVetor() {
        Object[] vetor = new Object[tamanho];
        Node<E> atual = topo;
        int i = 0;
        while (atual != null) {
            vetor[i] = atual.getElemento();
            i++;
            atual = atual.getNext();
        }
        return vetor;
    }

    @Override
    public String toString() {
        String texto = "[";
        Node<E> atual = topo;
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
