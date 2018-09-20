package com.itangcent.event.core;

import com.itangcent.event.reflect.TypeToken;
import org.junit.jupiter.api.Test;

public class TypeTokenTest {

    @Test
    public void ATest() {
        System.out.println("hasdasd");
        TypeToken<AImpl> typeToken = TypeToken.of(AImpl.class);
        for (TypeToken<? super AImpl> allType : TypeToken.of(AImpl.class)
                .getTypes().allTypes()) {
            System.out.println(allType);
        }
    }


    private interface A {
        void getA();
    }

    private class AImpl implements A {

        @Override
        public void getA() {

        }
    }
}
