package api.model

public class Todo {
    Long id

    String title
    boolean completed

    boolean isCompleted() {
        return completed
    }
}