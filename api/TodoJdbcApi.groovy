package api

import api.model.Todo
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

import java.sql.ResultSet
import java.sql.SQLException

@RestController
@RequestMapping("/api")
class TodoJdbcApi {

    Logger logger = LoggerFactory.getLogger(this.getClass())

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    JdbcTemplate jdbcTemplate2;

    RowMapper<Todo> todoRowMapper = new RowMapper<Todo>() {
        @Override
        Todo mapRow(ResultSet rs, int rowNum) throws SQLException {
            def todo = new Todo()
            todo.id = rs.getLong("id")
            todo.title = rs.getString("title")
            todo.completed = rs.getBoolean("completed")
            return todo
        }
    }


    @RequestMapping(method = RequestMethod.GET)
    String home() {
        "{}"
    }

    @RequestMapping(path = "/todos", method = RequestMethod.POST)
    @Transactional
    Todo write(@RequestBody Todo todo) {
        saveTodo(todo)

        return todo
    }

    private Todo saveTodo(Todo todo) {
        todo.id = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("TODO")
                .usingGeneratedKeyColumns("id")
                .executeAndReturnKey(new MapSqlParameterSource(["title": todo.title, "completed":false]))
        return todo
    }

    @RequestMapping(path = "/todos", method = RequestMethod.GET)
    List<Todo> readAll() {
        jdbcTemplate.query("select * from TODO", todoRowMapper)
    }

    @RequestMapping(path = "/todos/{id}", method = RequestMethod.GET)
    Todo read(@PathVariable Long id) {
        return getTodo(id)
    }

    private Todo getTodo(Long id) {
        jdbcTemplate.queryForObject("SELECT * FROM TODO where id = ?", [id] as Object[], todoRowMapper)
    }

    @RequestMapping(path = "/todos/{id}", method = RequestMethod.DELETE)
    void delete(@PathVariable Long id) {
        deleteTodo(id)
    }

    private deleteTodo(Long id) {
        jdbcTemplate.update("delete from TODO where id = ?", id)
    }

    @RequestMapping(path = "/todos/{id}", method = RequestMethod.PUT)
    void update(@PathVariable Long id, @RequestBody Todo todo) {
        jdbcTemplate.update("update TODO set completed = ?, title = ? where id = ?", todo.completed, todo.title, id)
    }

    @RequestMapping(path = "/todos", method = RequestMethod.DELETE)
    void clearCompleted() {
        jdbcTemplate.update("delete from TODO where completed = true")
    }

}

