package io.gamioo.sandbox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Rick.Mei
 */
public class IntPairType {
    private static final String UNDERLINE = "_";
    private static final String COMMA = ",";
    private static final int SIZE = 2;
    private int key;
    private  int value;

    public IntPairType() {

    }

    public IntPairType(int key, int value) {
        this.key = key;
        this.value = value;
    }

    public void init(int key, int value){
        this.key = key;
        this.value = value;
    }


    public int getKey() {
        return key;
    }

    @Override
    public String toString() {
        return this.key + UNDERLINE + this.value;
    }

    public int getValue() {
        return value;
    }

    public static IntPairType makePair(int key, int value) {
        return new IntPairType(key, value);
    }

    public static IntPairType valueOf(String s) {
        int[] array = Arrays.stream(s.split(UNDERLINE)).mapToInt(num -> Integer.parseInt(num)).toArray();
        if (array.length == SIZE) {
            return IntPairType.makePair(array[0], array[1]);
        }
        return IntPairType.makePair(0, 0);
    }

    public static IntPairType parseFromString(String s) {
        int[] array = Arrays.stream(s.split(UNDERLINE)).mapToInt(num -> Integer.parseInt(num)).toArray();
        if (array.length == SIZE) {
            return IntPairType.makePair(array[0], array[1]);
        }
        return IntPairType.makePair(0, 0);
    }

    public static List<IntPairType> parseListFromString(String s) {
        List<IntPairType> ret = new ArrayList<>();
        Arrays.stream(s.split(COMMA)).map(e -> ret.add(IntPairType.parseFromString(e))).collect(Collectors.toList());
        return ret;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        IntPairType pair = (IntPairType) o;
        return getKey() == pair.getKey() && getValue() == pair.getValue();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), getValue());
    }
}
