package io.turntabl.opentelemetryexamplejaeger;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.opentelemetry.OpenTelemetry;
import io.opentelemetry.exporters.jaeger.JaegerGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.export.SimpleSpansProcessor;
import io.opentelemetry.trace.Span;
import io.opentelemetry.trace.Tracer;

public class JaegerExample {
    private String ip; // = "jaeger";
    private int port; // = 14250;

    private Tracer tracer =  OpenTelemetry.getTracerFactory().get("io.turntabl.opentelemetryexamplejaeger.JaegerExample");
//            OpenTelemetry.getTracerFactory().get("io.opentelemetry.example.JaegerExample");


    private JaegerGrpcSpanExporter jaegerExporter;

    public JaegerExample(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    private void setupJaegerExporter(){

        ManagedChannel jaegerChannel = ManagedChannelBuilder.forAddress(ip, port).usePlaintext().build();
        this.jaegerExporter =
                JaegerGrpcSpanExporter.newBuilder()
                        .setServiceName("opentelemetryexamplejaeger")
                        .setChannel(jaegerChannel)
                        .setDeadline(30000)
                        .build();

      OpenTelemetrySdk.getTracerFactory()
                .addSpanProcessor(SimpleSpansProcessor.newBuilder(this.jaegerExporter).build());
    }
    private void myWonderfulUseCase() {
        // Generate a span
        Span span = this.tracer.spanBuilder("Start my wonderful use case").startSpan();
        span.addEvent("Event 0");
        // execute my use case - here we simulate a wait
        doWork();
        span.addEvent("Event 1");
        span.end();
    }
    private void doWork() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
    }
    public static void main(String[] args) {
        // Parsing the input
        if (args.length < 2) {
            System.out.println("Missing [hostname] [port]");
            System.exit(1);
        }
        String ip = args[0];
        int port = Integer.parseInt(args[1]);

        // Start the example
        JaegerExample example = new JaegerExample(ip, port);
        example.setupJaegerExporter();
        example.myWonderfulUseCase();
        // wait some seconds
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        System.out.println("Bye");
    }
}
