# redes-ep2-typerace
Repositório para o EP2 de Redes de Computadores, EACH-USP - 2021/2

# Integrantes
* Ryan Brenno Ramos - 11215772
* Wendel Fernandes de Lana - 11215772

## Pré-requisitos
* JDK 11 ou maior (testado com a JDK11 OpenJDK)
* Gradle (incluso no repositório, não é necessário instalá-lo)

### Rodando
Para rodar o servidor
```sh
./gradlew server:run
```

Para rodar um cliente
```sh
./gradlew client:run
```

### Regras do Jogo
* Palavras podem ser maiúsculas ou minúsculas
* Envie apenas uma palavra por vez 
* Não há ordem específica para enviar as palavras
* Vence aquele que acertar todas as palavras mais rápido
* Critério de desempate será a quantidade de respostas erradas

#### Instruções:
Digite "Iniciar [quantidade de palavras]" para começar o jogo com a quantidade de palavras indicada, caso não fornecido serão utilizadas 15 palavras por padrão
Digite "Sair" para se desconectar do servidor
