package com.wujiuye.flow;

import com.wujiuye.flow.common.MathUtil;
import org.junit.Assert;
import org.junit.Test;

public class MathUtilTest {

    @Test
    public void testDivide() {
        Assert.assertEquals(MathUtil.divide(1, 1), 1);
        Assert.assertEquals(MathUtil.divide(1, 2), 0);
        Assert.assertEquals(MathUtil.divide(1, 0), 0);
        Assert.assertEquals(MathUtil.divide(0, 1), 0);
    }

}
