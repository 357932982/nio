package main.java.com.nio.test.block;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

public class CharSetUtil {
    public static String decode(ByteBuffer buffer, String charsetName) {
        Charset charset = Charset.forName(charsetName);
        CharBuffer msg = charset.decode(buffer);
        return msg.toString();
    }

    public static ByteBuffer encode(String msg, String charsetName) {
        Charset charset = Charset.forName(charsetName);
        ByteBuffer byteBuffer = charset.encode(msg);
        return byteBuffer;
    }
}
