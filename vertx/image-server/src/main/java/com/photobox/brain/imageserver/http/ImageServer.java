package com.photobox.brain.imageserver.http;

import com.codahale.metrics.jvm.BufferPoolMetricSet;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import org.apache.log4j.BasicConfigurator;
import org.vertx.java.core.http.HttpServer;
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
        start = System.currentTimeMillis();
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
        final Timer timer = getTimer();
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

                 sb.append("Timers:\n");
                 sb.append("count:").append(timer.getCount()).append('\n');
                 sb.append("m15rate:").append(timer.getFifteenMinuteRate()).append('\n');
                 sb.append("m1_rate:").append(timer.getOneMinuteRate()).append('\n');
                 sb.append("m5_rate:").append(timer.getFiveMinuteRate()).append('\n');
                 sb.append("mean_rate:").append(timer.getMeanRate()).append('\n');

                 sb.append("snapshot:\n");
                 sb.append("75th percentile:").append(timer.getSnapshot().get75thPercentile()).append('\n');
                 sb.append("95th percentile:").append(timer.getSnapshot().get95thPercentile()).append('\n');
                 sb.append("98th percentile:").append(timer.getSnapshot().get98thPercentile()).append('\n');
                 sb.append("999th percentile:").append(timer.getSnapshot().get999thPercentile()).append('\n');
                 sb.append("99th percentile:").append(timer.getSnapshot().get99thPercentile()).append('\n');
                 sb.append("max:").append(timer.getSnapshot().getMax()).append('\n');
                 sb.append("mean:").append(timer.getSnapshot().getMean()).append('\n');
                 sb.append("median:").append(timer.getSnapshot().getMedian()).append('\n');
                 sb.append("min:").append(timer.getSnapshot().getMin()).append('\n');
                 sb.append("stdDev:").append(timer.getSnapshot().getStdDev()).append('\n');

                 request.response().end(sb.toString());
                 timer.update(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);

             } else {
                 request.response().sendFile("/space/" + path);
                 timer.update(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
             }







         });

        server.listen(8081, "localhost");
    }


    public Meter getMeter() {

        final Meter meter = metrics.meter("requestMeter");
        return meter;

    }

    public Timer getTimer() {
        final Timer timer = metrics.timer("requestTimer");
        return timer;
    }


}
