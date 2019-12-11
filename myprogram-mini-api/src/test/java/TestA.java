import com.xuren.Application;
import com.xuren.utils.RedisOperator;
import junit.framework.TestCase;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class TestA {
    private Logger log = LogManager.getLogger(this.getClass());
    @Autowired
    RedisOperator redisOperator;

    @Test
    public void test() {
        log.info("Dsdsdsdsdsd");
        redisOperator.set("name", "xuren");
        String name = redisOperator.get("name");
        TestCase.assertEquals("xuren",name);
    }

    @Before
    public void testBefore() {
        System.out.println("before");
    }

    @After
    public void testAfter() {
        System.out.println("after");
    }
}
