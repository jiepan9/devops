package com.photobox.brain.imageserver.http;

import org.vertx.java.core.VoidHandler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpServer;

import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;
import com.codahale.metrics.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by jpan on 1/22/15.
 */
public class ImageServer extends Verticle {
    final MetricRegistry metrics = new MetricRegistry();

    public void start(){
        startTestImageServing(); //8081
        startReadDataFromRequest(); //8082
        startProcessingRequest(); //8080
    }


    private void startProcessingRequest(){

        HttpServer server = vertx.createHttpServer();

        server.requestHandler(request -> {
            StringBuffer sb = new StringBuffer();
            for (Map.Entry<String, String> header: request.headers().entries()){
                sb.append(header.getKey()).append(": ").append(header.getValue()).append("\n");
            }
            for (Map.Entry<String, String> param: request.params().entries()){
                sb.append(param.getKey()).append(": ").append(param.getValue()).append("\n");
            }
            sb.append("remote address; ").append( request.remoteAddress()).append("\n");
            sb.append("path: ").append(request.path()).append("\n");
            sb.append("query: ").append(request.query()).append("\n");
            sb.append("absoluteURI: ").append(request.absoluteURI()).append("\n");
            request.response().putHeader("content-type", "text/plain");
            request.response().end(sb.toString());

          //  Logger log=container.logger();
          //  log.info("A request has arrived on the server!");

        });
        server.listen(8080, "localhost");
    }


    /**
     * No response !!
     */
    private void startReadDataFromRequest(){
        HttpServer server = vertx.createHttpServer();

        server.requestHandler(request -> {

            final Buffer body = new Buffer(0);

            request.dataHandler(buffer -> body.appendBuffer(buffer));
            request.endHandler(new VoidHandler() {
                public void handle() {
                    Logger log=container.logger();
                    // The entire body has now been received
                    log.info("The total body received was " + body.length() + " bytes");
                }
            });

        }).listen(8082, "localhost");
    }


    private void startTestImageServing(){
        final Meter meter = getMeter();
        final Timer timer = getTimer();
        HttpServer server = vertx.createHttpServer();
        server.requestHandler(request -> {
            long start = System.currentTimeMillis();
             Logger log = container.logger();
             String path = request.path();
             request.response().sendFile("d:/space/" + path);
             StringBuffer sb = new StringBuffer();
             for (Map.Entry<String, String> header: request.headers().entries()){
                sb.append(header.getKey()).append(": ").append(header.getValue()).append("\n");
            }
             for (Map.Entry<String, String> param: request.params().entries()){
                sb.append(param.getKey()).append(": ").append(param.getValue()).append("\n");
            }
             sb.append("remote address; ").append( request.remoteAddress()).append("\n");
             sb.append("path: ").append(request.path()).append("\n");
             sb.append("query: ").append(request.query()).append("\n");
             sb.append("absoluteURI: ").append(request.absoluteURI()).append("\n");
             meter.mark();
             long end = System.currentTimeMillis();
             timer.update(end - start, TimeUnit.MILLISECONDS);

             log.info("Request received: " + sb.toString());
             log.info("Count: " + meter.getCount());
             log.info("Timer five minute Rate: " + timer.getFiveMinuteRate());




         });

        server.listen(8081, "localhost");
    }


    public Meter getMeter() {

        final Meter requests = metrics.meter("requestMeter");
        return requests;

    }

    public Timer getTimer(){
        final  Timer requests = metrics.timer("requestTimer");
        return requests;
    }

}
