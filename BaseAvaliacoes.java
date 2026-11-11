import java.io.IOException;

// Guarda os livros, os usuários e cada avaliação válida, e decide o melhor livro de cada usuário
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
    int[] melhorLivro;          // índice do melhor livro do usuário
    int[] notaUsuario;          // maior nota que o usuário deu ao melhor livro, em décimos
    int capacidadeUsuarios;

    // Avaliações válidas: usuário, livro e nota de cada linha
    int[] parUsuario;
    int[] parLivro;
    int[] parNota;
    int quantidadePares;
    int capacidadePares;

    // Contadores da leitura (RF4)
    int registrosLidos;
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
        this.melhorLivro = new int[capacidadeUsuarios];
        this.notaUsuario = new int[capacidadeUsuarios];
        this.capacidadePares = 3100000;
        this.parUsuario = new int[capacidadePares];
        this.parLivro = new int[capacidadePares];
        this.parNota = new int[capacidadePares];
        this.quantidadePares = 0;
        this.registrosLidos = 0;
        this.descartadosSemUsuario = 0;
        this.descartadosSemTitulo = 0;
        this.descartadosNotaInvalida = 0;
    }

    // Método para ler o arquivo inteiro e indexar livros, usuários e avaliações
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
                registrarPar(usuario, livro, nota);
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
            melhorLivro[usuario] = -1;
            notaUsuario[usuario] = 0;
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

    // Método para guardar a avaliação; o melhor livro só pode ser decidido depois da leitura
    void registrarPar(int usuario, int livro, int nota) {
        if (quantidadePares == capacidadePares) {
            aumentarPares();
        }
        parUsuario[quantidadePares] = usuario;
        parLivro[quantidadePares] = livro;
        parNota[quantidadePares] = nota;
        quantidadePares++;
    }

    // Método para escolher o melhor livro de cada usuário passando uma vez pelas avaliações
    void calcularMelhoresLivros() {
        for (int i = 0; i < quantidadePares; i++) {
            int usuario = parUsuario[i];
            int livro = parLivro[i];
            int nota = parNota[i];
            if (melhorLivro[usuario] == -1 || compararLivros(livro, melhorLivro[usuario]) > 0) {
                melhorLivro[usuario] = livro;
                notaUsuario[usuario] = nota;
            } else if (livro == melhorLivro[usuario] && nota > notaUsuario[usuario]) {
                notaUsuario[usuario] = nota; // avaliou o mesmo livro de novo: vale a maior nota
            }
        }
    }

    // Método para comparar dois livros: 1 se o livroA é melhor, -1 se é pior, 0 se é o mesmo
    // Regra: maior média; empate, maior N; empate, título em ordem alfabética
    int compararLivros(int livroA, int livroB) {
        // somaA / NA > somaB / NB é o mesmo que somaA * NB > somaB * NA (sem divisão)
        double produtoA = (double) somaDecimos[livroA] * quantidadeAvaliacoes[livroB];
        double produtoB = (double) somaDecimos[livroB] * quantidadeAvaliacoes[livroA];
        if (produtoA > produtoB) {
            return 1;
        }
        if (produtoA < produtoB) {
            return -1;
        }
        if (quantidadeAvaliacoes[livroA] > quantidadeAvaliacoes[livroB]) {
            return 1;
        }
        if (quantidadeAvaliacoes[livroA] < quantidadeAvaliacoes[livroB]) {
            return -1;
        }
        int ordem = compararTexto(tituloChave[livroA], tituloTamanho[livroA], tituloChave[livroB], tituloTamanho[livroB]);
        if (ordem < 0) {
            return 1;
        }
        if (ordem > 0) {
            return -1;
        }
        return 0;
    }

    // Método para comparar dois textos em ordem alfabética: negativo se o textoA vem antes
    static int compararTexto(char[] textoA, int tamanhoA, char[] textoB, int tamanhoB) {
        int menor = tamanhoA;
        if (tamanhoB < menor) {
            menor = tamanhoB;
        }
        for (int i = 0; i < menor; i++) {
            if (textoA[i] != textoB[i]) {
                return textoA[i] - textoB[i];
            }
        }
        return tamanhoA - tamanhoB; // o mais curto vem antes
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
        int[] novoMelhor = new int[novaCapacidade];
        int[] novaNota = new int[novaCapacidade];
        for (int i = 0; i < capacidadeUsuarios; i++) {
            novoChave[i] = usuarioChave[i];
            novoTamanho[i] = usuarioTamanho[i];
            novoNome[i] = usuarioNome[i];
            novoNomeTamanho[i] = usuarioNomeTamanho[i];
            novoMelhor[i] = melhorLivro[i];
            novaNota[i] = notaUsuario[i];
        }
        usuarioChave = novoChave;
        usuarioTamanho = novoTamanho;
        usuarioNome = novoNome;
        usuarioNomeTamanho = novoNomeTamanho;
        melhorLivro = novoMelhor;
        notaUsuario = novaNota;
        capacidadeUsuarios = novaCapacidade;
    }

    // Método para dobrar os vetores de avaliações quando enchem
    void aumentarPares() {
        int novaCapacidade = capacidadePares * 2;
        int[] novoUsuario = new int[novaCapacidade];
        int[] novoLivro = new int[novaCapacidade];
        int[] novaNota = new int[novaCapacidade];
        for (int i = 0; i < capacidadePares; i++) {
            novoUsuario[i] = parUsuario[i];
            novoLivro[i] = parLivro[i];
            novaNota[i] = parNota[i];
        }
        parUsuario = novoUsuario;
        parLivro = novoLivro;
        parNota = novaNota;
        capacidadePares = novaCapacidade;
    }
}
