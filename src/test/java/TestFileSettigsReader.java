import org.junit.Assert;
import org.junit.Test;
import ru.sergjavacode.ChatServer;
import ru.sergjavacode.FileSettingsReader;
import ru.sergjavacode.LogToFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TestFileSettigsReader {
    //Тестируем правильный парсинг файла сетевых настроек
    @Test
    public void validFile() throws IOException {
        Map<String, String> mapHostPort = new HashMap<>();
        FileSettingsReader fileSettingsReader = new FileSettingsReader();
        mapHostPort = fileSettingsReader.readFileSettings("settings_test1.txt");
        Assert.assertEquals("202.33.44.123", mapHostPort.get("HOST"));
        Assert.assertEquals("901", mapHostPort.get("PORT"));
    }

    @Test
    public void noValidFiles() throws IOException {
        Map<String, String> mapHostPort = new HashMap<>();
        FileSettingsReader fileSettingsReader = new FileSettingsReader();
        mapHostPort = fileSettingsReader.readFileSettings("settings_test2.txt");
        Assert.assertEquals(null, mapHostPort.get("HOST"));
        Assert.assertEquals(null, mapHostPort.get("PORT"));

    }

    @Test
    public void noValidNullFiles() throws IOException {
        Map<String, String> mapHostPort = new HashMap<>();
        FileSettingsReader fileSettingsReader = new FileSettingsReader();
        mapHostPort = fileSettingsReader.readFileSettings("settings_test3.txt");
        Assert.assertEquals(null, mapHostPort.get("HOST"));
        Assert.assertEquals(null, mapHostPort.get("PORT"));

    }

}
