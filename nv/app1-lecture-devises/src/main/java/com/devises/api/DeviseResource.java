package com.devises.api;

import com.devises.ejb.DeviseService;
import com.devises.model.Devise;

import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/devises")
@Produces(MediaType.APPLICATION_JSON)
public class DeviseResource {

    @EJB
    private DeviseService deviseService;

    @GET
    public Response getAll() {
        List<Devise> devises = deviseService.getAllDevises();
        return Response.ok(devises).build();
    }

    @GET
    @Path("/{nom}")
    public Response getByNom(@PathParam("nom") String nom) {
        Devise d = deviseService.getDeviseByNom(nom);
        if (d == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(d).build();
    }
}
