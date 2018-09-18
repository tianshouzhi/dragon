import com.sun.org.apache.xpath.internal.operations.String;

import java.io.InputStream;
import java.net.Socket;

/**
 * Created by tianshouzhi on 2018/5/25.
 */
public class MysqlConnection {
	public static void main(String[] args) throws Exception {
		Socket socket = new Socket("localhost", 3306);
		InputStream inputStream = socket.getInputStream();
		byte[] payload_length = new byte[3];
		inputStream.read(payload_length);
		int payloadLength = byteArrayToInt(payload_length);
		System.out.println(payloadLength);

		byte[] sequence_id = new byte[1];
		inputStream.read(sequence_id);
		int sequenceId = byteInt(sequence_id);

		byte[] payload = new byte[payloadLength];
		inputStream.read(payload);
		// System.out.println(java.lang.String.valueOf(payload));
	}

	public static int byteArrayToInt(byte[] b) {
		return b[0] & 0xFF | (b[1] & 0xFF) << 8 | (b[2] & 0xFF) << 16;
	}

	public static int byteInt(byte[] b) {
		return b[0] & 0xFF;
	}
}
