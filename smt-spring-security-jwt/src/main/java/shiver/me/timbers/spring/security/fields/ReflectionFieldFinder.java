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

package shiver.me.timbers.spring.security.fields;

import java.lang.reflect.Field;

import static java.lang.String.format;

/**
 * @author Karl Bennett
 */
public class ReflectionFieldFinder implements FieldFinder {

    @Override
    public Field findField(Object object, String name, Class type) throws NoSuchFieldException {
        return find(object.getClass(), name, type);
    }

    @SuppressWarnings("unchecked")
    private Field find(Class objectType, String name, Class fieldType) throws NoSuchFieldException {
        if (Object.class.equals(objectType)) {
            throw new NoSuchFieldException(
                format("Could not find a field with name (%s) and type (%s).", name, fieldType.getName())
            );
        }

        for (Field field : objectType.getDeclaredFields()) {
            if (isTheRightField(field, name, fieldType)) {
                return field;
            }
        }

        return find(objectType.getSuperclass(), name, fieldType);
    }

    @SuppressWarnings("unchecked")
    private static boolean isTheRightField(Field field, String name, Class type) {
        return type.isAssignableFrom(field.getType()) && field.getName().equals(name);
    }
}
