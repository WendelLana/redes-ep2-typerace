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
Digite "Iniciar [quantidade de palavras]" para começar o jogo, caso a quantidade não seja fornecida serão utilizadas 15 palavras\
Digite "Sair" para se desconectar do servidor

#### Servidor Iniciado
![image](https://user-images.githubusercontent.com/48734976/149561892-42f9db73-5bc2-4b33-a091-ff30695a0b10.png)
#### Cliente Conectado ao Servidor
![image](https://user-images.githubusercontent.com/48734976/149562356-ad372d40-bb4c-474c-bfee-f58b2b9121bf.png)
#### Jogo Iniciado
![image](https://user-images.githubusercontent.com/48734976/149562829-07cf5c6b-8bb3-47de-8b86-889d8de1c76c.png)
#### Jogo Finalizado
![image](https://user-images.githubusercontent.com/48734976/149563226-bf4a69a2-bc8d-42f5-8080-897b1f7f62c5.png)
