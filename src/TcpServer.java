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
            ServerSocket server = new ServerSocket(10123);
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
                    int length = (int) head[5];
                    // 主要数据
                    byte[] overData = new byte[length];
                    for (int i = 0; i < length; i++) {
                        overData[i] = (byte) in.read();
                    }
                    System.out.println(Utils.addSpace(Utils.toHexString(overData)));

                    // 完整数据
                    byte[] newData = new byte[head.length + overData.length];

                    System.arraycopy(head, 0, newData, 0, head.length);
                    System.arraycopy(overData, 0, newData, head.length, overData.length);
                    System.out.println("123");
                    System.out.println(Utils.addSpace(Utils.toHexString(newData)));

                    try {
                        sleep(2000);
                        String a = "000100001215010412000000000000000000000000000000000000";
                        String b = "00010000000B01040802EF0001000002EF";
                        out.write(Utils.hexStringToBytes(b));
                        // ModbusReceiveAnalysis.analysis(Utils.addSpace(Utils.toHexString(newData)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}