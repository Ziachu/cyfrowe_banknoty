package Support;

import java.nio.ByteBuffer;

public class Converter {
	
	public static byte[] intToByte(int i) {
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.putInt(i);
		
		return bb.array();
	}
	
	public static int byteToInt(byte[] arr) {
		ByteBuffer bb = ByteBuffer.wrap(arr);
		
		return bb.getInt();
	}
	
	public static byte[] doubleToByte(double value) {
	    byte[] bytes = new byte[8];
	    ByteBuffer.wrap(bytes).putDouble(value);
	    
	    return bytes;
	}

	public static double byteToDouble(byte[] bytes) {
	    return ByteBuffer.wrap(bytes).getDouble();
	}
}
