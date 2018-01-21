package in.rishikeshdarandale.aws.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/* Test classes are extended from utility classes to increase the test coverage[1].
   Basically, I opted for not to influence the actual code to increase the test
   coverage.

   See: https://stackoverflow.com/questions/9700179/junit-testing-helper-class-with-only-static-methods

   [1] https://stackoverflow.com/a/9700354/8101556
*/
public class DateUtilsTest extends DateUtils {
    @Test
    public void testGetDate() {
        assertEquals("20180121", DateUtils.getDate(1516509349306L, "yyyyMMdd"));
    }
}
