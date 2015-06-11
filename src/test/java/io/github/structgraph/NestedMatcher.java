/* 
 * Copyright (C) 2015 Patrik Duditš <structgraph@dudits.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
