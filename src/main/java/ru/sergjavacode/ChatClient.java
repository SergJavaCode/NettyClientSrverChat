package ru.sergjavacode;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class ChatClient {
    private static String HOST;
    private static int PORT;
    static String clientName;

    public static void main(String[] args) throws InterruptedException, IOException {
        // Читаем сетевые настройки для клиента из файла
        FileSettingsReader fileSettingsReader = new FileSettingsReader();
        Map<String, String> mapHostPort = new HashMap<>();
        mapHostPort = fileSettingsReader.readFileSettings("settings.txt");
        if (mapHostPort.isEmpty()) {
            System.out.println("Файл настроек сконфигурирован не верно. Программа будет закрыта.");
            System.exit(0);
        }
        HOST = mapHostPort.get("HOST");
        PORT = Integer.parseInt(mapHostPort.get("PORT"));
        // стартуем клиента
        new ChatClient().start();
    }

    public void start() throws InterruptedException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // Получаем имя пользователя для сессии
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите своё имя: ");
        if (scanner.hasNext()) {
            clientName = scanner.nextLine();
            System.out.println("Добро пожаловать, " + clientName);
        }

        //конфигурируем клиент
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group) // Регистрируем EventLoopGroup для обработки всех событий клиента.
                    .channel(NioSocketChannel.class)// Используем NIO для нового соединения.
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            /*
                             * Связь сокетов/каналов происходит в потоках байтов.
                             * Декодер и кодировщик строк помогает преобразовывать байты и строки.
                             */
                            p.addLast(new StringDecoder());
                            p.addLast(new StringEncoder());
                            // Обработчик клиента, который содержит логику для чата.
                            p.addLast(clientName, new ChatClientHandler());
                        }
                    });

            // Стартуем клиент.
            ChannelFuture f = b.connect(HOST, PORT).sync();

            /*
             * Выполняем итерацию и принимаем вводимые сообщения чата от пользователя, а затем отправляем их на сервер.
             */
            while (scanner.hasNext()) {
                String input = scanner.nextLine();
                if (input.matches("/exit")) {
                    Channel channel = f.sync().channel();
                    channel.writeAndFlush("Клиент " + "[" + clientName + "]: " + " ввел " + input + " и покидает чат");
                    channel.flush();
                    System.exit(0);
                    break;
                }
                Channel channel = f.sync().channel();
                channel.writeAndFlush("[" + formatter.format(LocalDateTime.now()) + "] - " + "[" + clientName + "]: " + input);
                channel.flush();
            }

            // Ждем когда соединение будет разорвано
            f.channel().closeFuture().sync();
        } finally {
            // Прекращаемивсе event loop и поток.
            group.shutdownGracefully();
        }
    }
}