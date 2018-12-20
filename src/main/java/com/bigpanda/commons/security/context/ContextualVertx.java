package com.bigpanda.commons.security.context;

import io.netty.channel.EventLoopGroup;
import io.vertx.core.*;
import io.vertx.core.datagram.DatagramSocket;
import io.vertx.core.datagram.DatagramSocketOptions;
import io.vertx.core.dns.DnsClient;
import io.vertx.core.dns.DnsClientOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.file.FileSystem;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import io.vertx.core.shareddata.SharedData;
import io.vertx.core.spi.VerticleFactory;

import java.util.Set;
import java.util.function.Supplier;

public class ContextualVertx implements Vertx {

    private Vertx vertx;
    private EventBus eventBus;

    public ContextualVertx(Vertx vertx) {
        this.vertx = vertx;
        this.eventBus = new ContextualEventBus(vertx);
    }

    public static Vertx vertx() {
        return Vertx.vertx();
    }

    public static Vertx vertx(VertxOptions options) {
        return Vertx.vertx(options);
    }

    public static void clusteredVertx(VertxOptions options, Handler<AsyncResult<Vertx>> resultHandler) {
        Vertx.clusteredVertx(options, resultHandler);
    }

    @io.vertx.codegen.annotations.Nullable
    public static Context currentContext() {
        return Vertx.currentContext();
    }

    @Override
    public Context getOrCreateContext() {
        return vertx.getOrCreateContext();
    }

    @Override
    public NetServer createNetServer(NetServerOptions options) {
        return vertx.createNetServer(options);
    }

    @Override
    public NetServer createNetServer() {
        return vertx.createNetServer();
    }

    @Override
    public NetClient createNetClient(NetClientOptions options) {
        return vertx.createNetClient(options);
    }

    @Override
    public NetClient createNetClient() {
        return vertx.createNetClient();
    }

    @Override
    public HttpServer createHttpServer(HttpServerOptions options) {
        return vertx.createHttpServer(options);
    }

    @Override
    public HttpServer createHttpServer() {
        return vertx.createHttpServer();
    }

    @Override
    public HttpClient createHttpClient(HttpClientOptions options) {
        return vertx.createHttpClient(options);
    }

    @Override
    public HttpClient createHttpClient() {
        return vertx.createHttpClient();
    }

    @Override
    public DatagramSocket createDatagramSocket(DatagramSocketOptions options) {
        return vertx.createDatagramSocket(options);
    }

    @Override
    public DatagramSocket createDatagramSocket() {
        return vertx.createDatagramSocket();
    }

    @Override
    @io.vertx.codegen.annotations.CacheReturn
    public FileSystem fileSystem() {
        return vertx.fileSystem();
    }

    @Override
    @io.vertx.codegen.annotations.CacheReturn
    public EventBus eventBus() {
        return eventBus;
    }

    @Override
    public DnsClient createDnsClient(int port, String host) {
        return vertx.createDnsClient(port, host);
    }

    @Override
    public DnsClient createDnsClient() {
        return vertx.createDnsClient();
    }

    @Override
    public DnsClient createDnsClient(DnsClientOptions options) {
        return vertx.createDnsClient(options);
    }

    @Override
    @io.vertx.codegen.annotations.CacheReturn
    public SharedData sharedData() {
        return vertx.sharedData();
    }

    @Override
    public long setTimer(long delay, Handler<Long> handler) {
        return vertx.setTimer(delay, handler);
    }

    @Override
    public TimeoutStream timerStream(long delay) {
        return vertx.timerStream(delay);
    }

    @Override
    public long setPeriodic(long delay, Handler<Long> handler) {
        return vertx.setPeriodic(delay, handler);
    }

    @Override
    public TimeoutStream periodicStream(long delay) {
        return vertx.periodicStream(delay);
    }

    @Override
    public boolean cancelTimer(long id) {
        return vertx.cancelTimer(id);
    }

    @Override
    public void runOnContext(Handler<Void> action) {
        vertx.runOnContext(action);
    }

    @Override
    public void close() {
        vertx.close();
    }

    @Override
    public void close(Handler<AsyncResult<Void>> completionHandler) {
        vertx.close(completionHandler);
    }

    @Override
    @io.vertx.codegen.annotations.GenIgnore
    public void deployVerticle(Verticle verticle) {
        vertx.deployVerticle(verticle);
    }

    @Override
    @io.vertx.codegen.annotations.GenIgnore
    public void deployVerticle(Verticle verticle, Handler<AsyncResult<String>> completionHandler) {
        vertx.deployVerticle(verticle, completionHandler);
    }

    @Override
    @io.vertx.codegen.annotations.GenIgnore
    public void deployVerticle(Verticle verticle, DeploymentOptions options) {
        vertx.deployVerticle(verticle, options);
    }

    @Override
    @io.vertx.codegen.annotations.GenIgnore
    public void deployVerticle(Class<? extends Verticle> verticleClass, DeploymentOptions options) {
        vertx.deployVerticle(verticleClass, options);
    }

    @Override
    @io.vertx.codegen.annotations.GenIgnore
    public void deployVerticle(Supplier<Verticle> verticleSupplier, DeploymentOptions options) {
        vertx.deployVerticle(verticleSupplier, options);
    }

    @Override
    @io.vertx.codegen.annotations.GenIgnore
    public void deployVerticle(Verticle verticle, DeploymentOptions options, Handler<AsyncResult<String>> completionHandler) {
        vertx.deployVerticle(verticle, options, completionHandler);
    }

    @Override
    @io.vertx.codegen.annotations.GenIgnore
    public void deployVerticle(Class<? extends Verticle> verticleClass, DeploymentOptions options, Handler<AsyncResult<String>> completionHandler) {
        vertx.deployVerticle(verticleClass, options, completionHandler);
    }

    @Override
    @io.vertx.codegen.annotations.GenIgnore
    public void deployVerticle(Supplier<Verticle> verticleSupplier, DeploymentOptions options, Handler<AsyncResult<String>> completionHandler) {
        vertx.deployVerticle(verticleSupplier, options, completionHandler);
    }

    @Override
    public void deployVerticle(String name) {
        vertx.deployVerticle(name);
    }

    @Override
    public void deployVerticle(String name, Handler<AsyncResult<String>> completionHandler) {
        vertx.deployVerticle(name, completionHandler);
    }

    @Override
    public void deployVerticle(String name, DeploymentOptions options) {
        vertx.deployVerticle(name, options);
    }

    @Override
    public void deployVerticle(String name, DeploymentOptions options, Handler<AsyncResult<String>> completionHandler) {
        vertx.deployVerticle(name, options, completionHandler);
    }

    @Override
    public void undeploy(String deploymentID) {
        vertx.undeploy(deploymentID);
    }

    @Override
    public void undeploy(String deploymentID, Handler<AsyncResult<Void>> completionHandler) {
        vertx.undeploy(deploymentID, completionHandler);
    }

    @Override
    public Set<String> deploymentIDs() {
        return vertx.deploymentIDs();
    }

    @Override
    @io.vertx.codegen.annotations.GenIgnore
    public void registerVerticleFactory(VerticleFactory factory) {
        vertx.registerVerticleFactory(factory);
    }

    @Override
    @io.vertx.codegen.annotations.GenIgnore
    public void unregisterVerticleFactory(VerticleFactory factory) {
        vertx.unregisterVerticleFactory(factory);
    }

    @Override
    @io.vertx.codegen.annotations.GenIgnore
    public Set<VerticleFactory> verticleFactories() {
        return vertx.verticleFactories();
    }

    @Override
    public boolean isClustered() {
        return vertx.isClustered();
    }

    @Override
    public <T> void executeBlocking(Handler<Future<T>> blockingCodeHandler, boolean ordered, Handler<AsyncResult<T>> asyncResultHandler) {
        vertx.executeBlocking(blockingCodeHandler, ordered, asyncResultHandler);
    }

    @Override
    public <T> void executeBlocking(Handler<Future<T>> blockingCodeHandler, Handler<AsyncResult<T>> asyncResultHandler) {
        vertx.executeBlocking(blockingCodeHandler, asyncResultHandler);
    }

    @Override
    @io.vertx.codegen.annotations.GenIgnore
    public EventLoopGroup nettyEventLoopGroup() {
        return vertx.nettyEventLoopGroup();
    }

    @Override
    public WorkerExecutor createSharedWorkerExecutor(String name) {
        return vertx.createSharedWorkerExecutor(name);
    }

    @Override
    public WorkerExecutor createSharedWorkerExecutor(String name, int poolSize) {
        return vertx.createSharedWorkerExecutor(name, poolSize);
    }

    @Override
    public WorkerExecutor createSharedWorkerExecutor(String name, int poolSize, long maxExecuteTime) {
        return vertx.createSharedWorkerExecutor(name, poolSize, maxExecuteTime);
    }

    @Override
    @io.vertx.codegen.annotations.CacheReturn
    public boolean isNativeTransportEnabled() {
        return vertx.isNativeTransportEnabled();
    }

    @Override
    @io.vertx.codegen.annotations.Fluent
    public Vertx exceptionHandler(@io.vertx.codegen.annotations.Nullable Handler<Throwable> handler) {
        return vertx.exceptionHandler(handler);
    }

    @Override
    @io.vertx.codegen.annotations.Nullable
    @io.vertx.codegen.annotations.GenIgnore
    public Handler<Throwable> exceptionHandler() {
        return vertx.exceptionHandler();
    }

    @Override
    public boolean isMetricsEnabled() {
        return vertx.isMetricsEnabled();
    }

}
