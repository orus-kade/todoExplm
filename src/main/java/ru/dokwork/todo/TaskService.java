package ru.dokwork.todo;

import javax.ws.rs.*;
import java.util.Collection;
import java.util.UUID;


@Path("/tasks")
@Produces({"application/json"})
public class TaskService {

    /**
     * Get task by id.
     *
     * @param id identifier of task
     * @return task with specific id.
     */
    @GET
    @Path("/{id}")
    public Task get(@PathParam("{id}") UUID id) {
        return null;
    }

    /**
     * Add new task.
     *
     * @param task new task.
     */
    @POST
    @Consumes({"application/json"})
    public void add(Task task) {
    }

    /**
     * Update task with specific identifier
     *
     * @param id   identifier of task, that be updated.
     * @param task task with new properties.
     * @return http response.
     */
    @PATCH
    @Path("/{id}")
    @Consumes({"application/json"})
    public void update(@PathParam("{id}") UUID id, Task task) {
    }

    /**
     * Delete task with specific identifier.
     *
     * @param id identifier of task, that be removed.
     * @return http response.
     */
    @DELETE
    @Path("/{id}")
    public void delete(@PathParam("{id}") UUID id) {
    }

    /**
     * Find task with specific name.
     *
     * @param name name of task.
     * @return task with specific name.
     */
    @GET
    @Path("/{find}")
    public void find(@QueryParam("name") String name) {
    }

    /**
     * Get list of all tasks.
     *
     * @return list of tasks.
     */
    @GET
    public Collection<Task> getAll() {
        return null;
    }

    /**
     * Mark task with specific identifier as done.
     *
     * @param id identifier of task.
     * @return http response.
     */
    @PUT
    @Path("/{id}")
    public void done(@PathParam("{id}") UUID id) {
    }

    /**
     * Mark task with specific identifier as undone.
     *
     * @param id identifier of task.
     * @return http response.
     */
    @PUT
    @Path("/{id}")
    public void undone(@PathParam("{id}") UUID id) {
    }
}
