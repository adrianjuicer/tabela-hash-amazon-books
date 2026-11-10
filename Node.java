// Nó da lista encadeada guardada em cada posição da tabela hash
public class Node {
    char[] chave;
    int tamanho;
    int indice;
    Node proximo;

    // Construtor do nó
    public Node(char[] chave, int tamanho, int indice) {
        this.chave = chave;
        this.tamanho = tamanho;
        this.indice = indice;
        this.proximo = null;
    }
}
