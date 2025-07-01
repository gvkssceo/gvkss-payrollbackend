package com.payroll.texas.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class CustomFieldsService {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Set a custom field value
     * @param existingCustomFields Current custom fields JSONB
     * @param fieldName Field name to set
     * @param value Value to set
     * @return Updated custom fields JSONB
     */
    public String setCustomField(String existingCustomFields, String fieldName, Object value) {
        try {
            JsonNode customFieldsNode = existingCustomFields != null && !existingCustomFields.isEmpty() 
                ? objectMapper.readTree(existingCustomFields) 
                : objectMapper.createObjectNode();
            
            if (customFieldsNode.isObject()) {
                ObjectNode objectNode = (ObjectNode) customFieldsNode;
                objectNode.set(fieldName, objectMapper.valueToTree(value));
                return objectMapper.writeValueAsString(objectNode);
            }
            
            return objectMapper.writeValueAsString(Map.of(fieldName, value));
        } catch (Exception e) {
            throw new RuntimeException("Failed to set custom field: " + fieldName, e);
        }
    }
    
    /**
     * Get a custom field value
     * @param customFields Custom fields JSONB
     * @param fieldName Field name to get
     * @return Optional value of the field
     */
    public Optional<JsonNode> getCustomField(String customFields, String fieldName) {
        try {
            if (customFields == null || customFields.isEmpty()) {
                return Optional.empty();
            }
            
            JsonNode customFieldsNode = objectMapper.readTree(customFields);
            JsonNode fieldValue = customFieldsNode.get(fieldName);
            
            return Optional.ofNullable(fieldValue);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    /**
     * Get a custom field value as String
     * @param customFields Custom fields JSONB
     * @param fieldName Field name to get
     * @return Optional string value
     */
    public Optional<String> getCustomFieldString(String customFields, String fieldName) {
        return getCustomField(customFields, fieldName)
            .filter(JsonNode::isTextual)
            .map(JsonNode::asText);
    }
    
    /**
     * Get a custom field value as Integer
     * @param customFields Custom fields JSONB
     * @param fieldName Field name to get
     * @return Optional integer value
     */
    public Optional<Integer> getCustomFieldInteger(String customFields, String fieldName) {
        return getCustomField(customFields, fieldName)
            .filter(JsonNode::isNumber)
            .map(JsonNode::asInt);
    }
    
    /**
     * Get a custom field value as Boolean
     * @param customFields Custom fields JSONB
     * @param fieldName Field name to get
     * @return Optional boolean value
     */
    public Optional<Boolean> getCustomFieldBoolean(String customFields, String fieldName) {
        return getCustomField(customFields, fieldName)
            .filter(JsonNode::isBoolean)
            .map(JsonNode::asBoolean);
    }
    
    /**
     * Remove a custom field
     * @param existingCustomFields Current custom fields JSONB
     * @param fieldName Field name to remove
     * @return Updated custom fields JSONB
     */
    public String removeCustomField(String existingCustomFields, String fieldName) {
        try {
            if (existingCustomFields == null || existingCustomFields.isEmpty()) {
                return "{}";
            }
            
            JsonNode customFieldsNode = objectMapper.readTree(existingCustomFields);
            if (customFieldsNode.isObject()) {
                ObjectNode objectNode = (ObjectNode) customFieldsNode;
                objectNode.remove(fieldName);
                return objectMapper.writeValueAsString(objectNode);
            }
            
            return "{}";
        } catch (Exception e) {
            throw new RuntimeException("Failed to remove custom field: " + fieldName, e);
        }
    }
    
    /**
     * Check if a custom field exists
     * @param customFields Custom fields JSONB
     * @param fieldName Field name to check
     * @return True if field exists
     */
    public boolean hasCustomField(String customFields, String fieldName) {
        return getCustomField(customFields, fieldName).isPresent();
    }
    
    /**
     * Get all custom fields as a map
     * @param customFields Custom fields JSONB
     * @return Map of field names to values
     */
    public Map<String, Object> getAllCustomFields(String customFields) {
        try {
            if (customFields == null || customFields.isEmpty()) {
                return Map.of();
            }
            
            JsonNode customFieldsNode = objectMapper.readTree(customFields);
            return objectMapper.convertValue(customFieldsNode, Map.class);
        } catch (Exception e) {
            return Map.of();
        }
    }
    
    /**
     * Set multiple custom fields at once
     * @param existingCustomFields Current custom fields JSONB
     * @param fields Map of field names to values
     * @return Updated custom fields JSONB
     */
    public String setCustomFields(String existingCustomFields, Map<String, Object> fields) {
        try {
            JsonNode customFieldsNode = existingCustomFields != null && !existingCustomFields.isEmpty() 
                ? objectMapper.readTree(existingCustomFields) 
                : objectMapper.createObjectNode();
            
            if (customFieldsNode.isObject()) {
                ObjectNode objectNode = (ObjectNode) customFieldsNode;
                for (Map.Entry<String, Object> entry : fields.entrySet()) {
                    objectNode.set(entry.getKey(), objectMapper.valueToTree(entry.getValue()));
                }
                return objectMapper.writeValueAsString(objectNode);
            }
            
            return objectMapper.writeValueAsString(fields);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set custom fields", e);
        }
    }
    
    /**
     * Validate custom fields JSONB format
     * @param customFields Custom fields JSONB to validate
     * @return True if valid JSONB
     */
    public boolean isValidCustomFields(String customFields) {
        try {
            if (customFields == null || customFields.isEmpty()) {
                return true;
            }
            objectMapper.readTree(customFields);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get custom field with nested path (e.g., "preferences.notifications.email")
     * @param customFields Custom fields JSONB
     * @param path Nested path to the field
     * @return Optional value of the nested field
     */
    public Optional<JsonNode> getNestedCustomField(String customFields, String path) {
        try {
            if (customFields == null || customFields.isEmpty()) {
                return Optional.empty();
            }
            
            JsonNode customFieldsNode = objectMapper.readTree(customFields);
            String[] pathParts = path.split("\\.");
            
            JsonNode current = customFieldsNode;
            for (String part : pathParts) {
                if (current.isObject() && current.has(part)) {
                    current = current.get(part);
                } else {
                    return Optional.empty();
                }
            }
            
            return Optional.of(current);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    /**
     * Set nested custom field
     * @param existingCustomFields Current custom fields JSONB
     * @param path Nested path (e.g., "preferences.notifications.email")
     * @param value Value to set
     * @return Updated custom fields JSONB
     */
    public String setNestedCustomField(String existingCustomFields, String path, Object value) {
        try {
            JsonNode customFieldsNode = existingCustomFields != null && !existingCustomFields.isEmpty() 
                ? objectMapper.readTree(existingCustomFields) 
                : objectMapper.createObjectNode();
            
            String[] pathParts = path.split("\\.");
            
            if (customFieldsNode.isObject()) {
                ObjectNode objectNode = (ObjectNode) customFieldsNode;
                ObjectNode current = objectNode;
                
                // Navigate to the parent of the target field
                for (int i = 0; i < pathParts.length - 1; i++) {
                    String part = pathParts[i];
                    if (!current.has(part) || !current.get(part).isObject()) {
                        current.set(part, objectMapper.createObjectNode());
                    }
                    current = (ObjectNode) current.get(part);
                }
                
                // Set the final field
                current.set(pathParts[pathParts.length - 1], objectMapper.valueToTree(value));
                return objectMapper.writeValueAsString(objectNode);
            }
            
            return objectMapper.writeValueAsString(Map.of(path, value));
        } catch (Exception e) {
            throw new RuntimeException("Failed to set nested custom field: " + path, e);
        }
    }
} 