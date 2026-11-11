import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

// Escreve os arquivos de saída caractere por caractere, sem formatação pronta
public class EscritorSaida {
    BufferedWriter escritor;
    char[] digitos;

    // Construtor do escritor: cria (ou apaga e recria) o arquivo
    public EscritorSaida(String caminho) throws IOException {
        this.escritor = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(caminho), "UTF-8"));
        this.digitos = new char[12];
    }

    // Método para escrever um texto fixo
    void texto(String texto) throws IOException {
        escritor.write(texto);
    }

    // Método para escrever um caractere
    void caractere(char caractere) throws IOException {
        escritor.write(caractere);
    }

    // Método para escrever um campo de texto do CSV; põe entre aspas se tiver vírgula, aspas ou quebra de linha
    void campo(char[] valor, int tamanho) throws IOException {
        int precisaAspas = 0;
        for (int i = 0; i < tamanho; i++) {
            if (valor[i] == ',' || valor[i] == '"' || valor[i] == '\n' || valor[i] == '\r') {
                precisaAspas = 1;
            }
        }
        if (precisaAspas == 1) {
            escritor.write('"');
        }
        for (int i = 0; i < tamanho; i++) {
            if (valor[i] == '"') {
                escritor.write('"'); // aspas dentro do campo são escritas duas vezes
            }
            escritor.write(valor[i]);
        }
        if (precisaAspas == 1) {
            escritor.write('"');
        }
    }

    // Método para escrever um inteiro não negativo
    void inteiro(int valor) throws IOException {
        int quantidade = 0;
        if (valor == 0) {
            digitos[0] = '0';
            quantidade = 1;
        }
        while (valor > 0) {
            digitos[quantidade] = (char) ('0' + valor % 10);
            quantidade++;
            valor = valor / 10;
        }
        for (int i = quantidade - 1; i >= 0; i--) {
            escritor.write(digitos[i]); // os dígitos foram guardados ao contrário
        }
    }

    // Método para escrever a média soma / N com 4 casas decimais (ex.: 4.6000)
    void media(int somaDecimos, int quantidade) throws IOException {
        // média * 10000 arredondada; a soma está em décimos, então multiplica por 1000
        int escala = (int) ((somaDecimos * 1000.0) / quantidade + 0.5);
        inteiro(escala / 10000);
        escritor.write('.');
        int fracao = escala % 10000;
        escritor.write((char) ('0' + fracao / 1000));
        escritor.write((char) ('0' + (fracao / 100) % 10));
        escritor.write((char) ('0' + (fracao / 10) % 10));
        escritor.write((char) ('0' + fracao % 10));
    }

    // Método para escrever uma nota em décimos com uma casa (50 vira 5.0)
    void nota(int decimos) throws IOException {
        inteiro(decimos / 10);
        escritor.write('.');
        escritor.write((char) ('0' + decimos % 10));
    }

    // Método para terminar a linha
    void fimLinha() throws IOException {
        escritor.write('\n');
    }

    // Método para fechar o arquivo
    void fechar() throws IOException {
        escritor.close();
    }
}
