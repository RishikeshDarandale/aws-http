package io.github.rishikeshdarandale.aws;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.github.rishikeshdarandale.aws.AwsSignParams;

public class AwsSignParamsTest {
    @Test
    public void testMask() {
        assertEquals("XXXXXXXXKey", new AwsSignParams().mask("MySecretKey", 3));
        assertNull(new AwsSignParams().mask(null, 2));
    }

    @Test
    public void testToString() {
        StringBuilder sb = new StringBuilder("AwsSignParams [awsAccessKey=")
                .append("XXXXetid").append(", awsAccessSecret=").append("XXXXXX")
                .append(", region=").append("us-east-1").append(", serviceName=")
                .append("es").append(", timeInMillis=").append("12345]");
        AwsSignParams param = new AwsSignParams("secretid", "secret", "es");
        param.setTimeInMillis(12345L);
        assertEquals(sb.toString(), param.toString());
    }
    @Test
    public void testEqualsAndHashcode() {
        AwsSignParams param1 = new AwsSignParams("id", "secret", "es");
        AwsSignParams param2 = new AwsSignParams("id", "secret", "es");
        assertTrue(param1.equals(param2));
        assertEquals(param1.hashCode(), param2.hashCode());
        AwsSignParams param3 = new AwsSignParams("id1", "anothersecret", "iam");
        assertFalse(param3.equals(param1));
        assertNotEquals(param3.hashCode(), param1.hashCode());
        assertFalse(param3.equals(null));   
        assertTrue(param1.equals(param1));
        assertEquals(param1.hashCode(), param1.hashCode());
        AwsSignParams param4 = new AwsSignParams("id1", "anothersecret", "us-west-1", "iam");
        assertFalse(param3.equals(param4));
        assertNotEquals(param3.hashCode(), param4.hashCode());
    }
}
