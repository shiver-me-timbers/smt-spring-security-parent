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

package shiver.me.timbers.spring.security.jwt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Karl Bennett
 */
public class JwtPrincipalMapConverter implements MapConverter<JwtPrincipal> {

    @Override
    public JwtPrincipal convert(Map map) {
        return new JwtPrincipal(map.get("username").toString(), toList(map.get("roles")));
    }

    private static List<String> toList(Object list) {
        final List<String> oldList = (List<String>) list;
        final List<String> newList = new ArrayList<>(oldList.size());
        for (String element : oldList) {
            newList.add(element);
        }
        return newList;
    }
}
