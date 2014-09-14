package ru.dokwork.todo.dao;

import ru.dokwork.todo.Task;

import java.util.Collection;
import java.util.UUID;

public interface TaskDao {

    public Task getByUUID(UUID uuid);

    public void save(Task task);

    public void remove(Task task);

    Collection<Task> findByStatus(boolean isCompleted);

    Collection<Task> getAll();
}
