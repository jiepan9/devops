package com.photobox.brain;




import com.photobox.brain.resource.PhotoResource;
import io.dropwizard.Application;

/**
 * Created by jpan on 15-1-27.
 */
public class ImageServerApplication extends Application<ImageServerConfiguration> {


    public static void main(String[] args) throws Exception {

        new ImageServerApplication().run(args);
    }
    @Override
    public void run(ImageServerConfiguration conf, io.dropwizard.setup.Environment env) throws Exception {
        env.jersey().register(new PhotoResource());
        //env.servlets().addServlet("metrics-servlet",
        // new MetricsServlet(env.metrics())).addMapping("/metrics");
    }


}
