package ru.sergjavacode;

import java.util.ArrayList;
import java.util.List;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Обработчики на серверной стороне канала.
 */
public class ChatServerHandler extends SimpleChannelInboundHandler<String> {

    // Создаем лист для хранения подключенных клиентских каналов.
    static final List<Channel> channels = new ArrayList<Channel>();

    /*
     * Всякий раз, когда клиент подключается к серверу через канал, добавляем его канал в список каналов.
     */
    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        System.out.println("Подсоединение нового участника - " + ctx);
        channels.add(ctx.channel());
    }

    /*
     * Когда сообщение получено от клиента, отправляем это сообщение по всем каналам.
     * Для простоты в настоящее время мы будем отправлять полученное сообщение чата всем клиентам,
     * а не одному конкретному клиенту.
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("Сервер получил - " + msg);
        for (Channel c : channels) {
            c.writeAndFlush("-> " + msg + '\n');
        }
    }

    /*
     * В случае выбрасывания исключения закрываем канал.
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("Закрытие соединения с участником - " + ctx);
        ctx.close();
    }
}