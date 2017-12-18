package ut.ua.oleksiiholyk;

import org.junit.Test;
import ua.oleksiiholyk.api.MyPluginComponent;
import ua.oleksiiholyk.impl.MyPluginComponentImpl;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest
{
    @Test
    public void testMyName()
    {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        assertEquals("names do not match!", "myComponent",component.getName());
    }
}