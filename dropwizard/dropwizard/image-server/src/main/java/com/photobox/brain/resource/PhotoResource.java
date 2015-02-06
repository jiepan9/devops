package com.photobox.brain.resource;

import com.codahale.metrics.annotation.Timed;
import com.google.common.io.ByteStreams;

import javax.imageio.ImageIO;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.awt.image.BufferedImage;
import java.io.*;

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
        StreamingOutput streamOutput = os -> {

            InputStream stream =  new FileInputStream("d:/space/" + folder + "/" + id + ".jpg");
            ByteStreams.copy(stream, os);
        };

        return Response.ok(streamOutput).build();

        /*
        ByteArrayOutputStream baos=new ByteArrayOutputStream(1000);
        BufferedImage image = ImageIO.read(new File("d:/space/" + folder + "/" + id + ".jpg"));
        ImageIO.write(image, "jpg", baos);
        byte[] imageData = baos.toByteArray();
        return Response.ok(new ByteArrayInputStream(imageData)).build();
        */
    }


}
