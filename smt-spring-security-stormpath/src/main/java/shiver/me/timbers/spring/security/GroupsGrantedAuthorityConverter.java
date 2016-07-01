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

import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.group.GroupList;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;

import static com.stormpath.sdk.group.GroupStatus.ENABLED;

/**
 * @author Karl Bennett
 */
public class GroupsGrantedAuthorityConverter implements GrantedAuthorityConverter {

    @Override
    public Collection<? extends GrantedAuthority> convert(GroupList groups) {
        final Collection<GrantedAuthority> authorities = new ArrayList<>(groups.getSize());
        for (Group group : groups) {
            if (ENABLED.equals(group.getStatus())) {
                authorities.add(new SimpleGrantedAuthority(group.getName()));
            }
        }
        return authorities;
    }
}
