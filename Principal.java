import java.io.IOException;

// Trabalho Prático: Tabelas Hash: melhor livro de cada usuário da base Amazon Books Reviews
// Lê os arquivos da pasta em que o programa é executado
public class Principal {

    public static void main(String[] args) throws IOException {
        // Etapa 1: leitura e indexação
        BaseAvaliacoes base = new BaseAvaliacoes();
        base.carregar("Books_rating.csv");
        mostrarLeitura(base);
    }

    // Método para mostrar o resumo da leitura (RF4)
    static void mostrarLeitura(BaseAvaliacoes base) {
        int descartados = base.descartadosSemUsuario + base.descartadosSemTitulo + base.descartadosNotaInvalida;
        System.out.println("=== Leitura ===");
        System.out.println("Registros lidos:          " + base.registrosLidos);
        System.out.println("Avaliacoes validas:       " + base.quantidadePares);
        System.out.println("Descartados sem User_id:  " + base.descartadosSemUsuario);
        System.out.println("Descartados sem Title:    " + base.descartadosSemTitulo);
        System.out.println("Descartados nota invalida: " + base.descartadosNotaInvalida);
        System.out.println("Total descartado:         " + descartados);
        System.out.println("Usuarios distintos:       " + base.tabelaUsuarios.quantidade);
        System.out.println("Livros distintos:         " + base.tabelaTitulos.quantidade);
    }
}
