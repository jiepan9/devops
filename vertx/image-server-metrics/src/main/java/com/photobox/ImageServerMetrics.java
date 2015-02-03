package com.photobox;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by jpan on 15-2-3.
 */
public class ImageServerMetrics {

  
  
  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx(new VertxOptions().setMetricsEnabled(true).setJmxEnabled(true));
    
    AtomicLong expected = new AtomicLong();
    HttpServer server = vertx.createHttpServer( new HttpServerOptions().setHost("localhost").setPort(8080)).requestHandler(req ->{
      expected.incrementAndGet();
      String path = req.path();
      req.response().sendFile("/space/" + path);
      
      
    });

    Map<String, JsonObject> metrics = server.metrics();
    System.out.print(metrics.get("requests"));// requests
    System.out.print(metrics.get("bytes-written"));
    System.out.print(metrics.get("bytes-read"));
    System.out.print(metrics.get("exceptions"));

  }
}
