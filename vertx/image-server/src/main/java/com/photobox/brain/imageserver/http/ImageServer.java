package com.photobox.brain.imageserver.http;

import com.codahale.metrics.jvm.BufferPoolMetricSet;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import org.apache.log4j.BasicConfigurator;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;
import com.codahale.metrics.*;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;
import java.lang.management.RuntimeMXBean;

/**
 * Created by jpan on 1/22/15.
 */
public class ImageServer extends Verticle {


    final MetricRegistry metrics = new MetricRegistry();
    final RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();


    public void start(){
        BasicConfigurator.configure();
        startTestImageServing();
    }

    private void startTestImageServing(){
        // metrics jvm
        metrics.register("jvm.buffers", new BufferPoolMetricSet(ManagementFactory
                .getPlatformMBeanServer()));
        metrics.register("jvm.gc", new GarbageCollectorMetricSet());
        metrics.register("jvm.memory", new MemoryUsageGaugeSet());
        metrics.register("jvm.threads", new ThreadStatesGaugeSet());
        CsvReporter csvReporter = CsvReporter.forRegistry(metrics).build(new File("/home/jpan/devops/vertx/image-server/data"));
        csvReporter.start(1, TimeUnit.SECONDS);
        JmxReporter jmxReporter = JmxReporter.forRegistry(metrics).build();
        jmxReporter.start();

        // counter
        final Meter meter = getMeter();
        HttpServer server = vertx.createHttpServer();
        server.requestHandler(request -> {
             Logger log = container.logger();
             String path = request.path();
             meter.mark();
             if (path.contains("metrics")) {
                 //metrics
                 StringBuffer sb = new StringBuffer();
                 sb.append("Gauges\n");
                 metrics.getGauges().forEach((k, v) -> {
                     sb.append(k + ":" + v.getValue()).append('\n');
                 });
                 sb.append("Meters:\n" );
                 sb.append("count:").append(meter.getCount()).append('\n');
                 sb.append("m15_rate:").append(meter.getFifteenMinuteRate()).append('\n');
                 sb.append("m1_rate:").append(meter.getOneMinuteRate()).append('\n');
                 sb.append("m5_rate:").append(meter.getFiveMinuteRate()).append('\n');
                 sb.append("mean_rate:").append(meter.getMeanRate()).append('\n');
                 sb.append("units:").append(meter.)

                 request.response().end(sb.toString());

             } else {
                 request.response().sendFile("/space/" + path);
             }





            log.info("Count: " + counter.getCount());

         });

        server.listen(8081, "localhost");
    }


    public Meter getMeter() {

        final Meter requests = metrics.meter("requestMeter");
        return requests;

    }

}
