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

package shiver.me.timbers.spring.security.integration;

import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Import({JwtAnnotationSecurityConfiguration.class, JwtApplySecurityConfiguration.class})
@PropertySource("classpath:application.properties")
public class ITJwtConfigurationMutualExclusivity {

    @Test(expected = BeanCreationException.class)
    public void Cannot_apply_the_adaptor_if_the_annotation_is_present() {
        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(getClass());
        context.refresh();
    }
}
