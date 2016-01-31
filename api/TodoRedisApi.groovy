package api

import api.model.Todo
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.HashOperations
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.web.bind.annotation.*

import java.util.stream.Collectors

/*

spring run *.groovy -- --spring.redis.host=192.168.99.100

docker run --name redis -d -p 6379:6379 redis

Ligne de commande REDIS:
docker run -it --link redis:redis --rm redis sh -c 'exec redis-cli -h "$REDIS_PORT_6379_TCP_ADDR" -p "$REDIS_PORT_6379_TCP_PORT"'

Les fichiers static sont servies s'ils sont placé dans /static/
Voir les liens ci dessous qui documentent la feature springboot :
https://spring.io/blog/2013/12/19/serving-static-web-content-with-spring-boot
http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-spring-mvc-static-content

 */

@RestController
@RequestMapping("/api")
class TodoRedisApi {

    Logger logger = LoggerFactory.getLogger(this.getClass())

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    StringRedisTemplate template;


    @RequestMapping(method = RequestMethod.GET)
    String home() {
        "{}"
    }

    @RequestMapping(path = "/todos", method = RequestMethod.POST)
    Todo write(@RequestBody Todo todo) {
        todo.id = System.currentTimeMillis()
        saveTodo(todo)

        return todo
    }

    private void saveTodo(Todo todo) {
        String json = mapper.writeValueAsString(todo)
        HashOperations<String, String, String> opsForHash = template.opsForHash()
        opsForHash.put("todos", todo.id.toString(), json)
    }

    @RequestMapping(path = "/todos", method = RequestMethod.GET)
    List<Todo> readAll() {
        return template.opsForHash().entries("todos").values().stream()
                .map { json -> mapper.readValue(json, Todo.class)}
                .collect(Collectors.toList())
    }

    @RequestMapping(path = "/todos/{id}", method = RequestMethod.GET)
    Todo read(@PathVariable String id) {
        return getTodo(id)
    }

    private Todo getTodo(String id) {
        return Optional.of(template.opsForHash().get("todos", id))
                .map { json -> mapper.readValue(json, Todo.class) }
                .get()
    }

    @RequestMapping(path = "/todos/{id}", method = RequestMethod.DELETE)
    void delete(@PathVariable String id) {
        deleteTodo(id)
    }

    private deleteTodo(String id) {
        template.opsForHash().delete("todos", id)
    }

    @RequestMapping(path = "/todos/{id}", method = RequestMethod.PUT)
    void update(@PathVariable String id, @RequestBody Todo todo) {
        saveTodo(todo)
    }

    @RequestMapping(path = "/todos", method = RequestMethod.DELETE)
    void clearCompleted() {
        template.opsForHash().entries("todos").values().stream()
                .map { json -> mapper.readValue(json, Todo.class) }
                .filter { Todo todo -> todo.isCompleted() }
                .forEach { todo -> deleteTodo(todo.id) }
    }
}

