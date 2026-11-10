import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

// Lê o Books_rating.csv caractere por caractere e separa só as colunas usadas no trabalho
public class LeitorCsv {
    BufferedReader leitor;
    int tamanhoBuffer;      // caracteres lidos do arquivo de cada vez
    int tamanhoCampo;       // maior quantidade de caracteres guardada por campo
    char[] buffer;
    int quantidadeBuffer;   // caracteres válidos no buffer
    int posicaoBuffer;      // próximo caractere a ser lido do buffer

    // Colunas usadas: 1 Title, 3 User_id, 4 profileName, 6 review/score
    char[] titulo;
    int tamanhoTitulo;
    char[] usuario;
    int tamanhoUsuario;
    char[] nome;
    int tamanhoNome;
    char[] nota;
    int tamanhoNota;

    // Construtor do leitor: abre o arquivo e pula a linha do cabeçalho
    public LeitorCsv(String caminho) throws IOException {
        this.leitor = new BufferedReader(new InputStreamReader(new FileInputStream(caminho), "UTF-8"));
        this.tamanhoBuffer = 65536;
        this.tamanhoCampo = 1000;
        this.buffer = new char[tamanhoBuffer];
        this.quantidadeBuffer = 0;
        this.posicaoBuffer = 0;
        this.titulo = new char[tamanhoCampo];
        this.usuario = new char[tamanhoCampo];
        this.nome = new char[tamanhoCampo];
        this.nota = new char[tamanhoCampo];
        lerRegistro(); // cabeçalho
    }

    // Método para devolver o próximo caractere do arquivo, ou -1 no fim
    int proximoCaractere() throws IOException {
        if (posicaoBuffer == quantidadeBuffer) {
            quantidadeBuffer = leitor.read(buffer, 0, tamanhoBuffer);
            posicaoBuffer = 0;
            if (quantidadeBuffer <= 0) {
                quantidadeBuffer = 0;
                return -1;
            }
        }
        int caractere = buffer[posicaoBuffer];
        posicaoBuffer++;
        return caractere;
    }

    // Método para olhar o próximo caractere sem consumir, ou -1 no fim
    int espiarCaractere() throws IOException {
        int caractere = proximoCaractere();
        if (caractere != -1) {
            posicaoBuffer--; // devolve o caractere para o buffer
        }
        return caractere;
    }

    // Método para guardar o caractere no campo certo; as outras colunas são ignoradas
    void guardar(int campo, char caractere) {
        if (campo == 1 && tamanhoTitulo < tamanhoCampo) {
            titulo[tamanhoTitulo] = caractere;
            tamanhoTitulo++;
        } else if (campo == 3 && tamanhoUsuario < tamanhoCampo) {
            usuario[tamanhoUsuario] = caractere;
            tamanhoUsuario++;
        } else if (campo == 4 && tamanhoNome < tamanhoCampo) {
            nome[tamanhoNome] = caractere;
            tamanhoNome++;
        } else if (campo == 6 && tamanhoNota < tamanhoCampo) {
            nota[tamanhoNota] = caractere;
            tamanhoNota++;
        }
    }

    // Método para ler um registro inteiro; devolve 1 se leu, 0 se o arquivo acabou
    int lerRegistro() throws IOException {
        tamanhoTitulo = 0;
        tamanhoUsuario = 0;
        tamanhoNome = 0;
        tamanhoNota = 0;
        int campo = 0;
        int dentroAspas = 0;
        int leuAlgo = 0;
        int resultado = -1; // -1 enquanto o registro não terminou; depois 1 (leu) ou 0 (fim do arquivo)
        while (resultado == -1) {
            int caractere = proximoCaractere();
            if (caractere == -1) {
                resultado = leuAlgo;
            } else {
                leuAlgo = 1;
                if (dentroAspas == 1) {
                    if (caractere == '"') {
                        if (espiarCaractere() == '"') {
                            proximoCaractere();        // "" dentro de aspas vira uma aspa
                            guardar(campo, '"');
                        } else {
                            dentroAspas = 0;           // fechou as aspas
                        }
                    } else {
                        guardar(campo, (char) caractere); // vírgula e quebra de linha aqui são texto
                    }
                } else {
                    if (caractere == '"') {
                        dentroAspas = 1;
                    } else if (caractere == ',') {
                        campo++;
                    } else if (caractere == '\n') {
                        resultado = 1;                 // fim da linha fora das aspas: fim do registro
                    } else if (caractere != '\r') {
                        guardar(campo, (char) caractere);
                    }
                }
            }
        }
        return resultado;
    }

    // Método para converter a nota em décimos (4.0 vira 40); devolve -1 se for inválida
    int notaEmDecimos() {
        int parteInteira = 0;
        int decimos = 0;
        int digitosInteiros = 0;
        int digitosDecimais = 0;
        int depoisDoPonto = 0;
        for (int i = 0; i < tamanhoNota; i++) {
            char caractere = nota[i];
            if (caractere >= '0' && caractere <= '9') {
                if (depoisDoPonto == 0) {
                    parteInteira = parteInteira * 10 + (caractere - '0');
                    digitosInteiros++;
                    if (digitosInteiros > 2) {
                        return -1;
                    }
                } else {
                    if (digitosDecimais == 0) {
                        decimos = caractere - '0';
                    } else if (caractere != '0') {
                        return -1; // nota com mais de uma casa decimal que não é zero
                    }
                    digitosDecimais++;
                }
            } else if (caractere == '.' && depoisDoPonto == 0) {
                depoisDoPonto = 1;
            } else {
                return -1;
            }
        }
        if (digitosInteiros == 0) {
            return -1;
        }
        int valor = parteInteira * 10 + decimos;
        if (valor < 10 || valor > 50) {
            return -1; // fora de 1.0 a 5.0
        }
        return valor;
    }

    // Método para fechar o arquivo
    void fechar() throws IOException {
        leitor.close();
    }
}
