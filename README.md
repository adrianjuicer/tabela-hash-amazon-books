# Tabelas Hash: melhor livro de cada usuário (Amazon Books Reviews)

Trabalho prático de Resolução de Problemas Estruturados em Computação (RA3), PUCPR, 2026-2.
Professor Andrey Cabral Meira. Aluno: Adrian.

Para cada usuário da base `Books_rating.csv` (3 milhões de avaliações), o programa descobre o livro de maior
média entre os livros que ele avaliou, usando duas tabelas hash próprias com encadeamento separado
(capacidade fixa de 1 000 003 posições).

## Como compilar

Precisa só do JDK (testado com o Java 21). Na pasta do repositório:

```
javac -encoding UTF-8 -d classes *.java
```
