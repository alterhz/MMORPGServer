package org.game.core.net;

/**
 * RC4加密算法实现
 * 
 * RC4是一种对称加密算法，使用相同的密钥进行加密和解密。
 * 该实现包括密钥调度算法(KSA)和伪随机生成算法(PRGA)。
 */
public class RC4 {
    
    private static final int SBOX_LENGTH = 256;
    private int[] sbox;
    private int[] key;
    
    /**
     * 构造函数，使用指定的密钥初始化RC4
     * 
     * @param keyStr 密钥字符串
     */
    public RC4(String keyStr) {
        this.key = convertStringToBytes(keyStr);
        this.sbox = new int[SBOX_LENGTH];
        initialize();
    }
    
    /**
     * 初始化S盒
     * 使用KSA(Key Scheduling Algorithm)算法初始化S盒
     */
    private void initialize() {
        int keyLen = key.length;
        // 初始化S盒
        for (int i = 0; i < SBOX_LENGTH; i++) {
            sbox[i] = i;
        }
        
        // KSA算法
        int j = 0;
        for (int i = 0; i < SBOX_LENGTH; i++) {
            j = (j + sbox[i] + key[i % keyLen]) % SBOX_LENGTH;
            // 交换sbox[i]和sbox[j]
            int temp = sbox[i];
            sbox[i] = sbox[j];
            sbox[j] = temp;
        }
    }
    
    /**
     * 加密数据
     * 
     * @param plaintext 明文数据
     * @return 加密后的数据
     */
    public byte[] encrypt(byte[] plaintext) {
        return crypt(plaintext);
    }
    
    /**
     * 解密数据
     * 
     * @param ciphertext 密文数据
     * @return 解密后的数据
     */
    public byte[] decrypt(byte[] ciphertext) {
        return crypt(ciphertext);
    }
    
    /**
     * RC4加解密核心算法
     * 
     * @param data 输入数据
     * @return 处理后的数据
     */
    private byte[] crypt(byte[] data) {
        // 重新初始化S盒以保证每次调用的独立性
        int[] sboxCopy = sbox.clone();
        
        byte[] result = new byte[data.length];
        int i = 0, j = 0;
        
        // PRGA算法
        for (int k = 0; k < data.length; k++) {
            i = (i + 1) % SBOX_LENGTH;
            j = (j + sboxCopy[i]) % SBOX_LENGTH;
            
            // 交换sboxCopy[i]和sboxCopy[j]
            int temp = sboxCopy[i];
            sboxCopy[i] = sboxCopy[j];
            sboxCopy[j] = temp;
            
            // 生成密钥流字节
            int t = (sboxCopy[i] + sboxCopy[j]) % SBOX_LENGTH;
            int keyStreamByte = sboxCopy[t];
            
            // XOR操作
            result[k] = (byte) (data[k] ^ keyStreamByte);
        }
        
        return result;
    }
    
    /**
     * 将字符串转换为字节数组
     * 
     * @param str 输入字符串
     * @return 字节数组
     */
    private int[] convertStringToBytes(String str) {
        char[] chars = str.toCharArray();
        int[] result = new int[chars.length];
        for (int i = 0; i < chars.length; i++) {
            result[i] = (int) chars[i];
        }
        return result;
    }
    
    /**
     * 将整数转换为4字节的字节数组（大端序）
     * 
     * @param value 要转换的整数
     * @return 4字节的字节数组
     */
    public static byte[] intToBytes(int value) {
        return new byte[] {
                (byte) (value >> 24),
                (byte) (value >> 16),
                (byte) (value >> 8),
                (byte) value
        };
    }
    
    /**
     * 将4字节的字节数组转换为整数（大端序）
     * 
     * @param bytes 4字节的字节数组
     * @return 转换后的整数
     */
    public static int bytesToInt(byte[] bytes) {
        if (bytes.length != 4) {
            throw new IllegalArgumentException("字节数组长度必须为4");
        }
        return ((bytes[0] & 0xFF) << 24) |
               ((bytes[1] & 0xFF) << 16) |
               ((bytes[2] & 0xFF) << 8)  |
               (bytes[3] & 0xFF);
    }
}