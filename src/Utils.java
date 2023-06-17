public class Utils {
    static final char[] HEX_CHAR_TABLE = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };

    //将接收到的命令每隔两个字符加上一个空格
    public static String addSpace(String acceptCommand) {
        String regex = "(.{2})";
        acceptCommand = acceptCommand.replaceAll(regex, "$1 ");
        return acceptCommand;
    }

    public static byte charToByte(char c) {
        return (byte) "0123456789ABC-DEF".indexOf(c);
    }

    public static byte[] hexStringToBytes(String data) {
        if (data == null || "".equals(data))
            return null;
        data = data.toUpperCase();
        int length = data.length() / 2;
        char[] dataChars = data.toCharArray();
        byte[] byteData = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            byteData[i] = (byte) (charToByte(dataChars[pos]) << 4 | charToByte(dataChars[pos + 1]));
        }
        return byteData;
    }

    public static String toHexString(byte[] data) {
        if (data == null || data.length == 0)
            return null;
        byte[] hex = new byte[data.length * 2];
        int index = 0;
        for (byte b : data) {
            int v = b & 0xFF;
            hex[index++] = (byte) HEX_CHAR_TABLE[v >>> 4];
            hex[index++] = (byte) HEX_CHAR_TABLE[v & 0xF];
        }
        return new String(hex);
    }

    // 计算电流的值
    // 寄存器的值为 2ef（十六进制）；十进制为 751；
    // 结果 = 751 * 0.01 * 0.5
    // 默认 固定两位小数 需要乘以 0.01
    // 默认比例1000：1，实际比例500：1，需要乘以 0.5
    public static double calculateCurrentSize(int a) {
        return a * 0.01 * 0.5;
    }

    // 字节数组 计算 为 浮点数
    public static double[] getFloatArrByByteArr(byte[] arr) {
        int length = arr.length;
        double[] result = new double[length];
        for (int i = 0; i < length; i++) {
            int a = Byte.toUnsignedInt(arr[i]);
            result[i] = calculateCurrentSize(a);
        }
        return result;
    }
}
