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

## Como executar

O programa lê e grava na **pasta em que é executado**. Coloque nela o `Books_rating.csv` (base do Kaggle,
*Amazon Books Reviews*) e o `user_id.txt` (um `User_id` por linha; há um exemplo neste repositório). Depois:

```
java -cp <caminho do repositório>/classes Principal
```

Arquivos gerados:

| Arquivo | Conteúdo |
|---|---|
| `usuario_melhor_livro.csv` | um usuário por linha: `User_id, profileName, Title, media_livro, N_livro, nota_usuario` |
| `usuario_melhor_livro_ordenado.csv` | a mesma lista em ordem decrescente de `media_livro` (desempate: maior `N_livro`, depois `User_id`) |
| `ranking_top10_livros.csv` | os 10 livros de maior média com pelo menos 100 avaliações |

Na tela aparecem o resumo da leitura (linhas válidas e descartadas), o ranking, as consultas do
`user_id.txt` e o tempo de cada etapa.
