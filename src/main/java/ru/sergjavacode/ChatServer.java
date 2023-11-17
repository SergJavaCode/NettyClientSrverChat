package ru.sergjavacode;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import java.util.HashMap;
import java.util.Map;

public final class ChatServer {
    private static int PORT;
    private ChatServer() {
    }
    public static class SingletonHolder {
        public static final ChatServer HOLDER_INSTANCE = new ChatServer();
    }
    //для нашего случая делаем сервер синглтоном
    public static ChatServer getChatServerObj() {
        return SingletonHolder.HOLDER_INSTANCE;
    }

    public static void main(String[] args) throws Exception {

        /*
         * Конфигурируем сервер.
         */
        FileSettingsReader fileSettingsReader = new FileSettingsReader();
        Map<String, String> mapHostPort= new HashMap<>();
        mapHostPort = fileSettingsReader.readFileSettings("settings.txt");
        if (mapHostPort.isEmpty()) {
            System.out.println("Файл настроек сконфигурирован не верно. Программа будет закрыта.");
            System.exit(0);
        }
        PORT= Integer.parseInt(mapHostPort.get("PORT"));
        // Создаем группы скрвера и клинта. Boss принимает соединения клиентов.
        // Worker брабатывает дальнейшее общение через соединения.
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        //выделяем отдельную групу с одним потоком для логирования
        EventLoopGroup logWriterGroup = new NioEventLoopGroup(1);
        LogToFile logToFile = new LogToFile();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup) // Регистрируем boss & worker группы
                    .channel(NioServerSocketChannel.class)// Используем NIO для регистрации новых подключений.
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            /*
                             * Общение между сокетами/каналами происходит в потоках байтов.
                             * Декодер и кодировщик строк помогает преобразовывать байты и строки.
                             */
                            p.addLast(new StringDecoder());
                            p.addLast(new StringEncoder());
                            p.addLast(logWriterGroup, logToFile);
                            // Обработчик сервера, который содержит логику для чата.
                            p.addLast(new ChatServerHandler());

                        }
                    });

            // Стартуем сервер
            ChannelFuture f = b.bind(PORT).sync();
            System.out.println("Чат-сервер стартовал. Ждём клиентов.");

            // Ждем пока server socket будет закрыт.
            f.channel().closeFuture().sync();
        } finally {
            // Прекращаемивсе event loops и все потоки.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}