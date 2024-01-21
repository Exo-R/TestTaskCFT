package ru.cft.test.task;

import ru.cft.test.task.Exception.InvalidPathException;
import ru.cft.test.task.Exception.InvalidPrefixException;
import ru.cft.test.task.Statistics.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProcessLinesImpl implements ProcessLines{

    public static final String INTEGER_NAME_FILE = "integers.txt";
    public static final String REAL_NAME_FILE = "floats.txt";
    public static final String STRING_NAME_FILE = "strings.txt";

    @Override
    public void process(FilterArgs filterArgs) {

        String prefixNameOutfile = filterArgs.getPrefixFileName();
        String outfilePath = filterArgs.getOutFilePath();
        List<String> listInfileNames = filterArgs.getFileNames();
        boolean isAppending = filterArgs.isAppendModeWriter();
        StatisticsType statisticsType = filterArgs.getStatisticsType();

        validatePrefix(prefixNameOutfile);

        String integerNameOutfile = prefixNameOutfile + INTEGER_NAME_FILE;
        String realNameOutfile = prefixNameOutfile + REAL_NAME_FILE;
        String stringNameOutfile = prefixNameOutfile + STRING_NAME_FILE;

        outfilePath = pathChecking(outfilePath);

        Statistics statisticsInteger = new StatisticsInteger(statisticsType);
        Statistics statisticsReal = new StatisticsReal(statisticsType);
        Statistics statisticsString = new StatisticsString(statisticsType);

        try (
                BufferedWriter writerInteger =
                        new BufferedWriter(new FileWriter(outfilePath + integerNameOutfile, isAppending));
                BufferedWriter writerReal =
                        new BufferedWriter(new FileWriter(outfilePath + realNameOutfile, isAppending));
                BufferedWriter writerString =
                        new BufferedWriter(new FileWriter(outfilePath + stringNameOutfile, isAppending))
            ) {

            processFile(
                     writerInteger,
                     writerReal,
                     writerString,
                     statisticsInteger,
                     statisticsReal,
                     statisticsString,
                     listInfileNames
            );

        }catch (IOException e){

            System.err.println("Error creating output files:");
            System.err.println(e.getMessage());
        }

        if(!statisticsInteger.isEmpty()) {
            statisticsInteger.printStatistics(integerNameOutfile);
        }
        if(!statisticsReal.isEmpty()) {
            statisticsReal.printStatistics(realNameOutfile);
        }
        if(!statisticsString.isEmpty()) {
            statisticsString.printStatistics(stringNameOutfile);
        }

        removeFile(outfilePath + integerNameOutfile);
        removeFile(outfilePath + realNameOutfile);
        removeFile(outfilePath + stringNameOutfile);
    }

    private void processFile(
            BufferedWriter integerWriter,
            BufferedWriter realWriter,
            BufferedWriter stringWriter,
            Statistics integerStatistics,
            Statistics realStatistics,
            Statistics stringStatistics,
            List<String> listInfileNames
    ) throws IOException {

        List<BufferedReader> readers = new ArrayList<>();
        try {

            Queue<BufferedReader> queue = new LinkedList<>();
            for (String listInfileName : listInfileNames) {
                BufferedReader reader = new BufferedReader(new FileReader(listInfileName));
                queue.add(reader);
                readers.add(reader);
            }

            while (!queue.isEmpty()) {

                BufferedReader currentReader = queue.poll();

                String currentLine = currentReader.readLine();
                if (currentLine != null) {
                    queue.add(currentReader);

                    if (!currentLine.trim().isEmpty()) {
                        if (isInteger(currentLine)) {
                            integerWriter.write(currentLine);
                            integerWriter.newLine();
                            integerStatistics.addValue(currentLine);
                        } else if (isReal(currentLine)) {
                            realWriter.write(currentLine);
                            realWriter.newLine();
                            realStatistics.addValue(currentLine);
                        } else {
                            stringWriter.write(currentLine);
                            stringWriter.newLine();
                            stringStatistics.addValue(currentLine);
                        }
                    }
                }
            }
        } catch (IOException exception) {

            System.err.println("Error reading file:");
            System.err.println(exception.getMessage());

        } finally {
            for (BufferedReader reader : readers) {
                reader.close();
            }
        }
    }

    private String pathChecking(String outfilePath) {

        if(!outfilePath.isEmpty()) {

            validateOutfilePath(outfilePath);

            if (!Paths.get(outfilePath).isAbsolute()) {
                outfilePath = System.getProperty("user.dir") + "\\" + outfilePath + "\\";
            }
            else {
                outfilePath = outfilePath + "\\";
            }

            File directories = new File(outfilePath);
            boolean successDirsCreation = directories.mkdirs();
            if (!directories.exists() && !successDirsCreation) {
                throw new InvalidPathException("Error creating directories in outfile path!");
            }
        }
        return outfilePath;
    }

    private void removeFile(String filePath) {

        try {
            Path path = Paths.get(filePath);
            if (Files.size(path) == 0) {
                Files.delete(path);
            }
            } catch (IOException e) {
                System.err.println("Error deleting " + filePath + " :");
                e.printStackTrace();
            }
    }

    private boolean regexMatch(String regex, String line) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(line);
        return matcher.matches();
    }

    private boolean isReal(String line) {
        String regex = "^[-+]?\\d+[.]\\d+([Ee][-+]?\\d+)?$";
        return regexMatch(regex, line);
    }

    private boolean isInteger(String line) {
        String regex = "^([-+])?\\d+$";
        return regexMatch(regex, line);
    }

    private void validatePrefix(String prefix) {
        String regex = "[^/\\\\*:?<>|\"]*";
        if (!regexMatch(regex, prefix)) {
            throw new InvalidPrefixException("Error: the prefix contains invalid characters!");
        }
    }

    private void validateOutfilePath(String outfilePath) {
        String regex = "^([a-zA-Z]:\\\\)?[^*:?<>|\"]*";
        if (!regexMatch(regex, outfilePath)) {
            throw new InvalidPathException("Error: the outfile path contains invalid characters!");
        }
    }

}
