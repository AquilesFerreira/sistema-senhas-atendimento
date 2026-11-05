public class Node<E> {
    private E elemento;
    private Node<E> next;

    public Node(E elemento) {
        this.elemento = elemento;
    }

    public E getElemento() {
        return elemento;
    }

    public Node<E> getNext() {
        return next;
    }

    public void setNext(Node<E> next) {
        this.next = next;
    }
}