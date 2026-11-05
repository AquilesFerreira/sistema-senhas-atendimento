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
