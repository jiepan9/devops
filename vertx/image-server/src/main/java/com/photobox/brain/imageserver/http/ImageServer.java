package com.photobox.brain.imageserver.http;

import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.VoidHandler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;

import java.util.Map;

/**
 * Created by jpan on 1/22/15.
 */
public class ImageServer extends Verticle {

    public void start(){

        //Vertx.vertx(new VertxOptions().setMetricsEnabled(true));

        startTestImageServing();

    }

/*
    private void startProcessingRequest(){

        HttpServer server = vertx.createHttpServer();

        server.requestHandler(new Handler<HttpServerRequest>() {
            @Override
            public void handle(HttpServerRequest request) {
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

            }
        });
        server.listen(8080, "localhost");
    }
    */

/*
    private void startReadDataFromRequest(){
        HttpServer server = vertx.createHttpServer();

        server.requestHandler(new Handler<HttpServerRequest>() {
            public void handle(HttpServerRequest request) {

                final Buffer body = new Buffer(0);

                request.dataHandler(new Handler<Buffer>() {
                    public void handle(Buffer buffer) {
                        body.appendBuffer(buffer);
                    }
                });
                request.endHandler(new VoidHandler() {
                    public void handle() {
                        Logger log=container.logger();
                        // The entire body has now been received
                        log.info("The total body received was " + body.length() + " bytes");
                    }
                });

            }
        }).listen(8080, "localhost");
    }
*/

    private void startTestImageServing(){

        HttpServer server = vertx.createHttpServer();
        server.requestHandler(new Handler<HttpServerRequest>(){

           @Override
            public void handle(HttpServerRequest request) {
                Logger log = container.logger();
                String path = request.path();
                request.response().sendFile("/space/" + path);
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


               log.info("Request received: " + sb.toString());
            }
        });

        server.listen(8081, "localhost");
    }
}
