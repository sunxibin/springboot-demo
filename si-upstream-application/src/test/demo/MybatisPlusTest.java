package demo;

import com.si.upstream.dal.das.floor3.WayPointDas;
import com.si.upstream.dal.entity.floor3.WayPointDO;
import org.junit.Test;
import com.si.upstream.application.Application;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author sunxibin
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@PrepareForTest(WayPointDas.class)
@PowerMockIgnore( {"javax.management.*", "javax.net.ssl.*"})
public class MybatisPlusTest {

    @Resource
    private WayPointDas wayPointDas;

    @Test
    public void function_001() {
        WayPointDO pointCarrier = new WayPointDO();
        pointCarrier.setPointCode("600023_iM2N_GrSh");
        pointCarrier.setOccupiedState(1);
        int impactRows = wayPointDas.update(pointCarrier);
        System.out.println(impactRows);
    }
}
