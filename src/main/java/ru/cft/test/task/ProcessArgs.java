package ru.cft.test.task;

import org.apache.commons.cli.*;
import ru.cft.test.task.Exception.InvalidArgsException;
import ru.cft.test.task.Exception.StatisticsTypeException;
import ru.cft.test.task.Statistics.StatisticsType;

import java.util.List;

public class ProcessArgs {

    public static final String OUTPUT_PATH_OPTION = "o";
    public static final String PREFIX_OPTION = "p";
    public static final String APPEND_MODE_OPTION = "a";
    public static final String SHORT_STATISTICS_OPTION = "s";
    public static final String FULL_STATISTICS_OPTION = "f";

    public static final String OUTPUT_PATH_DESCRIPTION = "Output file path";
    public static final String PREFIX_DESCRIPTION = "Filename prefix";
    public static final String APPEND_MODE_DESCRIPTION = "Append mode writer";
    public static final String SHORT_STATISTICS_DESCRIPTION = "Short statistics";
    public static final String FULL_STATISTICS_DESCRIPTION = "Full statistics";

    public FilterArgs process(String[] args) {

        Options options = new Options();

        options.addOption(OUTPUT_PATH_OPTION, true, OUTPUT_PATH_DESCRIPTION);
        options.addOption(PREFIX_OPTION, true, PREFIX_DESCRIPTION);
        options.addOption(APPEND_MODE_OPTION, false, APPEND_MODE_DESCRIPTION);
        options.addOption(SHORT_STATISTICS_OPTION, false, SHORT_STATISTICS_DESCRIPTION);
        options.addOption(FULL_STATISTICS_OPTION, false, FULL_STATISTICS_DESCRIPTION);

        CommandLineParser parser = new DefaultParser();

        FilterArgs filterArgs = new FilterArgs();

        try {
            CommandLine line = parser.parse(options, args);

            String outPath = line.getOptionValue(OUTPUT_PATH_OPTION, "");             //как обработать если юзер не укажет второй арг
            String prefixNameFile = line.getOptionValue(PREFIX_OPTION, "");       //как обработать если юзер не укажет второй арг
            boolean isAppending = line.hasOption(APPEND_MODE_OPTION);
            boolean isShortStatistics = line.hasOption(SHORT_STATISTICS_OPTION);
            boolean isFullStatistics = line.hasOption(FULL_STATISTICS_OPTION);

            StatisticsType statisticsType;
            if (isShortStatistics && isFullStatistics) {
                throw new StatisticsTypeException("Error selecting statistics type!");
            }
             else if (isShortStatistics) {
                statisticsType = StatisticsType.SHORT;
            }
             else if (isFullStatistics) {
                statisticsType = StatisticsType.FULL;
            }
             else {
                throw new StatisticsTypeException("Error. Statistics type not specified!");
            }

            List<String> fileNames = line.getArgList();

            filterArgs.setOutFilePath(outPath);
            filterArgs.setPrefixFileName(prefixNameFile);
            filterArgs.setAppendModeWriter(isAppending);
            filterArgs.setStatisticsType(statisticsType);
            filterArgs.setFileNames(fileNames);

            return filterArgs;
        } catch (ParseException e) {
            throw new InvalidArgsException(String.format("Error processing arguments: %s", e.getMessage()));
        }
    }

}

