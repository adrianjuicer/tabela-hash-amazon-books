import java.io.IOException;

// Lê a base e conta as avaliações válidas e as descartadas (RF4)
public class BaseAvaliacoes {
    // Contadores da leitura (RF4)
    int registrosLidos;
    int quantidadePares;        // avaliações válidas
    int descartadosSemUsuario;
    int descartadosSemTitulo;
    int descartadosNotaInvalida;

    // Construtor da base: todos os contadores começam em zero
    public BaseAvaliacoes() {
        this.quantidadePares = 0;
        this.registrosLidos = 0;
        this.descartadosSemUsuario = 0;
        this.descartadosSemTitulo = 0;
        this.descartadosNotaInvalida = 0;
    }

    // Método para ler o arquivo inteiro e contar as avaliações válidas e as descartadas
    void carregar(String caminho) throws IOException {
        LeitorCsv leitor = new LeitorCsv(caminho);
        while (leitor.lerRegistro() == 1) {
            registrosLidos++;
            int nota = leitor.notaEmDecimos();
            if (leitor.tamanhoUsuario == 0) {
                descartadosSemUsuario++;
            } else if (leitor.tamanhoTitulo == 0) {
                descartadosSemTitulo++;
            } else if (nota == -1) {
                descartadosNotaInvalida++;
            } else {
                quantidadePares++;
            }
        }
        leitor.fechar();
    }
}
