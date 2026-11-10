// Tabela hash com encadeamento separado e capacidade fixa (Seção 5.2)
public class TabelaHash {
    Node[] posicoes;
    int capacidade;
    int quantidade;     // quantidade de chaves distintas guardadas
    int chaveNova;      // 1 se a última inserção criou uma chave nova, 0 se ela já existia
    Node ultimoNode;    // nó da chave da última inserção ou busca com sucesso

    // Construtor da tabela: todas as listas começam vazias
    public TabelaHash(int capacidade) {
        this.posicoes = new Node[capacidade];
        this.capacidade = capacidade;
        this.quantidade = 0;
        this.chaveNova = 0;
        this.ultimoNode = null;
    }

    // Método para comparar duas chaves caractere a caractere (1 = iguais, 0 = diferentes)
    static int chavesIguais(char[] chaveA, int tamanhoA, char[] chaveB, int tamanhoB) {
        if (tamanhoA != tamanhoB) {
            return 0;
        }
        for (int i = 0; i < tamanhoA; i++) {
            if (chaveA[i] != chaveB[i]) {
                return 0;
            }
        }
        return 1;
    }

    // Método para inserir a chave; devolve o índice dela (o de antes, se ela já estava na tabela)
    int inserir(char[] chave, int tamanho) {
        int posicao = FuncoesHash.posicao(FuncoesHash.hashFnv1a(chave, tamanho), capacidade);
        Node nodeAtual = posicoes[posicao];
        while (nodeAtual != null) {
            if (chavesIguais(nodeAtual.chave, nodeAtual.tamanho, chave, tamanho) == 1) {
                chaveNova = 0;
                ultimoNode = nodeAtual;
                return nodeAtual.indice;
            }
            nodeAtual = nodeAtual.proximo; // Avança para o próximo nó
        }
        char[] copia = new char[tamanho];
        for (int i = 0; i < tamanho; i++) {
            copia[i] = chave[i];
        }
        Node nodeNovo = new Node(copia, tamanho, quantidade);
        nodeNovo.proximo = posicoes[posicao]; // O novo nó aponta para o antigo início da lista
        posicoes[posicao] = nodeNovo;         // A posição aponta para o novo nó
        quantidade++;
        chaveNova = 1;
        ultimoNode = nodeNovo;
        return nodeNovo.indice;
    }

    // Método para buscar a chave; devolve o índice dela ou -1 se não existir
    int buscar(char[] chave, int tamanho) {
        int posicao = FuncoesHash.posicao(FuncoesHash.hashFnv1a(chave, tamanho), capacidade);
        Node nodeAtual = posicoes[posicao];
        while (nodeAtual != null) {
            if (chavesIguais(nodeAtual.chave, nodeAtual.tamanho, chave, tamanho) == 1) {
                ultimoNode = nodeAtual;
                return nodeAtual.indice;
            }
            nodeAtual = nodeAtual.proximo; // Avança para o próximo nó
        }
        return -1;
    }
}
