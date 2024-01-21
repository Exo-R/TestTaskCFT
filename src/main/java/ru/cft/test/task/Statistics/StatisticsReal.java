package ru.cft.test.task.Statistics;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class StatisticsReal implements Statistics {

    private final StatisticsType type;
    private long size = 0;
    private BigDecimal min = null;
    private BigDecimal max = null;
    private BigDecimal sum = BigDecimal.ZERO;

    public StatisticsReal(StatisticsType type) {
        this.type = type;
    }

    @Override
    public void addValue(String value) {

        BigDecimal newValue = StringToBigDecimal(value);

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
            System.out.println("Minimum value:     " + min.stripTrailingZeros());
            System.out.println("Maximum value:     " + max.stripTrailingZeros());
            System.out.println("Sum of values:     " + sum.stripTrailingZeros());
            System.out.println("Average value:     " + processAverage().stripTrailingZeros());
        }
        System.out.println();
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    private BigDecimal StringToBigDecimal(String value) {
        BigDecimal newValue = null;
        try {
            newValue = new BigDecimal(value);
        } catch (Exception e) {
            System.err.println("Error value: " + value);
            System.err.println(e.getMessage());
        }
        return newValue;
    }

    private void processMin(BigDecimal newValue) {
        try {
            if (min == null || newValue.compareTo(min) < 0) {
                min = newValue;
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private void processMax(BigDecimal newValue) {
        try {
            if (max == null || newValue.compareTo(max) > 0) {
                max = newValue;
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private void processSum(BigDecimal newValue) {
        try {
            sum = sum.add(newValue);
        }catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private BigDecimal processAverage() {
        BigDecimal avgValue = null;
        try {
            avgValue = sum.divide(BigDecimal.valueOf(size), RoundingMode.HALF_UP);
        }catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return avgValue;
    }


}
