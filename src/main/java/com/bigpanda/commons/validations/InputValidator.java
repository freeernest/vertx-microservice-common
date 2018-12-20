package com.bigpanda.commons.validations;

import com.bigpanda.commons.dictionary.DictionaryService;
import com.bigpanda.commons.lut.LutEnum;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by erik on 4/11/18.
 */
@Component
public class InputValidator {

    @Autowired(required = false)
    private DictionaryService dictionaryService;

    public boolean validateInput(JsonObject input, JsonObject verifications, String operation, String jsonPath, JsonArray errors) {
        validateUnsupportedInputFields(input, verifications, operation, jsonPath, errors);

        for (Map.Entry<String, Object> entry : verifications.getMap().entrySet()) {

            // Ignore fields that are not JsonObject since they are validations rules (like required, type and ..)
            if (!(entry.getValue() instanceof Map)) {
                continue;
            }

            if (!isAllowedInOperation(verifications, entry.getKey(), operation)) {
                continue;
            }

            String fieldName = entry.getKey();
            Object fieldValue = input.getValue(fieldName);
            JsonObject fieldVerification = new JsonObject((Map)entry.getValue());

            if (fieldValue instanceof JsonArray) {
                ((JsonArray) fieldValue).forEach(o -> validateField(fieldVerification, fieldName, o, operation, jsonPath, errors));
            } else {
                validateField(fieldVerification, fieldName, fieldValue, operation, jsonPath, errors);
            }
        }

        return true;
    }

    private void validateField(JsonObject fieldVerification, String fieldName, Object fieldValue, String operation, String jsonPath, JsonArray errors) {

        boolean required = fieldVerification.getBoolean("required", false);
        if (required && (fieldValue == null || fieldValue.toString().length() == 0)) {
            addError(errors, jsonPath + fieldName, "is required field");
//            throw new InputValidationException(jsonPath + fieldName, "is required field");
        }

        // Handle nested object verifications
        String verificationType = fieldVerification.getString("type");
        if (verificationType != null && verificationType.equals("object")) {
            if (fieldValue != null) {
                validateInput((JsonObject) fieldValue, fieldVerification, operation, jsonPath + fieldName + ".", errors);
            }
            return;
        }
        // End of handle nested object verifications

        if (verificationType != null && fieldValue != null) {
            validateType(fieldValue, fieldName, verificationType, jsonPath, errors);
        }

        String regex = fieldVerification.getString("regex");
        if (regex != null && fieldValue != null) {
            if (!fieldValue.toString().matches(regex)) {
                addError(errors, jsonPath + fieldName, "regex not matches");
//                throw new InputValidationException(jsonPath + fieldName, "regex not matches");
            }
        }

        Integer minlength = fieldVerification.getInteger("min_length");
        Integer maxlength = fieldVerification.getInteger("max_length");
        if (minlength != null && fieldValue != null && fieldValue.toString().length() < minlength) {
            addError(errors, jsonPath + fieldName, "below the allowed minimum length");
//            throw new InputValidationException(jsonPath + fieldName, "below the allowed minimum length");
        }
        if (maxlength != null && fieldValue != null && fieldValue.toString().length() > maxlength) {
            addError(errors, jsonPath + fieldName, "above the allowed maximum length");
//            throw new InputValidationException(jsonPath + fieldName, "above the allowed maximum length");
        }

        validateFieldValueRange(fieldVerification, fieldName, fieldValue, jsonPath, errors);

        String enumClazzName = fieldVerification.getString("enum_class");
        validateEnum(fieldName, fieldValue, jsonPath, enumClazzName, errors);

        String enumCodeClazzName = fieldVerification.getString("enum_value_class");
        validateEnumByValue(fieldName, fieldValue, jsonPath, enumCodeClazzName, errors);

        String dictionary = fieldVerification.getString("dictionary");
        validateDictionary(fieldName, fieldValue, jsonPath, dictionary, errors);
    }

    private void validateDictionary(String fieldName, Object fieldValue, String jsonPath, String dictionary, JsonArray errors) {
        if (dictionary != null && fieldValue != null) {
            try {
                if (!dictionaryService.isValidValue(dictionary, fieldValue.toString())) {
                    addError(errors, jsonPath + fieldName, "not in the allowed values of dictionary '" + dictionary + "'");
                }
            } catch (ServiceException e) {
                addError(errors, jsonPath + fieldName, "'" + dictionary + "' dictionary does not exist");
            }
        }
    }

    private void validateFieldValueRange(JsonObject fieldVerification, String fieldName, Object fieldValue, String jsonPath, JsonArray errors) {
        try{
            if(fieldValue != null && (fieldVerification.containsKey("min_value") || fieldVerification.containsKey("max_value"))) {
                Double minValue = fieldVerification.getDouble("min_value");
                Double maxValue = fieldVerification.getDouble("max_value");

                Double fieldValueDouble = Double.parseDouble(fieldValue.toString());

                if (minValue != null && fieldValueDouble.compareTo(minValue) < 0) {
                    String minValueToPresent = minValue == minValue.intValue() ? String.valueOf(minValue.intValue()) : minValue.toString();

                    addError(errors, jsonPath + fieldName, "below the allowed minimum value " + minValueToPresent);
//                throw new InputValidationException(jsonPath + fieldName, "below the allowed minimum value");
                }
                if(maxValue != null && fieldValueDouble.compareTo(maxValue) > 0) {
                    String maxValueToPresent = maxValue == maxValue.intValue() ? String.valueOf(maxValue.intValue()) : maxValue.toString();

                    addError(errors, jsonPath + fieldName, "above the allowed maximum value " + maxValueToPresent );
//                throw new InputValidationException(jsonPath + fieldName, "below the allowed maximum value");
                }
            }
        } catch (NumberFormatException ex){
            addError(errors, jsonPath + fieldName, "Should be numeric!");
        }
    }

    public void validateEnumByValue(String fieldName, Object fieldValue, String jsonPath, String enumClazzName, JsonArray errors) {
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
                    addError(errors, jsonPath + fieldName, String.format("not in the allowed values %s", LutEnum.allowedValues(enumClazz)).toString());
//                    throw new InputValidationException(
//                            jsonPath + fieldName
//                            , String.format("not in the allowed values %s", LutEnum.allowedValues(enumClazz)).toString());
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                addError(errors, jsonPath + fieldName, String.format("not in the allowed values %s", LutEnum.allowedValues(enumClazz)).toString());
//                throw new InputValidationException(
//                        jsonPath + fieldName
//                        , String.format("not in the allowed values %s", LutEnum.allowedValues(enumClazz)).toString());
            }
        }
    }

    public void validateEnum(String fieldName, Object fieldValue, String jsonPath, String enumClazzName, JsonArray errors) {
        if (enumClazzName != null && fieldValue != null) {
            Class<? extends Enum> enumClazz = null;
            try {
                enumClazz = (Class<? extends Enum>) Class.forName(enumClazzName);
                Enum.valueOf(enumClazz, fieldValue.toString());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                String allowedValues = Arrays.asList(enumClazz.getEnumConstants()).toString();
                addError(errors, jsonPath + fieldName, String.format("not in the allowed values %s", allowedValues));
//                throw new InputValidationException(jsonPath + fieldName, String.format("not in the allowed values %s", allowedValues));
            }
        }
    }

    private void validateType(Object fieldValue, String fieldName, String verificationType, String jsonPath, JsonArray errors) {
        if (verificationType.equals("number") && !(fieldValue instanceof Number)) {
            addError(errors, jsonPath + fieldName, "not a number data type");
//            throw new InputValidationException(jsonPath + fieldName, "not a number data type");
        } else
        if (verificationType.equals("boolean") && !(fieldValue instanceof Boolean)) {
            addError(errors, jsonPath + fieldName, "not a boolean data type");
//            throw new InputValidationException(jsonPath + fieldName, "not a boolean data type");
        } else
        if (verificationType.equals("string") && !(fieldValue instanceof String)) {
            addError(errors, jsonPath + fieldName, "not a string data type");
        }
        if (verificationType.equals("json")) {
            try {
                new JsonObject(fieldValue.toString());
            } catch (DecodeException e) {
                addError(errors, jsonPath + fieldName, "Failed to decode json: " + e.getMessage());
            }
        }
    }

    private boolean validateUnsupportedInputFields(JsonObject input, JsonObject verifications, String operation, String jsonPath, JsonArray errors) {
        if (input == null) {
            return true;
        }
        for (String fieldName : input.fieldNames()) {
            if (!verifications.containsKey(fieldName)) {
                addError(errors, jsonPath + fieldName, "not allowed in object");
//                throw new InputValidationException(jsonPath + fieldName, "not allowed in object");
            } else {
                if (!isAllowedInOperation(verifications, fieldName, operation)) {
                    addError(errors, jsonPath + fieldName, "not allowed in object for this operation");
//                    throw new InputValidationException(jsonPath + fieldName, "not allowed in object for this operation");
                }
            }
        }
        return true;
    }

    private boolean isAllowedInOperation(JsonObject verifications, String fieldName, String operation) {
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

    public static JsonArray addError(JsonArray errors, String field, String error) {
        return errors.add(new JsonObject().put("field", field).put("error", error));
    }

    public static JsonArray addError(String field, String error) {

        return addError(new JsonArray(), field, error);
    }
}
