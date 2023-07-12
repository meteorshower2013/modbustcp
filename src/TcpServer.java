

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;


import static java.lang.Thread.sleep;

//服务器端
public class TcpServer {
    public static void main(String[] args) {

        // 1.创建服务器端的ServerSocket，指明自己的端口号
        try {
            ServerSocket server = new ServerSocket(12344);
            // 2.调用accept表示监听客户端的socket
            Socket client = server.accept();
            // 3.获取一个输入流
            InputStream in = client.getInputStream();
            OutputStream out = client.getOutputStream();


            // 4.获取输入流中的数据
            while (true) {
                int data = 0;
                if ((data = in.read()) != -1) {
                    byte[] head = new byte[6];
                    head[0] = (byte) data;

                    in.read(head, 1, 5);
                    System.out.println("请求头" + Utils.addSpace(Utils.toHexString(head)));


                    // 获取长度

                }

            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.err.println(e);
        }
    }
}