import io.netty.channel.ChannelHandlerContext;
import org.junit.Assert;
import org.junit.Test;
import ru.sergjavacode.LogToFile;

import java.io.File;

public class TestLogToFile {
    @Test
    public void createLogFile() { //Тестируем факт создание файла лога
        LogToFile logToFile = new LogToFile();
        File file = new File("file.log");
        Assert.assertEquals(true, file.exists());
        file.deleteOnExit();
    }

}
