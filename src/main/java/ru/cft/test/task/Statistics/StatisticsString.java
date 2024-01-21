package ru.cft.test.task.Statistics;

public class StatisticsString implements Statistics {

    private final StatisticsType type;
    private long size = 0;
    private long min = Long.MAX_VALUE;
    private long max = 0;

    public StatisticsString(StatisticsType type) {
        this.type = type;
    }

    @Override
    public void addValue(String value) {

        long newValue = StringToLong(value);

        size++;

        if(type == StatisticsType.FULL) {
            processMin(newValue);
            processMax(newValue);
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
        }
        System.out.println();
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    private long StringToLong(String value) {
        long newValue = 0;
        try {
            newValue = value.length();
        } catch (Exception e) {
            System.err.println("Error value: " + value);
            System.err.println(e.getMessage());
        }
        return newValue;
    }

    private void processMin(long newValue) {
        min = Math.min(min, newValue);
    }

    private void processMax(long newValue) {
        max = Math.max(max, newValue);
    }

}
