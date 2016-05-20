package cc.lison.letschat;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;

import cc.lison.pojo.EchoFile;
import cc.lison.pojo.EchoMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.FileRegion;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;

public class ActivityMain extends Activity {

    //https://waylau.gitbooks.io/essential-netty-in-action/content/CORE%20FUNCTIONS/Decoding%20delimited%20and%20length-based%20protocols.html

    EditText et_scroll;
    EditText et_msg;
    Button btn_send, btn_pic;

    Activity activity;

    // String host = "192.168.1.200";
    String host = "172.16.71.76";
    int port = 8080;

    NioEventLoopGroup group;
    Channel channel;
    ChannelFuture channelFuture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    void initView() {
        activity = this;
        connect(handler);

        et_scroll = (EditText) super.findViewById(R.id.et_scroll);
        et_msg = (EditText) super.findViewById(R.id.et_msg);
        btn_send = (Button) super.findViewById(R.id.btn_send);
        btn_pic = (Button) super.findViewById(R.id.btn_pic);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = String.valueOf(et_msg.getText()) + "\r\n";
                if (msg.length() != 0) {
                    handler.obtainMessage(0x03).sendToTarget();
                }
            }
        });

        btn_pic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            }
        });
    }

    void connect(final Handler handler) {
        new Thread() {
            @Override
            public void run() {
                try {
                    group = new NioEventLoopGroup();

                    Bootstrap bootstrap = new Bootstrap();
                    bootstrap.group(group);
                    bootstrap.channel(NioSocketChannel.class);
                    bootstrap.handler(new LetsChatInitializer(handler));
                    bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
                    bootstrap.option(ChannelOption.TCP_NODELAY, true);

                    channelFuture = bootstrap.connect(new InetSocketAddress(host, port));
                    channel = channelFuture.sync().channel();
                    channelFuture.addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture channelFuture) throws Exception {
                            handler.obtainMessage(0x00).sendToTarget();
                        }
                    });

                    channel.closeFuture().sync();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    Uri uri;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            uri = data.getData();
            Log.e("uri", uri.toString());

            //ContentResolver cr = this.getContentResolver();
            //try {
            //    Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));

            //ImageView imageView = (ImageView) findViewById(R.id.iv01);
            ///* 将Bitmap设定到ImageView */
            //imageView.setImageBitmap(bitmap);

            handler.obtainMessage(0x04).sendToTarget();

            //} catch (FileNotFoundException e) {
            //    e.printStackTrace();
            //} catch (IOException e) {
            //    e.printStackTrace();
            //}
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private int dataLength = 1024;
    private int sumCountpackage = 0;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String m = msg.obj + "";
            switch (msg.what) {
                case 0x00:
                    //online

                    String hello = new String("I'm in!");

                    //ByteBuf buf = Unpooled.buffer(hello.length());
                    //buf.readBytes(hello.getBytes());
                    //channel.writeAndFlush(buf);
                    //channel.read();

                    EchoMessage em = new EchoMessage();
                    byte[] b = hello.getBytes();
                    em.setBytes(b);
                    em.setSumCountPackage(b.length);
                    em.setCountPackage(1);
                    em.setSend_time(System.currentTimeMillis());

                    channel.writeAndFlush(em);

                    break;
                case 0x01:
                    //receive
                    et_scroll.setText(et_scroll.getText() + m + "\r\n");
                    break;
                case 0x02:
                    //send complete
                    et_msg.setText("");
                    break;
                case 0x03:
                    //send txt
                    String mmm = String.valueOf(et_msg.getText() + "");
                    if (mmm.length() == 0)
                        return;

                    EchoMessage emm = new EchoMessage();
                    emm.setSend_time(System.currentTimeMillis());

                    byte[] bb = mmm.getBytes();
                    emm.setBytes(bb);
                    emm.setSumCountPackage(bb.length);
                    emm.setCountPackage(1);
                    emm.setSend_time(System.currentTimeMillis());

                    channel.writeAndFlush(emm).addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture channelFuture) throws Exception {
                            handler.obtainMessage(0x02).sendToTarget();
                        }
                    });

                    break;
                case 0x04:
                    //send pic

                    try {

                        ContentResolver resolver = activity.getContentResolver();
                        InputStream reader = resolver.openInputStream(uri);
                        byte[] bytes = new byte[reader.available()];

                        reader.read(bytes);
                        reader.close();

                        //byte[] bytes=toByteArray(filePath);

                        if ((bytes.length % dataLength == 0))
                            sumCountpackage = bytes.length / dataLength;
                        else
                            sumCountpackage = (bytes.length / dataLength) + 1;

                        //LOGGER.debug("文件总长度:" + randomAccessFile.length());
                        Log.i("TAG", "文件总长度:" + bytes.length);

                        //if (randomAccessFile.read(bytes) != -1) {
                        //for (int i = 0; i < bytes.length; i += dataLength) {
                        EchoFile msgFile = new EchoFile();
                        msgFile.setSumCountPackage(sumCountpackage);
                        //msgFile.setCountPackage(i);
                        msgFile.setCountPackage(1);

                        //byte[] b = new byte[dataLength];

                        //for (int j = i; j < dataLength; j++) {
                        //    b[i] = bytes[j];
                        //}

                        msgFile.setBytes(bytes);
                        //msgFile.setFile_md5("Iknowyournew.jpg");
                        msgFile.setFile_name(Build.MANUFACTURER + "-" + UUID.randomUUID() + ".jpg");
                        channel.writeAndFlush(msgFile);
                        //}
                        //} else {


                        System.out.println("文件已经读完");
                        //}
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException i) {
                        i.printStackTrace();
                    }

                    post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                ContentResolver resolver = activity.getContentResolver();
                                InputStream reader = resolver.openInputStream(uri);
                                byte[] bytes = new byte[reader.available()];

                                //reader.read(buffer, 0, buffer.length);

                                //while ((len=reader.read(buffer)) != 0) {
                                //channel.writeAndFlush(buffer + "\r\n");

                                //Environment.getExternalStorageDirectory()
                                //File f = new File(uri.toString());
                                //channel.writeAndFlush(new ChunkedFile(f, (int) f.length()) + "\r\n");
                                //}

                                //final ByteBuf buf = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Hi!\r\n", Charset.forName("UTF-8")));


                                reader.read(bytes);
                                reader.close();

                                channel.writeAndFlush("filelength:" + bytes.length + "\r\n");
                                channel.flush();
                                channel.read();

                                //final ByteBuf buff = Unpooled.copiedBuffer(bytes);
                                //channel.writeAndFlush(buff.readBytes(bytes));
                                channel.writeAndFlush(bytes);

                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                channel.flush();
                                channel.read();
                            }
                        }
                    });
                    break;
                default:
                    Toast.makeText(activity, "UNKNOWN MSG: " + m, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    public static byte[] toByteArray(String filename) throws IOException {

        File f = new File(filename);
        if (!f.exists()) {
            throw new FileNotFoundException(filename);
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream((int) f.length());
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(f));
            int buf_size = 1024;
            byte[] buffer = new byte[buf_size];
            int len = 0;
            while (-1 != (len = in.read(buffer, 0, buf_size))) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bos.close();
        }
    }
}