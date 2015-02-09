package com.photobox.brain.imageserver.http;

import com.codahale.metrics.jvm.BufferPoolMetricSet;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import org.apache.log4j.BasicConfigurator;
import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.ServerWebSocket;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;
import com.codahale.metrics.*;
import java.io.File;
import java.lang.management.ManagementFactory;

import java.util.concurrent.TimeUnit;
import java.lang.management.RuntimeMXBean;


/**
 * Created by jpan on 1/22/15.
 */
public class ImageServer extends Verticle {


    final MetricRegistry metrics = new MetricRegistry();
    final RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
    long start;


    public void start(){
        BasicConfigurator.configure();

        start = System.currentTimeMillis();

        startTestImageServing();
    }

    private void startTestImageServing(){
        Logger log = container.logger();

        // metrics jvm
        metrics.register("jvm.buffers", new BufferPoolMetricSet(ManagementFactory
                .getPlatformMBeanServer()));
        metrics.register("jvm.gc", new GarbageCollectorMetricSet());
        metrics.register("jvm.memory", new MemoryUsageGaugeSet());
        metrics.register("jvm.threads", new ThreadStatesGaugeSet());
        /*CsvReporter csvReporter = CsvReporter.forRegistry(metrics).build(new File("/home/jpan/devops/vertx/image-server/data"));
        csvReporter.start(1, TimeUnit.SECONDS);
        JmxReporter jmxReporter = JmxReporter.forRegistry(metrics).build();
        jmxReporter.start();
        */

        // counter
        Meter meter = getMeter();
        Timer photoTimer = getPhotoTimer();
        Timer metricsTimer = getMetricsTimer();

        HttpServer server = vertx.createHttpServer();
        server.requestHandler(request -> {

             String path = request.path();
             meter.mark();

            log.info("Requested path:" + path);
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

                 sb.append("PhotoTimers:\n");
                 sb.append("count:").append(photoTimer.getCount()).append('\n');
                 sb.append("m15rate:").append(photoTimer.getFifteenMinuteRate()).append('\n');
                 sb.append("m1_rate:").append(photoTimer.getOneMinuteRate()).append('\n');
                 sb.append("m5_rate:").append(photoTimer.getFiveMinuteRate()).append('\n');
                 sb.append("mean_rate:").append(photoTimer.getMeanRate()).append('\n');

                 sb.append("snapshot:\n");
                 sb.append("75th percentile:").append(photoTimer.getSnapshot().get75thPercentile()).append('\n');
                 sb.append("95th percentile:").append(photoTimer.getSnapshot().get95thPercentile()).append('\n');
                 sb.append("98th percentile:").append(photoTimer.getSnapshot().get98thPercentile()).append('\n');
                 sb.append("999th percentile:").append(photoTimer.getSnapshot().get999thPercentile()).append('\n');
                 sb.append("99th percentile:").append(photoTimer.getSnapshot().get99thPercentile()).append('\n');
                 sb.append("max:").append(photoTimer.getSnapshot().getMax()).append('\n');
                 sb.append("mean:").append(photoTimer.getSnapshot().getMean()).append('\n');
                 sb.append("median:").append(photoTimer.getSnapshot().getMedian()).append('\n');
                 sb.append("min:").append(photoTimer.getSnapshot().getMin()).append('\n');
                 sb.append("stdDev:").append(photoTimer.getSnapshot().getStdDev()).append('\n');

                 sb.append("MetricsTimers:\n");
                 sb.append("count:").append(metricsTimer.getCount()).append('\n');
                 sb.append("m15rate:").append(metricsTimer.getFifteenMinuteRate()).append('\n');
                 sb.append("m1_rate:").append(metricsTimer.getOneMinuteRate()).append('\n');
                 sb.append("m5_rate:").append(metricsTimer.getFiveMinuteRate()).append('\n');
                 sb.append("mean_rate:").append(metricsTimer.getMeanRate()).append('\n');

                 sb.append("snapshot:\n");
                 sb.append("75th percentile:").append(metricsTimer.getSnapshot().get75thPercentile()).append('\n');
                 sb.append("95th percentile:").append(metricsTimer.getSnapshot().get95thPercentile()).append('\n');
                 sb.append("98th percentile:").append(metricsTimer.getSnapshot().get98thPercentile()).append('\n');
                 sb.append("999th percentile:").append(metricsTimer.getSnapshot().get999thPercentile()).append('\n');
                 sb.append("99th percentile:").append(metricsTimer.getSnapshot().get99thPercentile()).append('\n');
                 sb.append("max:").append(metricsTimer.getSnapshot().getMax()).append('\n');
                 sb.append("mean:").append(metricsTimer.getSnapshot().getMean()).append('\n');
                 sb.append("median:").append(metricsTimer.getSnapshot().getMedian()).append('\n');
                 sb.append("min:").append(metricsTimer.getSnapshot().getMin()).append('\n');
                 sb.append("stdDev:").append(metricsTimer.getSnapshot().getStdDev()).append('\n');


                 request.response().end(sb.toString());
                 metricsTimer.update(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);

             } else  {
                 request.response().sendFile("d:/space/" + path);
                 photoTimer.update(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
             }
         });
//        server.setAcceptBacklog(10000);
       // server.setSendBufferSize(4 * 1024);
       // server.setReceiveBufferSize(4 * 1024);
        server.listen(8081);
    }


    public Meter getMeter() {

        Meter meter = metrics.meter("requestMeter");
        return meter;

    }

    public Timer getPhotoTimer() {
        Timer timer = metrics.timer("requestPhotoTimer");
        return timer;
    }

    public Timer getMetricsTimer(){
        Timer timer = metrics.timer("requestMetricsTimer");
        return timer;
    }



}
