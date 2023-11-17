package ru.sergjavacode;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//Класс парсинга файла сетевых настроек
public class FileSettingsReader {

    public Map<String, String> readFileSettings(String path) throws IOException {
        File file = new File(path);
        Map<String, String> mapHostPort = new HashMap<>();
        LineIterator iterator = FileUtils.lineIterator(file);
        Pattern patternHost = Pattern.compile("([0-9]{1,3}[\\.]){3}[0-9]{1,3}");
        Pattern patternPort = Pattern.compile("\\d+$");

        while (iterator.hasNext()) {
            String line = iterator.next();
            Matcher matcherHost = patternHost.matcher(line);
            Matcher matcherPort = patternPort.matcher(line);
            if (line.contains("HOST") & matcherHost.find()) {
                mapHostPort.put("HOST", matcherHost.group());
            }
            if (line.contains("PORT") & matcherPort.find()) {
                mapHostPort.put("PORT", matcherPort.group());
            }
        }
        return mapHostPort;
    }
}
