
# Apache Kafka e Programação Orientada a Eventos

O objetivo desse projeto é explorar técnicas do paradigma de Programação Orientada a Eventos. Para isso, utilizo o Apache Kafka, uma plataforma de streaming de dados distribuída, usada para fazer a intermediação de comunicação entre a API e o microsserviço de pedidos (Order).


## Descrição e passo a passo do projeto

### Criação do Productor

1. O primeiro passo foi a criação do projeto **api-rest-kafka** através do Spring Initializer, contendo as dependências do Spring Web, Lombok e Spring for Apache Kafka.

2. Foi criado um arquivo docker/docker-compose.yaml, configurado para rodar um container com o Kafka e o Zookeeper, que ajuda no gerenciamento de estados distribuídos.
([Fonte: Bitnami Kafka](https://hub.docker.com/r/bitnami/kafka))

3. Configuração do Kafka no projeto.

Para isso, é necessário habilitar o Kafka no arquivo principal do projeto:
```java
@EnableKafka  //Annotation para habilitar o Kafka
@SpringBootApplication  
public class Application {  
  
    public static void main(String[] args) {  
       SpringApplication.run(Application.class, args);  
    }  
}
```

Além disso, é necessário configurar a API como producer em *applications.properties*:

```properties
spring.kafka.producer.bootstrap-servers=127.0.0.1:9092  
spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer  
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
```

Aqui definimos como os dados serão serializados. Nesse caso, eles serão transformados de JSON para String.

4. Agora é a etapa de criação do service e controller. Como padrão, cada um terá uma pasta reservada.

O service possui o template do Kafka, usado para enviar o tópico e a mensagem quando um evento for disparado.

```java
@Service  
@RequiredArgsConstructor  
public class RegisterEventService {  
  
    private final KafkaTemplate<Object, Object> kafkaTemplate;
	//Método genérico, recebe o nome do tópico e o qualquer tipo de dado
    public <T> void addEvent(String topic, T data) {  
        kafkaTemplate.send(topic, data);  
    }  
}
```

Já o controller será o responsável por chamar esse service sempre que um endpoint for acessado:

```java
@RestController  
@RequiredArgsConstructor  
@RequestMapping("/api/order")  
public class OrdersController {  
  
    private final RegisterEventService registerEventService;  
  
    @PostMapping("/save")  
    public ResponseEntity<String> saveOrder(@RequestBody OrderRequestDto order) {  
        registerEventService.addEvent("save_order", order);  
        return ResponseEntity.ok("Sucesso");  
    }  
  
}
```

### Criação do Consumer

1. O primeiro passo foi a criação do projeto microsservico-kafka através do Spring Initializer, contendo as dependências do Lombok , Jackson e Spring for Apache Kafka.

2. Da mesma forma, habilitamos a configuração do Kafka no projeto:

```java
@EnableKafka  
@SpringBootApplication  
public class MicrosservicoKafkaApplication {  
  
    public static void main(String[] args) {  
       SpringApplication.run(MicrosservicoKafkaApplication.class, args);  
    }  
  
}
```

Também configuramos o application.properties:

```properties
spring.kafka.consumer.bootstrap-servers=127.0.0.1:9092  
spring.kafka.consumer.auto-offset-reset=earliest  
spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer  
spring.kafka.consumer.value-deserializer=org.apache.kafka.common.serialization.StringDeserializer
```

Lembrando que definimos como os dados que chegaram do kafka serão deserializados. Nesse caso, eles serão tranformados em String. Dentro da função, usaremos o Jackson para transformá-los em JSON.

3. Por fim, foi criado um service para consumir a mensagem enviada pela API:

```java
@Slf4j //adiciona um log na classe com lombok  
@Service  
public class SaveOrderService {  
  
    @KafkaListener(topics = "save_order", groupId = "microservice_save_order")  
    private void execute(ConsumerRecord<String, String> record) {  
        log.info("Key = {}", record.key());  
        log.info("Headers = {}", record.headers());  
        log.info("Partition = {}", record.partition());  
  
        String data = record.value();  
  
        ObjectMapper mapper = new ObjectMapper();  
        OrderRequestDto order = null;  
  
        try {  
            order = mapper.readValue(data, OrderRequestDto.class);  
        } catch (JsonProcessingException e) {  
            log.error("Falha ao converter evento [dado={}]", data, e);  
            return;  
        }  
  
        log.info("Evento recebido = {}", data);  
    }  
  
}
```

Por enquanto, a única função desse service é consumir a mensagem e publicar no log cada um de seus parâmetros. 

Ao rodar o programa, sempre que uma mensagem for enviar pela API, ela será lida e exibida no log da aplicação do Consumer.
## Instalação

1. Clone o repositório na sua máquina local:

`https://github.com/FabioSigF/poe-microsservicos-kafka`

2. É necessário ter o Docker instalado para criação de containers do Apache Kafka e Zookeeper.

https://www.docker.com/products/docker-desktop/


3. Inicialize o .yaml na pasta do docker com os seguinte comando:

```bash
cd docker
 docker-compose -f docker-compose.yml up
```

4. Agora, basta rodar a aplicação. Inicie a aplicação api-rest-kafka e a aplicação microsservico-kafka. Em seguida, envie uma mensagem através da URL `/api/order`, seguindo o formato apresentado na documentação abaixo.

Para isso, você pode usar uma plataforma de API, como o Postman. Se tudo deu certo, será exibido no log da aplicação microsservico-kafka os dados que formam recebidos.


## Documentação da API

### Enviar um pedido

```http
  POST /api/order/save
```

O único requesito desse método é o body. Ele deve ser algo como:

```json
{
    "code": "P2",
    "productName": "Prato Feito",
    "price": 49.90
}
```

Resultado:

![image](https://github.com/user-attachments/assets/35bd271c-c0dc-4645-b7b9-1d914dc5dcdb)
