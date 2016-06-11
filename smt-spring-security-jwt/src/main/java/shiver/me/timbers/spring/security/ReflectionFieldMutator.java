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

/**
 * @author Karl Bennett
 */
public class ReflectionFieldMutator implements FieldMutator {

    private final FieldFinder fieldFinder;
    private final FieldGetter fieldGetter;
    private final FieldSetter fieldSetter;

    public ReflectionFieldMutator(FieldFinder fieldFinder, FieldGetter fieldGetter, FieldSetter fieldSetter) {
        this.fieldFinder = fieldFinder;
        this.fieldGetter = fieldGetter;
        this.fieldSetter = fieldSetter;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T retrieve(Object object, String name, Class<T> type) {
        try {
            return (T) fieldGetter.get(object, fieldFinder.findField(object, name, type));
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void update(Object object, String name, Object value) {
        try {
            fieldSetter.set(object, fieldFinder.findField(object, name, value.getClass()), value);
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }
}
