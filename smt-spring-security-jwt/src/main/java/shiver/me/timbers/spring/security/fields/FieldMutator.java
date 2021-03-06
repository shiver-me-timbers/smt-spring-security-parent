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

/**
 * @author Karl Bennett
 */
public interface FieldMutator {

    <T> T retrieve(Object object, String name, Class<T> type);

    void replace(Object object, String name, Class type, Object value);

    <T> void update(Object object, String name, Class<T> type, Updater<T> updater);

    <F, T extends F> void copy(F from, T to);
}
