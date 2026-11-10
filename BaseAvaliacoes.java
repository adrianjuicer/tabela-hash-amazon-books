import java.io.IOException;

// Guarda os livros e os usuários da base em duas tabelas hash
public class BaseAvaliacoes {
    TabelaHash tabelaTitulos;
    TabelaHash tabelaUsuarios;

    // Livros: a posição no vetor é o índice do título na tabelaTitulos
    char[][] tituloChave;
    int[] tituloTamanho;
    int[] somaDecimos;          // soma das notas em décimos (5.0 vira 50)
    int[] quantidadeAvaliacoes; // N do livro
    int capacidadeLivros;

    // Usuários: a posição no vetor é o índice do User_id na tabelaUsuarios
    char[][] usuarioChave;
    int[] usuarioTamanho;
    char[][] usuarioNome;
    int[] usuarioNomeTamanho;
    int capacidadeUsuarios;

    // Contadores da leitura (RF4)
    int registrosLidos;
    int quantidadePares;        // avaliações válidas
    int descartadosSemUsuario;
    int descartadosSemTitulo;
    int descartadosNotaInvalida;

    // Construtor da base: cria as duas tabelas e os vetores vazios
    public BaseAvaliacoes() {
        int m = 1000003; // capacidade fixa das tabelas (Seção 5.1)
        this.tabelaTitulos = new TabelaHash(m);
        this.tabelaUsuarios = new TabelaHash(m);
        this.capacidadeLivros = 300000;
        this.tituloChave = new char[capacidadeLivros][];
        this.tituloTamanho = new int[capacidadeLivros];
        this.somaDecimos = new int[capacidadeLivros];
        this.quantidadeAvaliacoes = new int[capacidadeLivros];
        this.capacidadeUsuarios = 1200000;
        this.usuarioChave = new char[capacidadeUsuarios][];
        this.usuarioTamanho = new int[capacidadeUsuarios];
        this.usuarioNome = new char[capacidadeUsuarios][];
        this.usuarioNomeTamanho = new int[capacidadeUsuarios];
        this.quantidadePares = 0;
        this.registrosLidos = 0;
        this.descartadosSemUsuario = 0;
        this.descartadosSemTitulo = 0;
        this.descartadosNotaInvalida = 0;
    }

    // Método para ler o arquivo inteiro e indexar livros e usuários
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
                int livro = registrarLivro(leitor.titulo, leitor.tamanhoTitulo, nota);
                int usuario = registrarUsuario(leitor);
                quantidadePares++;
            }
        }
        leitor.fechar();
    }

    // Método para somar a nota no livro; cria o livro se ele é novo
    int registrarLivro(char[] titulo, int tamanho, int nota) {
        int livro = tabelaTitulos.inserir(titulo, tamanho);
        if (tabelaTitulos.chaveNova == 1) {
            if (livro == capacidadeLivros) {
                aumentarLivros();
            }
            tituloChave[livro] = tabelaTitulos.ultimoNode.chave;
            tituloTamanho[livro] = tamanho;
            somaDecimos[livro] = 0;
            quantidadeAvaliacoes[livro] = 0;
        }
        somaDecimos[livro] = somaDecimos[livro] + nota;
        quantidadeAvaliacoes[livro]++;
        return livro;
    }

    // Método para achar o usuário; cria se é novo e guarda o primeiro nome não vazio
    int registrarUsuario(LeitorCsv leitor) {
        int usuario = tabelaUsuarios.inserir(leitor.usuario, leitor.tamanhoUsuario);
        if (tabelaUsuarios.chaveNova == 1) {
            if (usuario == capacidadeUsuarios) {
                aumentarUsuarios();
            }
            usuarioChave[usuario] = tabelaUsuarios.ultimoNode.chave;
            usuarioTamanho[usuario] = leitor.tamanhoUsuario;
            usuarioNome[usuario] = null;
            usuarioNomeTamanho[usuario] = 0;
        }
        if (usuarioNomeTamanho[usuario] == 0 && leitor.tamanhoNome > 0) {
            char[] nome = new char[leitor.tamanhoNome];
            for (int i = 0; i < leitor.tamanhoNome; i++) {
                nome[i] = leitor.nome[i];
            }
            usuarioNome[usuario] = nome;
            usuarioNomeTamanho[usuario] = leitor.tamanhoNome;
        }
        return usuario;
    }

    // Método para dobrar os vetores de livros quando enchem
    void aumentarLivros() {
        int novaCapacidade = capacidadeLivros * 2;
        char[][] novoChave = new char[novaCapacidade][];
        int[] novoTamanho = new int[novaCapacidade];
        int[] novaSoma = new int[novaCapacidade];
        int[] novaQuantidade = new int[novaCapacidade];
        for (int i = 0; i < capacidadeLivros; i++) {
            novoChave[i] = tituloChave[i];
            novoTamanho[i] = tituloTamanho[i];
            novaSoma[i] = somaDecimos[i];
            novaQuantidade[i] = quantidadeAvaliacoes[i];
        }
        tituloChave = novoChave;
        tituloTamanho = novoTamanho;
        somaDecimos = novaSoma;
        quantidadeAvaliacoes = novaQuantidade;
        capacidadeLivros = novaCapacidade;
    }

    // Método para dobrar os vetores de usuários quando enchem
    void aumentarUsuarios() {
        int novaCapacidade = capacidadeUsuarios * 2;
        char[][] novoChave = new char[novaCapacidade][];
        int[] novoTamanho = new int[novaCapacidade];
        char[][] novoNome = new char[novaCapacidade][];
        int[] novoNomeTamanho = new int[novaCapacidade];
        for (int i = 0; i < capacidadeUsuarios; i++) {
            novoChave[i] = usuarioChave[i];
            novoTamanho[i] = usuarioTamanho[i];
            novoNome[i] = usuarioNome[i];
            novoNomeTamanho[i] = usuarioNomeTamanho[i];
        }
        usuarioChave = novoChave;
        usuarioTamanho = novoTamanho;
        usuarioNome = novoNome;
        usuarioNomeTamanho = novoNomeTamanho;
        capacidadeUsuarios = novaCapacidade;
    }
}
