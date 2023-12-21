# ChatRabbitMQ Tutorial

Este tutorial aborda a 4ª etapa do projeto, que inclui os seguintes tópicos:
- Configuração de Cluster
- Balanceamento de Carga (Load Balancing)

## Requisitos

Antes de prosseguir com este tutorial, certifique-se de ter concluído a etapa de instalação do RabbitMQ e compreendido os conceitos básicos.

## Configuração do Cluster RabbitMQ

### Passo 1: Criar Máquinas Virtuais na AWS (EC2)

Crie 3 máquinas virtuais na AWS (EC2) e nomeie-as como: `node1`, `node2` e `node3`.

### Passo 2: Instalar RabbitMQ no node1

Realize o procedimento completo de instalação do RabbitMQ no `node1`.

### Passo 3: Instalar RabbitMQ no node2

Instale o RabbitMQ no `node2`, mas evite criar um usuário. Isso significa que você deve pular o penúltimo comando do tutorial do RabbitMQ.

### Passo 4: Repetir o Processo para o node3

Repita o mesmo processo descrito anteriormente para o `node3`.

### Passo 5: Parar `node2` e `node3`
Execute o seguinte comando em `node2` e `node2`:
```shell script
sudo rabbitmqctl stop_app
```

### Passo 6: Obtenção do cookie de `node1`
Execute o seguinte comando em `node1` para obter seu cookie e salve-o no bloco de notas
```shell script
sudo cat /var/lib/rabbitmq/.erlang.cookie
```

### Passo 7: Aplicação do cookie de `node1` em `node2`
Execute os seguintes comando: 
```shell script
sudo su
```
```shell script
nano /var/lib/rabbitmq/.erlang.cookie
```
Ele irá abrir o cookie do `node2` em modo edição, apague o conteudo existente e cole o cookie de `node1` descrito na etapa 6.


### Exemplo de Comando Maven

Além disso, após a configuração do RabbitMQ, você pode usar o seguinte comando Maven para instalar dependências:

```shell script
mvn install
```