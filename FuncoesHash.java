// Função hash FNV1a (Seção 5.1) e o cálculo da posição na tabela
public class FuncoesHash {

    // Método para calcular a FNV1a: XOR com o caractere e multiplica pelo primo FNV
    static int hashFnv1a(char[] chave, int tamanho) {
        int h = 0x811C9DC5; // h0 = 2166136261
        for (int i = 0; i < tamanho; i++) {
            h = h ^ chave[i]; // XOR com o caractere
            h = h * 16777619; // multiplica pelo primo FNV
        }
        return h;
    }

    // Método para transformar o valor hash em uma posição válida do vetor
    static int posicao(int h, int capacidade) {
        return (h & 0x7FFFFFFF) % capacidade; // a máscara zera o bit de sinal
    }
}
