package ru.sergjavacode;

import io.netty.channel.*;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@ChannelHandler.Sharable
/*
 * Анотацичя Netty которая указывает, что один и тот же экземпляр аннотированного ChannelHandler
 * может быть добавлен в один или несколько ChannelPipelines несколько. По умолчанию для каждого нового соединения создается
 * новый экземпляр обработчика, что не позволит логировать срау всех клиентов.
 */
public class LogToFile extends SimpleChannelInboundHandler<String> {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final String pathFile = "file.log";

    //Отмечаем начало логирования очередной сессии
    public LogToFile() {
        try (FileWriter writerTempTxt = new FileWriter(pathFile, true)) {
            writerTempTxt.write("Логирование очередной сессии стратовало " + formatter.format(LocalDateTime.now()) + "\n");
            writerTempTxt.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //Логируем подсоединение нового участника
    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        try (FileWriter writerTempTxt = new FileWriter(pathFile, true)) {
            writerTempTxt.write("Подсоединение нового участника - " + ctx + "\n");
            writerTempTxt.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ctx.fireChannelActive();
    }

    //Логируем сообщения клиентов
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        try (FileWriter writerTempTxt = new FileWriter(pathFile, true)) {
            writerTempTxt.write(s + "\n");
            writerTempTxt.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        channelHandlerContext.fireChannelRead(s);
    }

    //Логируем закрытие соединения клиентом
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        try (FileWriter writerTempTxt = new FileWriter(pathFile, true)) {
            writerTempTxt.write("Закрытие соединения с участником - " + ctx + "\n");
            writerTempTxt.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ctx.fireExceptionCaught(cause);
    }
}
