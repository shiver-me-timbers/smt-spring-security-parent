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

import com.stormpath.sdk.application.ApplicationCriteria;
import com.stormpath.sdk.application.Applications;
import com.stormpath.sdk.query.Criterion;
import com.stormpath.sdk.query.StringExpressionFactory;

/**
 * @author Karl Bennett
 */
public class StormpathApplicationCriteriaFactory implements ApplicationCriteriaFactory {
    @Override
    public StringExpressionFactory name() {
        return Applications.name();
    }

    @Override
    public ApplicationCriteria where(Criterion criterion) {
        return Applications.where(criterion);
    }
}
