package com.bigpanda.commons.web.resourcehandlers;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.Status;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by erik on 9/20/18.
 */
public class HealthCheckResourceHandler extends AbstractResourceHandler {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private List<String> procedures;

    @Override
    public Router createRouter(Vertx vertx) {
        Router router = Router.router(vertx);

        HealthCheckHandler healthCheckHandler = HealthCheckHandler.create(vertx);

        procedures.forEach(procedure ->

                healthCheckHandler.register(procedure, procedureEvent -> {

                    vertx.eventBus().<Boolean>send("checks." + procedure, null, replyEvent -> {

                        if (replyEvent.succeeded()) {

                            procedureEvent.complete(replyEvent.result().body() ? Status.OK(): Status.KO());
                        } else {

                            if (replyEvent.cause() instanceof ReplyException
                                    && ((ReplyException) replyEvent.cause()).failureCode() == 919) {
                                procedureEvent.complete(Status.KO(new JsonObject(replyEvent.cause().getMessage())));
                            } else {
                                procedureEvent.fail(replyEvent.cause());
                                logger.error("An Error", replyEvent.cause());
                            }
                        }
                    });
                }));

        router.get("/").handler(healthCheckHandler);

        return router;
    }

    public void setProcedures(List<String> procedures) {
        this.procedures = procedures;
    }
}
