package io.gamioo.sandbox.util;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数学计算相关的工具类库.
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class MathUtils {
    /**
     * 一
     */
    public static final double ONE = 1.0;
    /**
     * 百
     */
    public static final double HUNDRED = 100.0;
    /**
     * 千
     */
    public static final double THOUSAND = 1000.0;
    /**
     * 万
     */
    public static final double TEN_THOUSAND = 1_0000.0;
    /**
     * 百万
     */
    public static final double MILLION = 100_0000.0;

    /**
     * 计算两个参数的和，如果相加出现溢出那就返回{@code int}的最大值.
     * <p>
     * 区别于JDK的方法，仅仅认同判定方案，游戏世界，溢出时那就修正一个合理的值，一般调用此方法的游戏逻辑决不能因异常而中断
     *
     * @param x 第一个参数
     * @param y 第二个参数
     * @return 两个参数的和
     * @see Math#addExact(int, int)
     */
    public static int addExact(int x, int y) {
        try {
            return Math.addExact(x, y);
        } catch (ArithmeticException e) {
            return Integer.MAX_VALUE;
        }
    }

    /**
     * 计算两个参数的和，如果相加出现溢出那就返回{@code long}的最大值.
     * <p>
     * 区别于JDK的方法，仅仅认同判定方案，游戏世界，溢出时那就修正一个合理的值，一般调用此方法的游戏逻辑决不能因异常而中断
     *
     * @param x 第一个参数
     * @param y 第二个参数
     * @return 两个参数的和
     * @see Math#addExact(long, long)
     */
    public static long addExact(long x, long y) {
        try {
            return Math.addExact(x, y);
        } catch (ArithmeticException e) {
            return Long.MAX_VALUE;
        }
    }

    /**
     * 计算两个参数的乘积，如果相乘出现溢出那就返回{@code int}的最大值.
     * <p>
     * 区别于JDK的方法，仅仅认同判定方案，游戏世界，溢出时那就修正一个合理的值，一般调用此方法的游戏逻辑决不能因异常而中断
     *
     * @param x 第一个参数
     * @param y 第二个参数
     * @return 两个参数的乘积
     * @see Math#multiplyExact(int, int)
     */
    public static int multiplyExact(int x, int y) {
        try {
            return Math.multiplyExact(x, y);
        } catch (ArithmeticException e) {
            return Integer.MAX_VALUE;
        }
    }

    /**
     * 计算两个参数的乘积，如果相乘出现溢出那就返回{@code long}的最大值.
     * <p>
     * 区别于JDK的方法，仅仅认同判定方案，游戏世界，溢出时那就修正一个合理的值，一般调用此方法的游戏逻辑决不能因异常而中断
     *
     * @param x 第一个参数
     * @param y 第二个参数
     * @return 两个参数的乘积
     * @see Math#multiplyExact(long, long)
     */
    public static long multiplyExact(long x, long y) {
        try {
            return Math.multiplyExact(x, y);
        } catch (ArithmeticException e) {
            return Long.MAX_VALUE;
        }
    }

    /**
     * 计算两点(x1,y1)到(x2,y2)的距离.
     * <p>
     * Math.sqrt(|x1-x2|² + |y1-y2|²)
     *
     * @param x1 坐标X1
     * @param y1 坐标Y1
     * @param x2 坐标X2
     * @param y2 坐标Y2
     * @return 两点的距离
     */
    public static double distance(int x1, int y1, int x2, int y2) {
        final double x = Math.abs(x1 - x2);
        final double y = Math.abs(y1 - y2);
        return Math.sqrt(x * x + y * y);
    }

    /**
     * 计算两点(x1,y1)到(x2,y2)的距离.
     * <p>
     * Math.sqrt(|x1-x2|² + |y1-y2|²)
     *
     * @param x1 坐标X1
     * @param y1 坐标Y1
     * @param x2 坐标X2
     * @param y2 坐标Y2
     * @return 两点的距离
     */
    public static double distance(double x1, double y1, double x2, double y2) {
        final double x = Math.abs(x1 - x2);
        final double y = Math.abs(y1 - y2);
        return Math.sqrt(x * x + y * y);
    }


    /**
     * 判定两点(x1,y1)和(x2,y2)是否相邻.
     * <p>
     * 可用于两个AOI是否相邻判定
     *
     * @param x1 坐标X1
     * @param y1 坐标Y1
     * @param x2 坐标X2
     * @param y2 坐标Y2
     * @return 如果两坐标相邻返回true, 否则返回false
     */
    public static boolean adjacent(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) <= 1 && Math.abs(y1 - y2) <= 1;
    }

    /**
     * 向下取整，并返回int值.
     *
     * @param a 一个带有小数的数值
     * @return 返回向下取整后的int值
     */
    public static int floorInt(double a) {
        return (int) Math.floor(a);
    }

    /**
     * 向下取整，并返回long值.
     *
     * @param a 一个带有小数的数值
     * @return 返回向下取整后的long值
     */
    public static long floorLong(double a) {
        return (long) Math.floor(a);
    }

    /**
     * 向上取整，并返回int值.
     *
     * @param a 一个带有小数的数值
     * @return 返回向上取整后的int值
     */
    public static int ceilInt(double a) {
        return (int) Math.ceil(a);
    }

    /**
     * 向上取整，并返回long值.
     *
     * @param a 一个带有小数的数值
     * @return 返回向上取整后的long值
     */
    public static long ceilLong(double a) {
        return (long) Math.ceil(a);
    }

    /**
     * 4舍5入取整，并返回int值.
     *
     * @param a 一个带有小数的数值
     * @return 返回向上取整后的int值
     */
    public static int roundInt(double a) {
        return (int) Math.round(a);
    }


    public static int min(List<Integer> array) {
        int ret = Integer.MAX_VALUE;
        for (int e : array) {
            if (e < ret) {
                ret = e;
            }
        }
        return ret;
    }


    public static int min(int... array) {
        int ret = Integer.MAX_VALUE;
        for (int e : array) {
            if (e < ret) {
                ret = e;
            }
        }
        return ret;
    }

    public static int max(int... array) {
        int ret = Integer.MIN_VALUE;
        for (int e : array) {
            if (e > ret) {
                ret = e;
            }
        }
        return ret;
    }

    public static int max(List<Integer> array) {
        int ret = Integer.MIN_VALUE;
        for (int e : array) {
            if (e > ret) {
                ret = e;
            }
        }
        return ret;
    }

    /**
     * 4舍5入取整，并返回long值.
     *
     * @param a 一个带有小数的数值
     * @return 返回向上取整后的long值
     */
    public static long roundLong(double a) {
        return (long) Math.round(a);
    }

    /**
     * 格式化小数位数的方法.
     * <p>
     * 采用了{@link BigDecimal#setScale(int, RoundingMode)}方式来保留小数位数<br>
     * 默认舍入方式为4舍5入, 参考{@link RoundingMode#HALF_UP}
     *
     * @param value    原始值
     * @param newScale 保留小数位数
     * @return 返回要被保留指定小数位数的值.
     */
    public static float formatScale(float value, int newScale) {
        return formatScale(value, newScale, RoundingMode.HALF_UP);
    }

    /**
     * 格式化小数位数的方法.
     * <p>
     * 采用了{@link BigDecimal#setScale(int, RoundingMode)}方式来保留小数位数
     *
     * @param value    原始值
     * @param newScale 保留小数位数
     * @param mode     被保留位数后舍入方式，参考{@link RoundingMode}
     * @return 返回要被保留指定小数位数的值.
     */
    public static float formatScale(float value, int newScale, RoundingMode mode) {
        return BigDecimal.valueOf(value).setScale(newScale, mode).floatValue();
    }

    /**
     * 格式化小数位数的方法.
     * <p>
     * 采用了{@link BigDecimal#setScale(int, RoundingMode)}方式来保留小数位数<br>
     * 默认舍入方式为4舍5入, 参考{@link RoundingMode#HALF_UP}
     *
     * @param value    原始值
     * @param newScale 保留小数位数
     * @return 返回要被保留指定小数位数的值.
     */
    public static double formatScale(double value, int newScale) {
        return formatScale(value, newScale, RoundingMode.HALF_UP);
    }

    /**
     * 格式化小数位数的方法.
     * <p>
     * 采用了{@link BigDecimal#setScale(int, RoundingMode)}方式来保留小数位数
     *
     * @param value    原始值
     * @param newScale 保留小数位数
     * @param mode     被保留位数后舍入方式，参考{@link RoundingMode}
     * @return 返回要被保留指定小数位数的值.
     */
    public static double formatScale(double value, int newScale, RoundingMode mode) {
        return BigDecimal.valueOf(value).setScale(newScale, mode).doubleValue();
    }

    /**格式化乘百分比
     * @param value 待格式化的数值
     * @return 格式化后的字符串*/

    public static String prettyPercentage(double value){
        NumberFormat nf = NumberFormat.getPercentInstance();
        nf.setMinimumFractionDigits(2);
        return nf.format(value);
    }

}