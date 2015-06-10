/* 
 * Copyright 2015 Patrik Duditš <structgraph@dudits.net>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.structgraph;

import java.util.function.Consumer;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.mockito.Mockito;

/**
 * Why the hell this is not part of hamcrest lib?
 * @author Patrik Duditš
 */
public class NestedMatcher<T> extends TypeSafeDiagnosingMatcher<T> {
    private final Consumer<T> assertion;
    public NestedMatcher(Consumer<T> assertion) {
        this.assertion = assertion;        
    }

    @Override
    protected boolean matchesSafely(T t, Description d) {
        try {
            assertion.accept(t);        
        } catch (AssertionError ae) {
            d.appendText(ae.toString());
            return false;
        }
        return true;
    }
    
    

    @Override
    public void describeTo(Description d) {
        d.appendText("specified asserts");
    }
    
    public static <U> NestedMatcher<U> passes(Consumer<U> assertion) {
        return new NestedMatcher<>(assertion);
    }

    public static <U> U passing(Consumer<U> assertion) {
        return Mockito.argThat(new NestedMatcher<>(assertion));
    }
    
}
