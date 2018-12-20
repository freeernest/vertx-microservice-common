package com.bigpanda.commons.security;

import com.bigpanda.commons.security.annotations.Authorize;
import com.bigpanda.commons.services.proxy.ServiceContext;
import com.bigpanda.commons.web.exceptions.BadRequestException;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.jwt.JWTAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Function;

@Component
@Scope("prototype")
public class AuthorizationInterceptor implements Function<Message<JsonObject>, Future<Message<JsonObject>>> {
    @Autowired
    private JWTAuth jwtAuth;
    private Map<String, Authorize> methodAuthoritiesMap;
    private final static String AUTH_SCHEMA = "Bearer";


    public AuthorizationInterceptor init(Class clazz) {
        methodAuthoritiesMap = buildMethodAuthoritiesMap(clazz);
        return this;
    }

    private Map<String, Authorize> buildMethodAuthoritiesMap(Class clazz) {
        Map<String, Authorize> methodAuthorities = new HashMap<>();

        for(Method method : clazz.getMethods()) {
            Authorize authorize = method.getAnnotation(Authorize.class);

            if(authorize != null) {
                methodAuthorities.put(method.getName(), authorize);
            }
        }

        return methodAuthorities;
    }

    public Future<Message<JsonObject>> apply(Message<JsonObject> msg) {
        Future<Message<JsonObject>> fut = Future.future();
        String methodName = msg.headers().get("action");

        Authorize authorize = methodAuthoritiesMap.get(methodName);

        if (authorize != null && authorize.value().length > 0) {

            if (!msg.headers().contains("context")) {
                fut.fail(new UnauthorizedException());
                return fut;
            }

            ServiceContext context = new ServiceContext(new JsonObject(msg.headers().get("context")));
            final String authorization = context.getAuthToken();

            if (authorization == null) {
                fut.fail(new UnauthorizedException());
            } else {
                int idx = authorization.indexOf(' ');

                if (idx <= 0) {// dont know why i am doing it, it supposed to be only with space
                    idx = authorization.indexOf(':');
                }

                if (idx <= 0) {
                    fut.fail(new BadRequestException());
                    return fut;
                }

                if (!AUTH_SCHEMA.equals(authorization.substring(0, idx))) {
                    fut.fail(new UnauthorizedException());
                    return fut;
                }

                jwtAuth.authenticate(new JsonObject().put("jwt", authorization.substring(idx + 1)), authenticate -> {
                    if (authenticate.failed()) {
                        fut.fail(new UnauthorizedException());
                        return;
                    }

                    final User user = authenticate.result();

                    if (user == null) {
                        fut.fail(new ForbiddenException());
                        return;
                    }

                    HashSet<String> methodAuthorities = new HashSet<>(Arrays.asList(authorize.value()));
                    JsonArray userAuthorities = user.principal().getJsonArray("auths");

                    boolean isAuthorized = userAuthorities.stream().anyMatch(o -> methodAuthorities.contains(o));

                    if (isAuthorized) {
                        context.setPrincipal(user.principal());
                        msg.headers().set("context", context.toJson().encode());

                        fut.complete(msg);
                    } else {
                        fut.fail(new ForbiddenException());
                    }
                });
            }
        }
        else {
            fut.complete(msg);
        }

        return fut;
    }

    public void setJwtAuth(JWTAuth jwtAuth) {
        this.jwtAuth = jwtAuth;
    }
}