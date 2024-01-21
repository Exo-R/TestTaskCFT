package ru.cft.test.task;

public class MainClass {

    public static void main(String[] args) {
        ProcessArgs processArgs = new ProcessArgs();
        FilterArgs filterArgs = processArgs.process(args);
        ProcessLines processLines = new ProcessLinesImpl();
        processLines.process(filterArgs);
    }
}

