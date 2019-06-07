package io.gcat;

import io.gcat.entity.JVMParameter;
import io.gcat.parser.CMSParNewParser;
import io.gcat.parser.Parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Gcat {

    private Parser parser;

    public static void main(String[] args) throws IOException {
        if (1 != args.length) {
            throw new IllegalArgumentException("Usage:params: {gc.log}");
        }

        File logFile = new File(args[0]);
        Gcat gcat = new Gcat();
        gcat.parse(logFile);
    }

    public void parse(File logFile) throws IOException {
        try (FileReader fileReader = new FileReader(logFile);
             BufferedReader reader = new BufferedReader(fileReader)) {
            detect(reader);
            feed(parser, reader);
            parser.query(null);
        }
    }

    private void feed(Parser parser, BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            parser.feed(line);
        }
    }

    private void detect(BufferedReader reader) throws IOException {
        String jvmVersion = null;
        JVMParameter jvmParameter = null;

        int lineCount = 0;
        String line;
        while ((line = reader.readLine()) != null) {
            if (20 < ++lineCount) {
                throw new IllegalStateException("can not found collector type in top 20 lines!");
            }
            if (line.startsWith("Java HotSpot")) {
                jvmVersion = getJvmVersion(line);
            } else if (line.startsWith("CommandLine flags: ")) {
                jvmParameter = getJVMParameter(line);
                break;
            }
        }

        parser = getParser(jvmVersion, jvmParameter);
    }

    private JVMParameter getJVMParameter(String line) {
        JVMParameter jvmParamter = new JVMParameter();
        String COMMAND_LINE_FLAGS = "CommandLine flags: ";
        String flagStr = line.substring(COMMAND_LINE_FLAGS.length(), line.length());

        int s = " -XX:".length();
        while (s < flagStr.length()) {
            int e = flagStr.indexOf(" -XX:", s);
            if (e == -1) {
                e = flagStr.length();
            }
            if (flagStr.charAt(s) == '+') {
                String key = flagStr.substring(s + 1, e).trim();
                jvmParamter.put(key, "true");
            } else {
                String[] pair = flagStr.substring(s, e).split("=", 2);
                if (pair.length != 2) {
                    System.out.println(pair[0]);
                }
                jvmParamter.put(pair[0], pair[1]);
            }

            s = e + " -XX:".length();
        }

        return jvmParamter;
    }

    private String getJvmVersion(String line) {
        Matcher m = Pattern.compile("(1\\.\\d+)\\.\\d").matcher(line);
        if (m.find()) {
            return m.group(1);
        }

        return null;
    }

    private Parser getParser(String jvmVersion, JVMParameter jvmParamter) {
        Objects.requireNonNull(jvmVersion);
        Objects.requireNonNull(jvmParamter);

        Boolean useParNewGC = jvmParamter.is("UseParNewGC");
        Boolean useConcMarkSweepGC = jvmParamter.is("UseConcMarkSweepGC");

        if (useParNewGC && useConcMarkSweepGC) {
            return new CMSParNewParser(jvmVersion, jvmParamter);
        } else {
            throw new IllegalStateException("can not found match parser in flags!");
        }
    }
}