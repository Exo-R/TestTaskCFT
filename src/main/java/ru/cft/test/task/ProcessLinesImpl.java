package ru.cft.test.task;

import ru.cft.test.task.Exception.InvalidPathException;
import ru.cft.test.task.Exception.InvalidPrefixException;
import ru.cft.test.task.Statistics.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProcessLinesImpl implements ProcessLines{

    public static final String INTEGER_NAME_FILE = "integers.txt";
    public static final String REAL_NAME_FILE = "floats.txt";
    public static final String STRING_NAME_FILE = "strings.txt";

    @Override
    public void process(FilterArgs filterArgs) {
//*
        String prefixNameOutfile = filterArgs.getPrefixFileName();
        String outfilePath = filterArgs.getOutFilePath();
        List<String> listInfileNames = filterArgs.getFileNames();
        boolean isAppending = filterArgs.isAppendModeWriter();
        StatisticsType statisticsType = filterArgs.getStatisticsType();

        //if (!isCorrectPrefix(prefixNameOutfile))
        //    throw new InvalidPrefixException("Error: the prefix contains invalid characters!");
        validatePrefix(prefixNameOutfile);

        String integerNameOutfile = prefixNameOutfile + INTEGER_NAME_FILE;
        String realNameOutfile = prefixNameOutfile + REAL_NAME_FILE;
        String stringNameOutfile = prefixNameOutfile + STRING_NAME_FILE;

        outfilePath = pathChecking(outfilePath); // проверка на корректность вых. пути

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

        removeFile(outfilePath + integerNameOutfile); // как учесть пустой файл, который уже был создан ранее,
        removeFile(outfilePath + realNameOutfile);    // но он не использовался в проге, т.е. нужно не удалять.
        removeFile(outfilePath + stringNameOutfile);  // этот код его удалит

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

        BufferedReader[] readers = new BufferedReader[listInfileNames.size()];

        //StringBuilder currentLine = new StringBuilder();

        try {
            for (int i = 0; i < listInfileNames.size(); i++) {
                readers[i] = new BufferedReader(new FileReader(listInfileNames.get(i)));
                System.out.println("Reader[" + i + "] = " + readers[i].lines().count()); //delete
            }
            boolean isEmptyFiles = true;

            while (isEmptyFiles) {

                isEmptyFiles = false;

                for (int i = 0; i < listInfileNames.size(); i++) {

                    //String currentSymbols = readers[i].read();
                    String currentLine = String.valueOf(readers[i].read());


                    //System.out.println("listInfileNames: " + listInfileNames.get(i));
                    System.out.println(currentLine);

                    if (currentLine != null && !currentLine.trim().isEmpty()) {
                        currentLine = currentLine.trim();

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

                        isEmptyFiles = true;
                    }
                }
            }

        } catch (IOException exception) {

            System.err.println("Error reading file:");
            System.err.println(exception.getMessage());

        } finally {

            for (BufferedReader reader : readers)
                if (reader != null)
                    reader.close();
        }
    }

    private String pathChecking(String outfilePath) {

        if(!outfilePath.isEmpty()) {

            //if (!isCorrectOutfilePath(outfilePath))
            //    throw new InvalidPathException("Error: the outfile path contains invalid characters!");
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
        String regex = "^([a-zA-Z]:\\\\)?[^*:?<>|\"]*"; // убрано 2 знака: / \
        if (!regexMatch(regex, outfilePath)) {
            throw new InvalidPathException("Error: the outfile path contains invalid characters!");
        }
    }

}
