public class HexCodec { 
  private static final char[] kDigits = { 
    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
    'a', 'b', 'c', 'd', 'e', 'f'
  } ;
  
  public static char[] bytesToHex(byte[] raw) { 
    int length = raw.length;
    char[] hex = new char[length * 2];
    for (int i = 0; i < length; i++) { 
      int value = (raw[i] + 256) % 256;
      int highIndex = value >> 4;
      int lowIndex = value & 0x0f;
      hex[i * 2 + 0] = kDigits[highIndex];
      hex[i * 2 + 1] = kDigits[lowIndex];
    } 
    return hex;
  } 
  
  public static byte[] hexToBytes(char[] hex) { 
    int length = hex.length / 2;
    byte[] raw = new byte[length];
    for (int i = 0; i < length; i++) { 
      int high = Character.digit(hex[i * 2], 16);
      int low = Character.digit(hex[i * 2 + 1], 16);
      int value = (high << 4) | low;
      if (value > 127) value -= 256;
      raw[i] = (byte)value;
    } 
    return raw;
  } 
  
  public static byte[] hexToBytes(String hex) { 
    return hexToBytes(hex.toCharArray());
  } 
} 

