package com.bigpanda.commons.web.handler;

import com.bigpanda.commons.lut.LutEnum;
import com.bigpanda.commons.web.exceptions.InputValidationException;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by erik on 10/19/17.
 */
public class InputValidatorHandler implements Handler<RoutingContext> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private JsonObject verifications;
    private final Map<String, Validator> validatorMap;
    private final String operation;

    InputValidatorHandler(Vertx vertx, String verificationName, String operation, Map<String, Validator> validatorMap) {
        this.validatorMap = validatorMap;
        this.operation = operation;
        vertx.fileSystem().readFile("verifications/" + verificationName + ".ver.json", result -> {
            if (result.succeeded()) {
                this.verifications = result.result().toJsonObject();
            } else {
                logger.warn("Enable to load verification " + verificationName, result.cause());
            }
        });
    }

    public static InputValidatorHandler create(Vertx vertx, String verificationName, String operation, Map<String, Validator> validatorMap) {
        return new InputValidatorHandler(vertx, verificationName, operation, validatorMap);
    }

    public static InputValidatorHandler create(Vertx vertx, String verificationName, String operation) {
        return new InputValidatorHandler(vertx, verificationName, operation, null);
    }

    public static InputValidatorHandler create(Vertx vertx, String verificationName) {
        return new InputValidatorHandler(vertx, verificationName, null, null);
    }

    public static JsonObject extractVerifications(JsonObject config, String verificationName) {
        JsonObject verifications = config.getJsonObject("verifications");
        if (verifications != null) {
            JsonObject verification = verifications.getJsonObject(verificationName);
            if (verification != null) {
                return verification;
            }
        }
        return new JsonObject();
    }

    @Override
    public void handle(RoutingContext event) {
        // Throws an exception in case the validation fails
        validateInput( event.getBodyAsJson(), verifications, "");

        event.next();
    }

    private boolean validateInput(JsonObject input, JsonObject verifications, String jsonPath) {
        validateUnsupportedInputFields(input, verifications, jsonPath);

        for (Map.Entry<String, Object> entry : verifications.getMap().entrySet()) {

            // Ignore fields that are not JsonObject since they are validations rules (like required, type and ..)
            if (!(entry.getValue() instanceof Map)) {
                continue;
            }

            if (!isAllowedInOperation(verifications, entry.getKey())) {
                continue;
            }

            String fieldName = entry.getKey();
            Object fieldValue = input.getValue(fieldName);
            JsonObject fieldVerification = new JsonObject((Map)entry.getValue());

            if (fieldValue instanceof JsonArray) {
                ((JsonArray) fieldValue).forEach(o -> validateField(fieldVerification, fieldName, o, jsonPath));
            } else {
                validateField(fieldVerification, fieldName, fieldValue, jsonPath);
            }
        }

        return true;
    }

    private void validateField(JsonObject fieldVerification, String fieldName, Object fieldValue, String jsonPath) {

        boolean required = fieldVerification.getBoolean("required", false);
        if (required && (fieldValue == null || fieldValue.toString().length() == 0)) {
//            throw new InputValidationException(jsonPath + fieldName, "is required field");
        }

        // Handle nested object verifications
        String verificationType = fieldVerification.getString("type");
        if (verificationType != null && verificationType.equals("object")) {
            if (fieldValue != null) {
                validateInput((JsonObject) fieldValue, fieldVerification, jsonPath + fieldName + ".");
            }
            return;
        }
        // End of handle nested object verifications

        if (verificationType != null && fieldValue != null) {
            validateType(fieldValue, fieldName, verificationType, jsonPath);
        }

        String regex = fieldVerification.getString("regex");
        if (regex != null && fieldValue != null) {
            if (!fieldValue.toString().matches(regex)) {
//                throw new InputValidationException(jsonPath + fieldName, "regex not matches");
            }
        }

        Integer minlength = fieldVerification.getInteger("min_length");
        Integer maxlength = fieldVerification.getInteger("max_length");
        if (minlength != null && fieldValue != null && fieldValue.toString().length() < minlength) {
//            throw new InputValidationException(jsonPath + fieldName, "below the allowed minimum length");
        }
        if (maxlength != null && fieldValue != null && fieldValue.toString().length() > maxlength) {
//            throw new InputValidationException(jsonPath + fieldName, "above the allowed maximum length");
        }

        if (validatorMap != null) {
            String validatorBean = fieldVerification.getString("validatorBean");
            if (validatorBean != null && fieldValue != null) {
                if (!validatorMap.get(validatorBean).validate(fieldValue.toString())) {
//                    throw new InputValidationException(jsonPath + fieldName, validatorBean + " not valid");
                }
            }
        }

        String enumClazzName = fieldVerification.getString("enum_class");
        validateEnum(fieldName, fieldValue, jsonPath, enumClazzName);

        String enumCodeClazzName = fieldVerification.getString("enum_code_class");
        validateEnumByValue(fieldName, fieldValue, jsonPath, enumCodeClazzName);
    }

    public static void validateEnumByValue(String fieldName, Object fieldValue, String jsonPath, String enumClazzName) {
        if (enumClazzName != null && fieldValue != null) {
            Class<? extends Enum> enumClazz = null;
            try {
                enumClazz = (Class<? extends Enum>) Class.forName(enumClazzName);

                Object value;

                if(fieldValue instanceof Integer) {
                    value = Integer.parseInt(fieldValue.toString());
                }
                else {
                    value = fieldValue.toString();
                }

                LutEnum lutEnum = ((LutEnum) enumClazz.getEnumConstants()[0]).get(value);
                if (lutEnum == null) {
//                    throw new InputValidationException(
//                            jsonPath + fieldName
//                            , String.format("not in the allowed values %s", LutEnum.allowedValues(enumClazz)).toString());
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
//                throw new InputValidationException(
//                        jsonPath + fieldName
//                        , String.format("not in the allowed values %s", LutEnum.allowedValues(enumClazz)).toString());
            }
        }
    }

    public static void validateEnum(String fieldName, Object fieldValue, String jsonPath, String enumClazzName) {
        if (enumClazzName != null && fieldValue != null) {
            Class<? extends Enum> enumClazz = null;
            try {
                enumClazz = (Class<? extends Enum>) Class.forName(enumClazzName);
                Enum.valueOf(enumClazz, fieldValue.toString());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                String allowedValues = Arrays.asList(enumClazz.getEnumConstants()).toString();
//                throw new InputValidationException(jsonPath + fieldName, String.format("not in the allowed values %s", allowedValues));
            }
        }
    }

    private void validateType(Object fieldValue, String fieldName, String verificationType, String jsonPath) {
        if (verificationType.equals("number") && !(fieldValue instanceof Number)) {
//            throw new InputValidationException(jsonPath + fieldName, "not a number data type");
        }
        if (verificationType.equals("boolean") && !(fieldValue instanceof Boolean)) {
//            throw new InputValidationException(jsonPath + fieldName, "not a boolean data type");
        }
    }

    private boolean validateUnsupportedInputFields(JsonObject input, JsonObject verifications, String jsonPath) {
        if (input == null) {
            return true;
        }
        for (String fieldName : input.fieldNames()) {
            if (!verifications.containsKey(fieldName)) {
//                throw new InputValidationException(jsonPath + fieldName, "not allowed in object");
            } else {
                if (!isAllowedInOperation(verifications, fieldName)) {
//                    throw new InputValidationException(jsonPath + fieldName, "not allowed in object for this operation");
                }
            }
        }
        return true;
    }

    private boolean isAllowedInOperation(JsonObject verifications, String fieldName) {
        JsonObject verificationField = verifications.getJsonObject(fieldName);
        Object fieldOperation = verificationField.getValue("operation");
        if (fieldOperation != null) {
            if (fieldOperation instanceof String && !fieldOperation.equals(operation)) {

                return false;
            } else if (fieldOperation instanceof JsonArray && !((JsonArray) fieldOperation).contains(operation)) {

                return false;
            }
        }
        return true;
    }

    public interface Validator {
        boolean validate(String s);
    }

}
