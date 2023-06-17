import org.omg.Messaging.SyncScopeHelper;

import javax.rmi.CORBA.Util;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//客户端
public class TcpClient extends Thread {
    private String ip;
    private int port;

    private Socket socket;


    private boolean close = false; // 关闭连接标志位，true表示关闭，false表示连接

    public TcpClient(String host, int port) {
        setIp(host);
        setPort(port);
        initSocket();
    }

    // 初始化 socket
    public void initSocket() {
        //1.创建Socket对象，指明服务器IP和端口号
        try {
            InetAddress address = InetAddress.getByName(getIp());
            System.out.println(address);
            socket = new Socket(getIp(), getPort());
            socket.setKeepAlive(true); // 开启保持活动状态的套接

            close = !sendData(socket); // 发送初始数据，发送成功则表示已经连接上，发送失败表示已经断开

        } catch (Exception e) {
            System.err.println(e);
        }
    }

    /**
     * 读数据线程
     */
    public void run() {
        while (true) {
            try {
                sleep(60 * 1000);
                sendData(socket);
            } catch (Exception e) {
                System.out.println(e);
            }

            //---------读数据---------------------------
            close = isServerClose(socket);//判断是否断开
            if (!close) { // 没有断开，开始读数据
                read(socket);
            }

            //---------创建连接-------------------------
            while (close) { // 已经断开，重新建立连接
                try {
                    System.out.println("重新建立连接：" + getIp() + ":" + getPort());
                    InetAddress address = InetAddress.getByName(getIp());
                    socket = new Socket(address, getPort());
                    socket.setKeepAlive(true);
                    close = !sendData(socket);
                    System.out.println("建立连接成功：" + getIp() + ":" + getPort());
                } catch (Exception se) {
                    System.out.println("创建连接失败:" + getIp() + ":" + getPort());
                    close = true;
                }
            }
        }
    }

    /**
     * 发送数据，发送失败返回false,发送成功返回true
     *
     * @param _socket
     * @return
     */
    public Boolean sendData(Socket _socket) {
        String query = ModbusSendGenerate.query();
        System.out.println("向服务端发送数据： " + query);
        byte[] a = Utils.hexStringToBytes(query);
        try {
            OutputStream out = _socket.getOutputStream();
            out.write(a);
            return true;
        } catch (Exception se) {
            se.printStackTrace();
            return false;
        }

    }


    // 读取数据
    public void read(Socket _socket) {
        try {
            InputStream input = _socket.getInputStream();
            byte[] sn = new byte[50];
            input.read(sn);
            System.out.println("接收数据=========================================");
            System.out.println(Utils.addSpace(Utils.toHexString(sn)));

            handleData(sn);
            System.out.println("接收数据==========================================end");
        } catch (Exception e) {
            System.out.println(e);
        }
    }


    // 处理数据
    private void handleData(byte[] arr) {

        // modbus tcp mbap
        byte[] head = new byte[7];
        System.arraycopy(arr, 0, head, 0, head.length);

        // modbus tcp pdu
        int length = (int) head[5] - 1;
        byte[] otherData = new byte[length];
        System.arraycopy(arr, head.length, otherData, 0, otherData.length);
        System.out.println("解析---请求头MBAP: " + Utils.addSpace(Utils.toHexString(head)));
        System.out.println("解析---请求尾PDU: " + Utils.addSpace(Utils.toHexString(otherData)));

        // 电流寄存器数组
        // 完整报文的 第九个字节 代表返回的 字节长度，两个字节代表一个值
        // pdu 的 第二个字节是 数据有效长度
        if (otherData.length > 2) {
            int validLength = Byte.toUnsignedInt(otherData[1]);
            if (validLength % 2 != 0) {
                return;
            } else {
                int num = validLength / 2;
                System.out.println("解析---通道个数： " + num);
            }


            // 有效字节
            byte[] validByte = new byte[validLength];
            System.arraycopy(otherData, 2, validByte, 0, validLength);
            System.out.println("解析---电流有效字节: " + Utils.addSpace(Utils.toHexString(validByte)));

            // 字节 转换为 int
            ArrayList validInt = new ArrayList();
            for (int i = 0; i < validByte.length; i++) {

                if (i % 2 == 0) {
                    byte[] byteArray = {validByte[i], validByte[i + 1]};
                    System.out.println("解析---通道" + (i / 2 + 1) + "--16进制: " + Utils.addSpace(Utils.toHexString(byteArray)));
                    ByteBuffer buffer = ByteBuffer.wrap(byteArray);
                    int result = buffer.getShort();
                    System.out.println("解析---通道" + (i / 2 + 1) + "--10进制: " + result);
                    float a = (float) Utils.calculateCurrentSize(result);
                    System.out.println("解析---通道" + (i / 2 + 1) + "电流真实值: " + a);
                    validInt.add(a);
                }
            }



            System.out.println(getIp() + "：" +getPort() +"---全部通道电流为 : " + validInt.toString());
        }

        return;
    }

    /**
     * 判断是否断开连接，断开返回true,没有返回false
     *
     * @param socket
     * @return
     */
    public Boolean isServerClose(Socket socket) {
        try {
            socket.sendUrgentData(0xFF);// 发送1个字节的紧急数据，默认情况下，服务器端没有开启紧急数据处理，不影响正常通信
            return false;
        } catch (Exception se) {
            return true;
        }
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }


    public static void main(String[] args) {
        TcpClient tc = new TcpClient("127.0.0.1", 10123);
        tc.run();
    }
}