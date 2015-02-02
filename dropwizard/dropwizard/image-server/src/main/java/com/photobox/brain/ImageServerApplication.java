package com.photobox.brain;



import com.codahale.metrics.servlets.MetricsServlet;
import com.photobox.brain.resource.PhotoResource;
import io.dropwizard.Application;
import io.dropwizard.Configuration;

/**
 * Created by jpan on 15-1-27.
 */
public class ImageServerApplication extends Application<Configuration> {


    public static void main(String[] args) throws Exception {

        new ImageServerApplication().run(args);
    }
    @Override
    public void run(Configuration conf, io.dropwizard.setup.Environment env) throws Exception {
        env.jersey().register(new PhotoResource());
        //env.servlets().addServlet("metrics-servlet",
           // new MetricsServlet(env.metrics())).addMapping("/metrics");
    }


}
