import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

// Trabalho Prático: Tabelas Hash: melhor livro de cada usuário da base Amazon Books Reviews
// Lê os arquivos da pasta em que o programa é executado
public class Principal {

    public static void main(String[] args) throws IOException {
        // Etapa 1: leitura e indexação
        double inicio = System.nanoTime();
        BaseAvaliacoes base = new BaseAvaliacoes();
        base.carregar("Books_rating.csv");
        double tempoLeitura = (System.nanoTime() - inicio) / 1000000.0;
        mostrarLeitura(base);

        // Etapa 2: melhor livro de cada usuário
        inicio = System.nanoTime();
        base.calcularMelhoresLivros();
        double tempoMelhorLivro = (System.nanoTime() - inicio) / 1000000.0;

        // Etapa 3: ordenação da lista de usuários (quicksort) e ranking dos livros
        inicio = System.nanoTime();
        int quantidadeUsuarios = base.tabelaUsuarios.quantidade;
        int[] ordem = new int[quantidadeUsuarios];
        for (int i = 0; i < quantidadeUsuarios; i++) {
            ordem[i] = i;
        }
        Ordenacao.quicksort(ordem, 0, quantidadeUsuarios - 1, base);
        int[] ranking = calcularRanking(base);
        double tempoOrdenacao = (System.nanoTime() - inicio) / 1000000.0;

        // Etapa 4: gravação dos arquivos
        inicio = System.nanoTime();
        gravarUsuarios(base, null, "usuario_melhor_livro.csv");
        gravarUsuarios(base, ordem, "usuario_melhor_livro_ordenado.csv");
        gravarRanking(base, ranking);
        double tempoGravacao = (System.nanoTime() - inicio) / 1000000.0;
        mostrarRanking(base, ranking);

        // Etapa 5: consultas pontuais do arquivo user_id.txt
        inicio = System.nanoTime();
        consultar(base);
        double tempoConsultas = (System.nanoTime() - inicio) / 1000000.0;

        System.out.println();
        System.out.println("=== Tempos (ms) ===");
        System.out.println("Leitura e indexacao:    " + arredondar(tempoLeitura, 2));
        System.out.println("Calculo do melhor livro: " + arredondar(tempoMelhorLivro, 2));
        System.out.println("Ordenacao (quicksort):   " + arredondar(tempoOrdenacao, 2));
        System.out.println("Gravacao dos arquivos:   " + arredondar(tempoGravacao, 2));
        System.out.println("Consultas:               " + arredondar(tempoConsultas, 2));
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

    // Método para escolher os 10 livros de maior média com pelo menos 100 avaliações (seleção)
    static int[] calcularRanking(BaseAvaliacoes base) {
        int[] ranking = new int[10];
        int minimoAvaliacoes = 100; // RF7: só livros com N >= 100
        int quantidadeLivros = base.tabelaTitulos.quantidade;
        int[] escolhido = new int[quantidadeLivros];
        for (int posicao = 0; posicao < 10; posicao++) {
            int melhor = -1;
            for (int i = 0; i < quantidadeLivros; i++) {
                if (escolhido[i] == 0 && base.quantidadeAvaliacoes[i] >= minimoAvaliacoes) {
                    if (melhor == -1 || base.compararLivros(i, melhor) > 0) {
                        melhor = i;
                    }
                }
            }
            ranking[posicao] = melhor;
            if (melhor != -1) {
                escolhido[melhor] = 1;
            }
        }
        return ranking;
    }

    // Método para gravar a lista de usuários; sem vetor de ordem, grava na ordem em que apareceram
    static void gravarUsuarios(BaseAvaliacoes base, int[] ordem, String caminho) throws IOException {
        EscritorSaida saida = new EscritorSaida(caminho);
        saida.texto("User_id,profileName,Title,media_livro,N_livro,nota_usuario");
        saida.fimLinha();
        int quantidadeUsuarios = base.tabelaUsuarios.quantidade;
        for (int i = 0; i < quantidadeUsuarios; i++) {
            int usuario = i;
            if (ordem != null) {
                usuario = ordem[i];
            }
            int livro = base.melhorLivro[usuario];
            saida.campo(base.usuarioChave[usuario], base.usuarioTamanho[usuario]);
            saida.caractere(',');
            saida.campo(base.usuarioNome[usuario], base.usuarioNomeTamanho[usuario]);
            saida.caractere(',');
            saida.campo(base.tituloChave[livro], base.tituloTamanho[livro]);
            saida.caractere(',');
            saida.media(base.somaDecimos[livro], base.quantidadeAvaliacoes[livro]);
            saida.caractere(',');
            saida.inteiro(base.quantidadeAvaliacoes[livro]);
            saida.caractere(',');
            saida.nota(base.notaUsuario[usuario]);
            saida.fimLinha();
        }
        saida.fechar();
    }

    // Método para gravar o ranking dos 10 livros (RF7)
    static void gravarRanking(BaseAvaliacoes base, int[] ranking) throws IOException {
        EscritorSaida saida = new EscritorSaida("ranking_top10_livros.csv");
        saida.texto("posicao,Title,media_livro,N_livro");
        saida.fimLinha();
        for (int posicao = 0; posicao < 10; posicao++) {
            int livro = ranking[posicao];
            if (livro != -1) {
                saida.inteiro(posicao + 1);
                saida.caractere(',');
                saida.campo(base.tituloChave[livro], base.tituloTamanho[livro]);
                saida.caractere(',');
                saida.media(base.somaDecimos[livro], base.quantidadeAvaliacoes[livro]);
                saida.caractere(',');
                saida.inteiro(base.quantidadeAvaliacoes[livro]);
                saida.fimLinha();
            }
        }
        saida.fechar();
    }

    // Método para mostrar o ranking na tela
    static void mostrarRanking(BaseAvaliacoes base, int[] ranking) {
        System.out.println();
        System.out.println("=== Top 10 livros (N >= 100) ===");
        for (int posicao = 0; posicao < 10; posicao++) {
            int livro = ranking[posicao];
            if (livro != -1) {
                System.out.print((posicao + 1) + ". ");
                imprimir(base.tituloChave[livro], base.tituloTamanho[livro]);
                System.out.println(" | media " + arredondar(base.somaDecimos[livro] / (10.0 * base.quantidadeAvaliacoes[livro]), 4)
                        + " | N " + base.quantidadeAvaliacoes[livro]);
            }
        }
    }

    // Método para ler o user_id.txt (um User_id por linha) e mostrar o nome e o melhor livro (RF6)
    static void consultar(BaseAvaliacoes base) throws IOException {
        System.out.println();
        System.out.println("=== Consultas (user_id.txt) ===");
        BufferedReader leitor = new BufferedReader(new InputStreamReader(new FileInputStream("user_id.txt"), "UTF-8"));
        int tamanhoMaximo = 1000; // um User_id tem no máximo 21 caracteres; sobra espaço
        char[] chave = new char[tamanhoMaximo];
        int tamanho = 0;
        int caractere = leitor.read();
        while (caractere != -1) {
            if (caractere == '\n') {
                if (tamanho > 0) {
                    mostrarConsulta(base, chave, tamanho);
                }
                tamanho = 0;
            } else if (caractere != '\r' && caractere != ' ' && tamanho < tamanhoMaximo) {
                chave[tamanho] = (char) caractere;
                tamanho++;
            }
            caractere = leitor.read();
        }
        if (tamanho > 0) {
            mostrarConsulta(base, chave, tamanho); // última linha sem quebra de linha no final
        }
        leitor.close();
    }

    // Método para mostrar o resultado da consulta de um User_id
    static void mostrarConsulta(BaseAvaliacoes base, char[] chave, int tamanho) {
        imprimir(chave, tamanho);
        int usuario = base.tabelaUsuarios.buscar(chave, tamanho);
        if (usuario == -1) {
            System.out.println(" | nao encontrado");
            return;
        }
        int livro = base.melhorLivro[usuario];
        System.out.print(" | ");
        if (base.usuarioNomeTamanho[usuario] == 0) {
            System.out.print("(sem nome)");
        } else {
            imprimir(base.usuarioNome[usuario], base.usuarioNomeTamanho[usuario]);
        }
        System.out.print(" | melhor livro: ");
        imprimir(base.tituloChave[livro], base.tituloTamanho[livro]);
        System.out.println(" | media " + arredondar(base.somaDecimos[livro] / (10.0 * base.quantidadeAvaliacoes[livro]), 4)
                + " | N " + base.quantidadeAvaliacoes[livro] + " | nota do usuario " + (base.notaUsuario[usuario] / 10.0));
    }

    // Método para imprimir um texto guardado em vetor de char
    static void imprimir(char[] texto, int tamanho) {
        for (int i = 0; i < tamanho; i++) {
            System.out.print(texto[i]);
        }
    }

    // Método para arredondar um valor com a quantidade de casas pedida
    static double arredondar(double valor, int casas) {
        double fator = 1;
        for (int i = 0; i < casas; i++) {
            fator = fator * 10;
        }
        return (int) (valor * fator + 0.5) / fator;
    }
}
