package com.wujiuye.flow;

/**
 * Created by ZengShiLin on 2023/6/25 10:15 AM
 *
 * @author ZengShiLin
 */
public enum PropType {

    /**
     * 99线
     */
    PROP_99(0.99),
    /**
     * 95线
     */
    PROP_95(0.95),
    /**
     * 90线
     */
    PROP_90(0.90),

    /**
     * 85线
     */
    PROP_85(0.85),

    /**
     * 80线
     */
    PROP_80(0.80);

    public final double proportion;

    PropType(double proportion) {
        this.proportion = proportion;
    }

}
