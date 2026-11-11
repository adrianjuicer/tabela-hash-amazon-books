// Quicksort da lista de usuários (Seção 8.1), com o separar visto em aula
public class Ordenacao {

    // Método para ordenar o vetor de usuários entre as posições inicio e fim
    static void quicksort(int[] vetor, int inicio, int fim, BaseAvaliacoes base) {
        if (inicio < fim) {
            // Pivô do meio levado para o início: evita o pior caso quando o vetor já vem quase ordenado
            int meio = (inicio + fim) / 2;
            int troca = vetor[inicio];
            vetor[inicio] = vetor[meio];
            vetor[meio] = troca;
            int posicaoPivo = separar(vetor, inicio, fim, base);
            quicksort(vetor, inicio, posicaoPivo - 1, base);
            quicksort(vetor, posicaoPivo + 1, fim, base);
        }
    }

    // Método para separar: quem vem antes do pivô vai para a esquerda, quem vem depois para a direita
    static int separar(int[] vetor, int inicio, int fim, BaseAvaliacoes base) {
        int pivo = vetor[inicio];
        int i = inicio + 1;
        int direita = fim;
        while (i <= direita) {
            while (i <= fim && base.compararUsuarios(vetor[i], pivo) <= 0) {
                i = i + 1;
            }
            while (direita > inicio && base.compararUsuarios(vetor[direita], pivo) > 0) {
                direita = direita - 1;
            }
            if (i < direita) {
                int troca = vetor[i];
                vetor[i] = vetor[direita];
                vetor[direita] = troca;
                i = i + 1;
                direita = direita - 1;
            }
        }
        vetor[inicio] = vetor[direita];
        vetor[direita] = pivo;
        return direita;
    }
}
