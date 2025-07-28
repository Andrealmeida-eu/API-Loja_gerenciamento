package com.studioAlanGodoy.Loja.infrastructure;

import java.lang.reflect.Field;
import java.util.Map;

public class EntityUpdate {

    public static <T> void updatePartialEntity(T entity, Map<String, Object> updates)
            throws IllegalAccessException {
        Class<?> clazz = entity.getClass();

        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            String fieldName = entry.getKey();
            Object value = entry.getValue();

            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);

                if (value != null && field.getType().isAssignableFrom(value.getClass())) {
                    field.set(entity, value);
                }
            } catch (NoSuchFieldException e) {
                // Campo não existe - pode ser ignorado ou lançar exceção
            }
        }
    }
}