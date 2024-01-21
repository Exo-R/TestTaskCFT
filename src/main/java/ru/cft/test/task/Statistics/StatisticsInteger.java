package ru.cft.test.task.Statistics;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class StatisticsInteger implements Statistics {

    public static final int CONST_SCALE = 16;

    private final StatisticsType type;
    private long size = 0;
    private BigInteger min = null;
    private BigInteger max = null;
    private BigInteger sum = BigInteger.ZERO;

    public StatisticsInteger(StatisticsType type) {
        this.type = type;
    }

    @Override
    public void addValue(String value) {

        BigInteger newValue = StringToBigInteger(value);

        size++;

        if(type == StatisticsType.FULL) {
            processMin(newValue);
            processMax(newValue);
            processSum(newValue);
        }
    }

    @Override
    public void printStatistics(String nameOutfile) {
        System.out.println();
        if (type == StatisticsType.SHORT) {
            System.out.println("Short statistics:  " + nameOutfile);
            System.out.println();
            System.out.println("Number of values:  " + size);
        }
        else {
            System.out.println("Full statistics:   " + nameOutfile);
            System.out.println();
            System.out.println("Number of values:  " + size);
            System.out.println("Minimum value:     " + min);
            System.out.println("Maximum value:     " + max);
            System.out.println("Sum of values:     " + sum);
            System.out.println("Average value:     " + processAverage().stripTrailingZeros());
        }
        System.out.println();
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    private BigInteger StringToBigInteger(String value) {
        BigInteger newValue = null;
        try {
            newValue = new BigInteger(value);
        } catch (Exception e) {
            System.err.println("Error value: " + value);
            System.err.println(e.getMessage());
        }
        return newValue;
    }

    private void processMin(BigInteger newValue) {
        try {
            if (min == null || newValue.compareTo(min) < 0) {
                min = newValue;
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private void processMax(BigInteger newValue) {
        try {
            if (max == null || newValue.compareTo(max) > 0) {
                max = newValue;
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private void processSum(BigInteger newValue) {
        try {
            sum = sum.add(newValue);
        }catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private BigDecimal processAverage() {
        BigDecimal avgValue = null;
        try {
            avgValue = new BigDecimal(sum).divide(BigDecimal.valueOf(size), CONST_SCALE, RoundingMode.HALF_UP);
        }catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return avgValue;
    }

}
