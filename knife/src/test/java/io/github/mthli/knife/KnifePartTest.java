package io.github.mthli.knife;

import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@SmallTest
public class KnifePartTest extends BaseKnifeTest {

    @Test public void constructor() {
        KnifePart knifePart = new KnifePart(1, 2);
        assertEquals(1, knifePart.getStart());
        assertEquals(2, knifePart.getEnd());
    }

    @Test public void isValid() {
        KnifePart knifePart = new KnifePart(1, 2);
        assertTrue(knifePart.isValid());
    }

    @Test public void isNotValid() {
        KnifePart knifePart = new KnifePart(2, 1);
        assertFalse(knifePart.isValid());
    }
}
