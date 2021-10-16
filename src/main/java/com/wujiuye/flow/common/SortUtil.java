package com.wujiuye.flow.common;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

/**
 * @author wujiuye
 */
public class SortUtil {

    public static <T> void sort(List<T> list, Comparator<T> comparator) {
        Object[] a = list.toArray();
        Arrays.sort(a, (Comparator) comparator);
        ListIterator<T> i = list.listIterator();
        for (Object e : a) {
            i.next();
            i.set((T) e);
        }
    }

}
