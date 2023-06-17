public class ModbusSendGenerate {
    protected static final String TcpHead = "00000000000601";

    public static String query(){
        // 默认值十六进制 00 01 00 00 00 06 01 04 00 00 00 09
        StringBuffer query = new StringBuffer(TcpHead);
        query.append("04");
        query.append("00");
        query.append("00");
        query.append("00");
        query.append("04");
        return query.toString();
    }
}
