using System;

namespace ZGame
{
    /// <summary>
    /// RC4加密算法实现类
    /// </summary>
    public class RC4
    {
        private const int SBOX_LENGTH = 256;
        private readonly int[] sbox;
        private readonly int[] key;

        /// <summary>
        /// 构造函数，使用指定的密钥初始化RC4
        /// </summary>
        /// <param name="keyStr">密钥字符串</param>
        public RC4(string keyStr)
        {
            this.key = ConvertStringToBytes(keyStr);
            this.sbox = new int[SBOX_LENGTH];
            Initialize();
        }

        /// <summary>
        /// 初始化S盒
        /// 使用KSA(Key Scheduling Algorithm)算法初始化S盒
        /// </summary>
        private void Initialize()
        {
            int keyLen = key.Length;
            // 初始化S盒
            for (int i = 0; i < SBOX_LENGTH; i++)
            {
                sbox[i] = i;
            }

            // KSA算法
            int j = 0;
            for (int i = 0; i < SBOX_LENGTH; i++)
            {
                j = (j + sbox[i] + key[i % keyLen]) % SBOX_LENGTH;
                // 交换sbox[i]和sbox[j]
                int temp = sbox[i];
                sbox[i] = sbox[j];
                sbox[j] = temp;
            }
        }

        /// <summary>
        /// 加密数据
        /// </summary>
        /// <param name="plaintext">明文数据</param>
        /// <returns>加密后的数据</returns>
        public byte[] Encrypt(byte[] plaintext)
        {
            return Crypt(plaintext);
        }

        /// <summary>
        /// 解密数据
        /// </summary>
        /// <param name="ciphertext">密文数据</param>
        /// <returns>解密后的数据</returns>
        public byte[] Decrypt(byte[] ciphertext)
        {
            return Crypt(ciphertext);
        }

        /// <summary>
        /// RC4加解密核心算法
        /// </summary>
        /// <param name="data">输入数据</param>
        /// <returns>处理后的数据</returns>
        private byte[] Crypt(byte[] data)
        {
            // 重新初始化S盒以保证每次调用的独立性
            int[] sboxCopy = (int[])sbox.Clone();

            byte[] result = new byte[data.Length];
            int i = 0, j = 0;

            // PRGA算法
            for (int k = 0; k < data.Length; k++)
            {
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
                result[k] = (byte)(data[k] ^ keyStreamByte);
            }

            return result;
        }

        /// <summary>
        /// 将字符串转换为字节数组
        /// </summary>
        /// <param name="str">输入字符串</param>
        /// <returns>字节数组</returns>
        private int[] ConvertStringToBytes(string str)
        {
            char[] chars = str.ToCharArray();
            int[] result = new int[chars.Length];
            for (int i = 0; i < chars.Length; i++)
            {
                result[i] = (int)chars[i];
            }
            return result;
        }

        /// <summary>
        /// 将整数转换为4字节的字节数组（大端序）
        /// </summary>
        /// <param name="value">要转换的整数</param>
        /// <returns>4字节的字节数组</returns>
        public static byte[] IntToBytes(int value)
        {
            return new byte[] {
                (byte)(value >> 24),
                (byte)(value >> 16),
                (byte)(value >> 8),
                (byte)value
            };
        }

        /// <summary>
        /// 将4字节的字节数组转换为整数（大端序）
        /// </summary>
        /// <param name="bytes">4字节的字节数组</param>
        /// <returns>转换后的整数</returns>
        public static int BytesToInt(byte[] bytes)
        {
            if (bytes.Length != 4)
            {
                throw new ArgumentException("字节数组长度必须为4");
            }
            return ((bytes[0] & 0xFF) << 24) |
                ((bytes[1] & 0xFF) << 16) |
                ((bytes[2] & 0xFF) << 8) |
                (bytes[3] & 0xFF);
        }
    }
}