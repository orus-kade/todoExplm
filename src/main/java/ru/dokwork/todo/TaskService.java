package ru.dokwork.todo;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.ArrayList;
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
    public Task get(@PathParam("id") UUID id) {
        Task task = new Task("get", "success");
        task.setId(id);
        return task;
    }

    /**
     * Add new task.
     *
     * @param task new task.
     */
    @POST
    @Path("/add")
    @Consumes({"application/json"})
    public Response add(Task task) {
        task.setId(UUID.randomUUID());
        URI location = URI.create("/tasks/" + task.getId().toString());
        return Response.created(location).build();
    }
//
//    /**
//     * Update task with specific identifier
//     *
//     * @param id   identifier of task, that be updated.
//     * @param task task with new properties.
//     * @return http response.
//     */
//    @PUT
//    @Path("/{id}")
//    @Consumes({"application/json"})
//    public void update(@PathParam("id") UUID id, Task task) {
//    }
//
//    /**
//     * Modify properties of the task with specific identifier.
//     *
//     * @param id          identifier of task, that be edited.
//     * @param name        new name.
//     * @param description new description.
//     * @param isCompleted new completed status.
//     */
//    @PATCH
//    @Path("/{id}")
//    public void patch(@PathParam("id") UUID id,
//                      @QueryParam("name") String name,
//                      @QueryParam("description") String description,
//                      @QueryParam("completed") Boolean isCompleted) {
//
//    }
//
//    /**
//     * Delete task with specific identifier.
//     *
//     * @param id identifier of task, that be removed.
//     * @return http response.
//     */
//    @DELETE
//    @Path("/{id}")
//    public void delete(@PathParam("id") UUID id) {
//    }
//
//    /**
//     * Find all completed or uncompleted tasks.
//     *
//     * @param isCompleted mark of completed state for search.
//     * @return collection with completed (if argument is true) or uncompleted (if argument is false) tasks.
//     */
//    @GET
//    public Collection<Task> find(@QueryParam("completed") Boolean isCompleted) {
//    }

    /**
     * Get list of all tasks.
     *
     * @return list of tasks.
     */
    @GET
    public Collection<Task> getAll() {
        Collection<Task> tasks = new ArrayList<>();
        tasks.add(new Task());
        tasks.add(new Task());
        tasks.add(new Task());
        return tasks;
    }
}
