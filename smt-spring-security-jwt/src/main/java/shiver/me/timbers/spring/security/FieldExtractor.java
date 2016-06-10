/*
 * Copyright 2016 Karl Bennett
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package shiver.me.timbers.spring.security;

import java.lang.reflect.Field;

/**
 * @author Karl Bennett
 */
public class FieldExtractor {

    private final FieldGetter fieldGetter;

    FieldExtractor(FieldGetter fieldGetter) {
        this.fieldGetter = fieldGetter;
    }

    <T> T extract(Class<T> type, String name, Object object) {
        return extract(type, name, object.getClass(), object);
    }

    @SuppressWarnings("unchecked")
    private <T> T extract(Class<T> fieldType, String name, Class objectType, Object object) {
        if (Object.class.equals(objectType)) {
            return null;
        }

        for (Field field : objectType.getDeclaredFields()) {
            if (isTheRightField(fieldType, name, field)) {
                try {
                    return (T) fieldGetter.get(field, object);
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException(e);
                }
            }
        }

        return extract(fieldType, name, objectType.getSuperclass(), object);
    }

    private static <T> boolean isTheRightField(Class<T> type, String name, Field field) {
        return type.isAssignableFrom(field.getType()) && field.getName().equals(name);
    }
}
