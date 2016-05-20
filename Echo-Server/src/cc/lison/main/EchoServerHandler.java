package cc.lison.main;

import cc.lison.pojo.EchoFile;
import cc.lison.pojo.EchoMessage;

import cc.lison.pojo.EchoPojo;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * Created by Lison-Liou on 5/17/2016.
 */
public class EchoServerHandler extends SimpleChannelInboundHandler<Object> {

    private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 文件存储路径
     */
    private String FILE_SAVE_PATH = "D:";

    /**
     * byte[] seek step size
     */
    private int DATA_LENGTH = 1024;

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

        Channel incoming = ctx.channel();

        if (msg instanceof EchoFile) {
            EchoFile ef = (EchoFile) msg;
            int SumCountPackage = ef.getSumCountPackage();
            int countPackage = ef.getCountPackage();
            byte[] bytes = ef.getBytes();
            String file_name = ef.getFile_name();

            String path = FILE_SAVE_PATH + File.separator + file_name;
            File file = new File(path);
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.seek(countPackage * DATA_LENGTH - DATA_LENGTH);
            randomAccessFile.write(bytes);

            System.out.println("SYSTEM TOTAL PACKAGE COUNT：" + ef.getSumCountPackage());
            System.out.println("SYSTEM NOT IS THE " + countPackage + "th PACKAGE");
            System.out.println("SYSTEM PACKAGE COUNT: " + bytes.length);

            countPackage = countPackage + 1;

            if (countPackage <= SumCountPackage) {
                ef.setCountPackage(countPackage);
                ctx.writeAndFlush(ef);
                randomAccessFile.close();

                ctx.writeAndFlush("SYSTEM " + countPackage + " UPLOADED");
            } else {
                randomAccessFile.close();
                ctx.close();
                ctx.writeAndFlush("SYSTEM " + ef.getFile_name() + " UPLOAD FINISHED");
            }
        } else if (msg instanceof EchoMessage) {
            EchoMessage em = (EchoMessage) msg;
            System.out.println("RECEIVED: " + ctx.channel().remoteAddress() + " " + new String(em.getBytes(), CharsetUtil.UTF_8));

            EchoMessage em_echo = EchoMessage.buildMessage("SYSTEM - GOT YOUR MESSAGE", EchoMessage.MessageType.BUSINESS2SERVER);

//            for (Channel channel : channels) {
//                String m = new String(em.getBytes(), CharsetUtil.UTF_8);
//                if (channel == incoming)
//                    //incoming.writeAndFlush("YOU " + m);
//                    incoming.writeAndFlush(em_echo);
//                else
//                    channel.writeAndFlush(incoming.remoteAddress() + " " + m);
//            }

            incoming.writeAndFlush(em_echo);
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {

        Channel incoming = ctx.channel();
        System.out.println("SYSTEM CHANNEL REMOVED: " + incoming.remoteAddress());

//        for (Channel channel : channels) {
//            if (channel == incoming)
//                incoming.writeAndFlush("YOU OFFLINE");
//            else
//                channel.writeAndFlush(incoming.remoteAddress() + " OFFLINE");
//        }

        channels.remove(incoming);
        System.out.println("SYSTEM CHANNEL SIZE: " + channels.size());

        EchoPojo pojo = EchoMessage.buildMessage("", EchoMessage.MessageType.CLIENT_OFFLINE);
        incoming.writeAndFlush(pojo);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

        Channel incoming = ctx.channel();
        System.out.println("SYSTEM CHANNEL ADDED: " + incoming.remoteAddress());

        channels.add(incoming);
        System.out.println("SYSTEM CHANNEL SIZE: " + channels.size());

//        for (Channel channel : channels) {
//            if (channel == incoming)
//                incoming.writeAndFlush("YOU ONLINE");
//            else
//                channel.writeAndFlush(incoming.remoteAddress() + " ONLINE");
//        }

        EchoPojo pojo = EchoMessage.buildMessage("", EchoMessage.MessageType.SERVER_ACCEPT);
        incoming.writeAndFlush(pojo);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        EchoPojo pojo = EchoMessage.buildMessage("", EchoMessage.MessageType.CLIENT_ONLINE);
        ctx.writeAndFlush(pojo);
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        EchoPojo pojo = EchoMessage.buildMessage("", EchoMessage.MessageType.CLIENT_OFFLINE);
        ctx.writeAndFlush(pojo);
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        EchoPojo pojo = EchoMessage.buildMessage(cause.getMessage(), EchoMessage.MessageType.SERVER_EXCEPTION);
        ctx.writeAndFlush(pojo);
        ctx.close();
    }
}
