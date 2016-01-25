import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.web.bind.annotation.*
import springfox.documentation.swagger2.annotations.EnableSwagger2
//@Grab(group='org.springframework.data', module='spring-data-redis', version='1.6.2.RELEASE')
@Grab(group='org.springframework.boot', module='spring-boot-starter-web', version='1.3.2.RELEASE')
@Grab(group='org.springframework.boot', module='spring-boot-starter-redis', version='1.3.2.RELEASE')
@Grab(group='io.springfox', module='springfox-swagger2', version='2.3.1')
@Grab(group='io.springfox', module='springfox-swagger-ui', version='2.3.1')




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
@EnableSwagger2
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
        String json = mapper.writeValueAsString(todo)

        template.opsForList().leftPush("todos", json)

        return todo
    }

    @RequestMapping(path = "/todos", method = RequestMethod.GET)
    List<Todo> readAll() {
        def size = template.opsForList().size("todos")
        return template.opsForList().range("todos", 0, size-1).stream()
                .map { json -> mapper.readValue(json, Todo.class)}.collect()
    }

    @RequestMapping(path = "/todos/{id}", method = RequestMethod.GET)
    Todo read(@PathVariable String id) {
        def size = template.opsForList().size("todos")

        return template.opsForList().range("todos", 0, size).stream()
                .map { json -> mapper.readValue(json, Todo.class)}
                .filter{ Todo todo -> todo.id == Long.valueOf(id) }
                .findFirst().get()
    }

    public static class Todo {
        Long id
        String title
        boolean completed
    }
}

