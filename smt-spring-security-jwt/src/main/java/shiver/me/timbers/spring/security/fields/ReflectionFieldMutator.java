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
import java.lang.reflect.Modifier;

/**
 * @author Karl Bennett
 */
public class ReflectionFieldMutator implements FieldMutator {

    private final FieldFinder fieldFinder;
    private final FieldGetSetter fieldGetSetter;

    public ReflectionFieldMutator(FieldFinder fieldFinder, FieldGetSetter fieldGetSetter) {
        this.fieldFinder = fieldFinder;
        this.fieldGetSetter = fieldGetSetter;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T retrieve(Object object, String name, Class<T> type) {
        try {
            return (T) fieldGetSetter.get(object, fieldFinder.findField(object, name, type));
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void replace(Object object, String name, Class type, Object value) {
        try {
            fieldGetSetter.set(object, fieldFinder.findField(object, name, type), value);
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public <T> void update(Object object, String name, Class<T> type, Updater<T> updater) {
        final T value = retrieve(object, name, type);
        replace(object, name, type, updater.update(value));
    }

    @Override
    public <F, T extends F> void copy(F from, T to) {
        try {
            copyFields(from.getClass(), from, to);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void copyFields(Class type, Object from, Object to) throws IllegalAccessException {
        if (Object.class.equals(type)) {
            return;
        }

        for (Field field : type.getDeclaredFields()) {
            copyField(field, from, to);
        }

        copyFields(type.getSuperclass(), from, to);
    }

    private void copyField(Field field, Object from, Object to) throws IllegalAccessException {
        if (Modifier.isStatic(field.getModifiers())) {
            return;
        }

        fieldGetSetter.set(to, field, fieldGetSetter.get(from, field));
    }

}
