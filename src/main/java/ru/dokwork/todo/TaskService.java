package ru.dokwork.todo;

import ru.dokwork.todo.dao.MockTaskDao;
import ru.dokwork.todo.dao.TaskDao;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;


@Path("/tasks")
@Produces({"application/json"})
public class TaskService {

    private TaskDao dao;

    /**
     * Get task by id.
     *
     * @param uuid identifier of task
     * @return task with specific id.
     */
    @GET
    @Path("/{uuid}")
    public Task get(@PathParam("uuid") UUID uuid) {
        return dao.getByUUID(uuid);
    }

    /**
     * Add new task.
     *
     * @param task new task.
     */
    @POST
    @Path("/")
    @Consumes({"application/json"})
    public Response add(Task task) {
        dao.save(task);
        URI location = URI.create("/tasks/" + task.getUUID().toString());
        return Response.created(location).build();
    }

    /**
     * Update task with specific identifier
     *
     * @param uuid identifier of task, that be updated.
     * @param task task with new properties.
     * @return http response.
     */
    @PUT
    @Path("/{uuid}")
    @Consumes({"application/json"})
    public Response update(@PathParam("uuid") UUID uuid, Task task) {
        Task originTask = dao.getByUUID(uuid);
        if (originTask == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        originTask.setName(task.getName());
        originTask.setDescription(task.getDescription());
        originTask.setCompleted(task.isCompleted());
        dao.save(originTask);
        return Response.ok(originTask, MediaType.APPLICATION_JSON_TYPE).build();
    }

    /**
     * Modify properties of the task with specific identifier.
     *
     * @param uuid          identifier of task, that be edited.
     * @param name        new name.
     * @param description new description.
     * @param isCompleted new completed status.
     */
    @PATCH
    @Path("/{uuid}")
    public Response patch(@PathParam("uuid") UUID uuid,
                          @QueryParam("name") String name,
                          @QueryParam("description") String description,
                          @QueryParam("completed") Boolean isCompleted) {
        Task originTask = dao.getByUUID(uuid);
        if (originTask == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (name != null) {
            originTask.setName(name);
        }
        if (description != null) {
            originTask.setDescription(description);
        }
        if (isCompleted != null) {
            originTask.setCompleted(isCompleted);
        }
        dao.save(originTask);
        return Response.ok(originTask, MediaType.APPLICATION_JSON_TYPE).build();
    }

    /**
     * Delete task with specific identifier.
     *
     * @param uuid identifier of task, that be removed.
     * @return http response.
     */
    @DELETE
    @Path("/{uuid}")
    public Response delete(@PathParam("uuid") UUID uuid) {
        Task originTask = dao.getByUUID(uuid);
        if (originTask == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        dao.remove(originTask);
        return Response.noContent().build();
    }

    /**
     * Find all completed or uncompleted tasks.
     *
     * @param isCompleted mark of completed state for search.
     * @return collection with completed (if argument is true) or uncompleted (if argument is false) tasks.
     */
    @GET
    public Collection<Task> find(@QueryParam("completed") Boolean isCompleted) {
        if (isCompleted == null) {
            return null;
        }
        return dao.findByStatus(isCompleted);
    }

    /**
     * Get list of all tasks.
     *
     * @return list of tasks.
     */
    @GET
    public Collection<Task> getAll() {
        return dao.getAll();
    }

    public TaskService() {
        dao = new MockTaskDao();
    }
}
