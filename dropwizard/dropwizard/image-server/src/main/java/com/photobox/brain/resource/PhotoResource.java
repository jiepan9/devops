package com.photobox.brain.resource;

import com.codahale.metrics.annotation.Timed;
import javax.imageio.ImageIO;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Created by jpan on 15-1-27.
 */
@Path("/")
public class PhotoResource {


    @GET
    @Path("/{folder}/{id}.jpg")
    @Produces("image/jpeg")
    @Timed
    public Response getPhoto(@PathParam("folder") String folder,
                             @PathParam("id") String id) throws IOException {
        ByteArrayOutputStream baos=new ByteArrayOutputStream(1000);
        BufferedImage image = ImageIO.read(new File("/space/" + folder + "/" + id + ".jpg"));
        ImageIO.write(image, "jpg", baos);
        byte[] imageData = baos.toByteArray();
        return Response.ok(new ByteArrayInputStream(imageData)).build();
    }


}
