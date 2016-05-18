package cc.lison.main;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Created by Lison-Liou on 5/17/2016.
 */
public class EchoServer {

    /**
     * 服务端口.
     */
    static int PORT = 8080;

    /**
     * 启动Netty的方法. 方法添加日期：2014-10-11 <br>
     * 创建者:刘源
     */
    public void initialize() {
        ServerBootstrap server = new ServerBootstrap();
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            server.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new InitializerPipeline()).option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true).childOption(ChannelOption.TCP_NODELAY, true);
            ChannelFuture f = server.bind(PORT).sync();
            System.out.println("SYSTEM - SERVER PORT: " + PORT);
            f.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            System.out.println("SYSTEM ERROR: " + e.getMessage());
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    /**
     * 应用程序入口.
     */
    public static void main(String[] args) {

        if (args != null && args.length != 0) {
            try {
                PORT = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {

            }
        }

        new EchoServer().initialize();
    }
}