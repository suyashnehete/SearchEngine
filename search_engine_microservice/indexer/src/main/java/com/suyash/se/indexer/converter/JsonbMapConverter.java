package com.suyash.se.indexer.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;
import java.util.Map;

@Converter(autoApply = true)
public class JsonbMapConverter implements AttributeConverter<Map<Integer, Double>, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<Integer, Double> map) {
        try {
            return objectMapper.writeValueAsString(map); // Convert to JSON string
        } catch (IOException e) {
            throw new RuntimeException("Error converting map to JSON", e);
        }
    }

    @Override
    public Map<Integer, Double> convertToEntityAttribute(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<Map<Integer, Double>>() {
            });
        } catch (IOException e) {
            throw new RuntimeException("Error converting JSON to map", e);
        }
    }
}
