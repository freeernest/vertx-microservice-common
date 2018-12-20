package com.bigpanda.commons.validations;

import com.bigpanda.commons.validations.anotations.Operation;
import com.bigpanda.commons.web.exceptions.InputValidationException;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Component
@Scope("prototype")
public class ValidatorInterceptorImpl implements ValidatorInterceptor {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private static Map<String, JsonObject> verifications;
    private Map<String, String> methodOperations;
    private String verificationName;
    @Autowired
    private InputValidator inputValidator;

    static {
        verifications = new HashMap<>();
    }

    @Override
    public ValidatorInterceptor init(Vertx vertx, Class clazz) {
        this.methodOperations = buildMethodsOperations(clazz);
        this.verificationName = clazz.getSimpleName();

        synchronized (verifications) {

            if (verifications.get(verificationName) == null) {

                JsonObject verification = vertx.fileSystem().readFileBlocking("verifications/" + verificationName + ".ver.json").toJsonObject();
                verifications.put(verificationName, verification);
            }
        }
        return this;
    }

    @Override
    public Future<Message<JsonObject>> apply(Message<JsonObject> jsonObjectMessage) {

        if (jsonObjectMessage.body().size() == 1) {
            Object body = jsonObjectMessage.body().getValue(jsonObjectMessage.body().fieldNames().iterator().next());
            if (body instanceof JsonObject) {

                String method = jsonObjectMessage.headers().get("action");
                String allowedOperation = methodOperations.get(method);

                // Will throw an exception in case input is invalid
                try {
                    JsonObject methodVerifications = verifications.get(verificationName).getJsonObject(method);
                    if (methodVerifications == null) {
                        logger.warn("No verifications for {} for method {}", verificationName, method);
                        return Future.succeededFuture(jsonObjectMessage);
                    }
                    JsonArray errors = new JsonArray();
                    inputValidator.validateInput((JsonObject) body, methodVerifications, allowedOperation, "", errors);

                    if (!errors.isEmpty()) {
                        jsonObjectMessage.reply(new InputValidationException(errors));
                        logger.error("Error in method: " + method + " - " + errors.toString());
                        return Future.failedFuture(new ReplyException(null, -1, ""));
                    }
                } catch (Exception e) {

                    logger.error("An Error", e);
                    return Future.failedFuture(new ServiceException(500, e.getMessage()));
                }
            }
        }
        return Future.succeededFuture(jsonObjectMessage);
    }

    private Map<String, String> buildMethodsOperations(Class clazz) {
        Map<String, String> methodOperations = new HashMap<>();
        for (Method method : clazz.getMethods()) {
            Operation operation = method.getAnnotation(Operation.class);
            if (operation != null) {
                methodOperations.put(method.getName(), operation.value());
            }
        }
        return methodOperations;
    }
}
