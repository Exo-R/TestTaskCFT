package ru.cft.test.task;

import lombok.Data;
import ru.cft.test.task.Statistics.StatisticsType;

import java.util.List;

@Data
public class FilterArgs {

    private String outFilePath;

    private String prefixFileName;

    private boolean appendModeWriter;

    private StatisticsType statisticsType;

    private List<String> fileNames;
}
