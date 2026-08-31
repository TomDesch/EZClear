package io.stealingdapenta.ezclear;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class EZClearTest {

    @Test
    void pluginDoesNotExposeMutableStaticState() {
        List<String> mutableStaticFields = Arrays.stream(EZClear.class.getDeclaredFields())
                .filter(EZClearTest::isMutableStatic)
                .map(Field::getName)
                .toList();

        assertEquals(List.of(), mutableStaticFields);
    }

    private static boolean isMutableStatic(Field field) {
        int modifiers = field.getModifiers();
        return Modifier.isStatic(modifiers) && !Modifier.isFinal(modifiers);
    }
}
