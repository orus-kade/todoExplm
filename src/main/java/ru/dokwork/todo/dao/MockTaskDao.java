package ru.dokwork.todo.dao;

import ru.dokwork.todo.Task;

import java.util.*;

/**
 * // TODO: Comment this
 */
public class MockTaskDao implements TaskDao {

    private static Map<UUID, Task> tasks = new HashMap<>();

    @Override
    public Task getByUUID(UUID uuid) {
        return tasks.get(uuid);
    }

    @Override
    public void save(Task task) {
        if (task.getUUID() == null) {
            task.setUUID(UUID.randomUUID());
        }
        tasks.put(task.getUUID(), task);
    }

    @Override
    public void remove(Task task) {
        tasks.remove(task.getUUID());
    }

    @Override
    public Collection<Task> findByStatus(boolean isCompleted) {
        Collection<Task> result = new LinkedList<>();
        for (Task task : tasks.values()) {
            if (task.isCompleted() == isCompleted) {
                result.add(task);
            }
        }
        return result;
    }

    @Override
    public Collection<Task> getAll() {
        return tasks.values();
    }
}
