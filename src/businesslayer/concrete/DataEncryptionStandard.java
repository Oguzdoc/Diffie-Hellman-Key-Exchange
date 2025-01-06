package businesslayer.concrete;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DataEncryptionStandard
{
    private final int[] INIT_PERMUTATION = 
    {
        57, 49, 41, 33, 25, 17, 9, 1,
        59, 51, 43, 35, 27, 19, 11, 3,
        61, 53, 45, 37, 29, 21, 13, 5,
        63, 55, 47, 39, 31, 23, 15, 7,
        56, 48, 40, 32, 24, 16, 8, 0,
        58, 50, 42, 34, 26, 18, 10, 2,
        60, 52, 44, 36, 28, 20, 12, 4,
        62, 54, 46, 38, 30, 22, 14, 6
    };

    private final int[] FINAL_PERMUTATION = 
    {
        39, 7, 47, 15, 55, 23, 63, 31,
        38, 6, 46, 14, 54, 22, 62, 30,
        37, 5, 45, 13, 53, 21, 61, 29,
        36, 4, 44, 12, 52, 20, 60, 28,
        35, 3, 43, 11, 51, 19, 59, 27,
        34, 2, 42, 10, 50, 18, 58, 26,
        33, 1, 41, 9, 49, 17, 57, 25,
        32, 0, 40, 8, 48, 16, 56, 24
    };

    private final int[] PERMUTED_CHOICE = 
    {
        48, 40, 32, 24, 16, 8, 0, 49,
        41, 33, 25, 17, 9, 1, 50, 42,
        34, 26, 18, 10, 2, 51, 43, 35,
        27, 19, 11, 3, 52, 44, 36, 28,
        20, 12, 4, 53, 45, 37, 29, 21,
        13, 5, 54, 46, 38, 30, 22, 14,
        6, 55, 47, 39, 31, 23, 15, 7
    };
    
    private final int[] COMPRESSION_PERMUTATION_TABLE = 
    {
        13, 16, 10, 23, 0, 4,
        2, 27, 14, 5, 20, 9,
        22, 18, 11, 3, 25, 7,
        15, 6, 26, 19, 12, 1,
        40, 51, 30, 36, 46, 54,
        29, 39, 50, 44, 32, 47,
        43, 48, 38, 55, 33, 52,
        45, 41, 49, 35, 28, 31
    };

    private final int[] EXPANSION_PERM = 
    {
        31, 0, 1, 2, 3, 4,
        3, 4, 5, 6, 7, 8,
        7, 8, 9, 10, 11, 12,
        11, 12, 13, 14, 15, 16,
        15, 16, 17, 18, 19, 20,
        19, 20, 21, 22, 23, 24,
        23, 24, 25, 26, 27, 28,
        27, 28, 29, 30, 31, 0
    };

    private final int[] P_BOX_TABLE = 
    {
        15, 6, 19, 20, 28, 11, 27, 16,
        0, 14, 22, 25, 4, 17, 30, 9,
        1, 7, 23, 13, 31, 26, 2, 8,
        18, 12, 29, 5, 21, 10, 3, 24
    };

    private final int[][][] SBOX = 
    {
        {
            {14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7},
            {0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8},
            {4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0},
            {15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13}
        },
        {
            {15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10},
            {3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5},
            {0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15},
            {13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9}
        },
        {
            {10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8},
            {13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1},
            {13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7},
            {1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12}
        },
        {
            {7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15},
            {13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9},
            {10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4},
            {3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14}
        },
        {
            {2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9},
            {14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6},
            {4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14},
            {11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3}
        },
        {
            {12, 1, 10, 15, 9, 2, 6, 8, 0, 5, 14, 4, 3, 11, 7, 13},
            {10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8},
            {9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6},
            {4, 3, 2, 12, 1, 10, 15, 9, 8, 5, 14, 11, 6, 7, 0, 13}
        },
        {
            {4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1},
            {13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6},
            {1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2},
            {6, 1, 3, 10, 13, 8, 7, 4, 11, 14, 9, 5, 0, 15, 2, 12}
        },
        {
            {13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7},
            {1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2},
            {7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8},
            {2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 3, 5, 0, 6, 11}
        }
    };

    /**
     * Permutes bits based on the specified permutation table.
     *
     * @param bits   the list of bits to permute
     * @param table  the permutation table used to rearrange bits
     * @return       a list of permuted bits
     */
    private List<Integer> permuteBits(List<Integer> bits, int[] table) 
    {
        List<Integer> permutedBits = new ArrayList<>();
        for (int position : table) 
            permutedBits.add(bits.get(position));
        return permutedBits;
    }

    /**
     * Computes the XOR result of two lists of bits.
     *
     * @param first   the first list of bits
     * @param second  the second list of bits
     * @return        a list of bits resulting from XOR operation
     */
    private List<Integer> xorBits(List<Integer> first, List<Integer> second) 
    {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < first.size(); i++) 
            result.add(first.get(i) ^ second.get(i));
        return result;
    }

    /**
     * Performs substitution using S-boxes to transform 48-bit input into 32-bit output.
     *
     * @param bits  the list of 48 bits to substitute
     * @return      a list of 32 bits after S-box substitution
     */
    private List<Integer> sboxSubstitution(List<Integer> bits) 
    {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < 48; i += 6) 
        {
            List<Integer> block = bits.subList(i, i + 6);
            int row = (block.get(0) << 1) | block.get(5);
            int col = (block.get(1) << 3) | (block.get(2) << 2) | (block.get(3) << 1) | block.get(4);
            int val = SBOX[i / 6][row][col];

            for (int j = 3; j >= 0; j--) 
                result.add((val >> j) & 1);
        }
        return result;
    }

    /**
     * Performs a single DES round using the left and right halves of bits and a subkey.
     * 
     * @param  left   the left half of the 64-bit block
     * @param  right  the right half of the 64-bit block
     * @param  subkey the 48-bit subkey for this DES round
     * @return        the updated left and right halves after the round
     */
    private List<List<Integer>> performDesRound(List<Integer> left, List<Integer> right, List<Integer> subkey)
    {
        List<Integer> expandedRight = permuteBits(right, EXPANSION_PERM);
        List<Integer> xorRes = xorBits(expandedRight, subkey);
        List<Integer> sboxOutput = sboxSubstitution(xorRes);
        List<Integer> permutedOutput = permuteBits(sboxOutput, P_BOX_TABLE);
        List<List<Integer>> nextRound = new ArrayList<>();
        nextRound.add(right);
        nextRound.add(xorBits(left, permutedOutput));
        return nextRound;
    }

    /**
     * Generates 16 subkeys from the main key, each used in a DES round.
     * 
     * @param  keyBits the 64-bit main key in bits
     * @return         the 16 subkeys generated from the main key
     */
    private List<List<Integer>> generateSubkeys(List<Integer> keyBits) 
    {
        List<Integer> permutedKey = permuteBits(keyBits,PERMUTED_CHOICE);
        List<Integer> leftKey = new ArrayList<>(permutedKey.subList(0, 28));
        List<Integer> rightKey = new ArrayList<>(permutedKey.subList(28, 56));
        
        int[] shifts = {1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1};
        List<List<Integer>> keys = new ArrayList<>();

        for (int shift : shifts) 
        {
            Collections.rotate(leftKey, -shift);
            Collections.rotate(rightKey, -shift);
            List<Integer> combined = new ArrayList<>(leftKey);
            combined.addAll(rightKey);
            keys.add(permuteBits(combined, COMPRESSION_PERMUTATION_TABLE));
        }
        
        return keys;
    }

    /**
     * Encrypts a 64-bit block using DES and a 64-bit key.
     * 
     * @param  bits    the 64-bit block of data to encrypt
     * @param  keyBits the 64-bit encryption key
     * @return         the encrypted 64-bit block
     */
    private List<Integer> encryptBlock(List<Integer> bits, List<Integer> keyBits) 
    {
        List<Integer> permuted = permuteBits(bits, INIT_PERMUTATION);
        List<Integer> left = new ArrayList<>(permuted.subList(0, 32));
        List<Integer> right = new ArrayList<>(permuted.subList(32, 64));
        
        List<List<Integer>> subkeys = generateSubkeys(keyBits);

        for (List<Integer> subkey : subkeys) 
        {
            List<List<Integer>> round = performDesRound(left, right, subkey);
            left = round.get(0);
            right = round.get(1);
        }

        List<Integer> combined = new ArrayList<>(right);
        combined.addAll(left);
            return permuteBits(combined, FINAL_PERMUTATION);
    }

    /**
     * Decrypts a 64-bit block using DES and a 64-bit key by reversing the encryption steps.
     * 
     * @param  blockBits the 64-bit block of data to decrypt
     * @param  keyBits   the 64-bit decryption key
     * @return           the decrypted 64-bit block
     */
    public List<Integer> decryptBlock(List<Integer> blockBits, List<Integer> keyBits) 
    {
        List<Integer> paddedBlockBits = applyPadding(blockBits);
        List<Integer> permutedBlock = permuteBits(paddedBlockBits, INIT_PERMUTATION);

        List<Integer> leftBits = new ArrayList<>(permutedBlock.subList(0, 32));
        List<Integer> rightBits = new ArrayList<>(permutedBlock.subList(32, 64));

        List<List<Integer>> subkeys = generateSubkeys(keyBits);
        // Reverse subkeys manually
        for (int i = subkeys.size() - 1; i >= 0; i--) 
        {
            List<List<Integer>> temp = performDesRound(leftBits, rightBits, subkeys.get(i));
            leftBits = temp.get(0);
            rightBits = temp.get(1);
        }

        List<Integer> combinedBits = new ArrayList<>(rightBits);
        combinedBits.addAll(leftBits);
        return permuteBits(combinedBits, FINAL_PERMUTATION);
    }
    
    /**
     * Applies padding to a 64-bit block if needed.
     * 
     * @param  bits the list of bits to be padded
     * @return      the padded list of bits
     */

    private static List<Integer> applyPadding(List<Integer> bits) 
    {
        int padSize = 64 - (bits.size() % 64);
        List<Integer> padding = new ArrayList<>(Collections.nCopies(padSize - 8, 0));

        String padBinary = Integer.toBinaryString(padSize);

        for (int i = 8 - padBinary.length(); i > 0; i--) 
            padding.add(0);
        
        for (char bit : padBinary.toCharArray()) 
            padding.add(Character.getNumericValue(bit));

        List<Integer> paddedBits = new ArrayList<>(bits);
        paddedBits.addAll(padding);
        return paddedBits;
    }

    /**
    * Removes the padding from the given list of bits.
    * The padding length is stored in the last 8 bits of the list. 
    * This method extracts that padding length and trims the corresponding number of bits from the end.
    * 
    * @param bits The list of bits to remove padding from.
    * @return A new list of bits without the padding.
    */
    private static List<Integer> removePadding(List<Integer> bits) 
    {
        int paddingLength = Integer.parseInt(
                bits.subList(bits.size() - 8, bits.size()).stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining()), 2);

        return bits.subList(0, bits.size() - paddingLength);
    }

    /**
    * Converts a string of text into a list of bits.
    * Each character is converted to an 8-bit representation (binary).
    * Padding is applied to the bit list to ensure it's a multiple of 64 bits in length.
    * 
    * @param text The string of text to convert.
    * @return A list of bits representing the text, with padding applied.
    */
    private static List<Integer> convertTextToBits(String text) 
    {
        List<Integer> bits = new ArrayList<>();
        for (char character : text.toCharArray()) 
        {
            String binaryString = Integer.toBinaryString(character);

            for (int j = 8 - binaryString.length(); j > 0; j--) 
                bits.add(0);
            
            for (char bit : binaryString.toCharArray()) 
                bits.add(Character.getNumericValue(bit));
            
        }
        return applyPadding(bits);
    }
    
    /**
     * Converts a list of bits back into a text string.
     * First, the padding is removed from the list of bits, then the bits are grouped into 8-bit chunks, 
     * and each chunk is converted back into the corresponding character.
     * 
     * @param bits The list of bits to convert.
     * @return The resulting text string.
     */
    private static String convertBitsToText(List<Integer> bits) 
    {
        List<Integer> unpaddedBits = removePadding(bits);
        StringBuilder text = new StringBuilder();

        for (int i = 0; i < unpaddedBits.size(); i += 8) 
        {
            String byteString = unpaddedBits.subList(i, i + 8).stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining());
            text.append((char) Integer.parseInt(byteString, 2));
        }
        return text.toString();
    }
    
    /**
    * Encrypts a message using a given key. The message is split into blocks of 8 characters, 
    * which are then converted into bits. Each block is encrypted using the provided key bits.
    * 
    * @param text The text message to encrypt.
    * @param key The key used for encryption.
    * @return A list of encrypted bits representing the message.
    * @throws IOException If an error occurs during the encryption process.
    */
    private List<Integer> encryptMessage(String text, String key) throws IOException 
    {
        List<Integer> keyBits = convertTextToBits(key);
        List<Integer> encryptedBits = new ArrayList<>();

        for (int i = 0; i < text.length(); i += 8) 
        {
            String block = text.substring(i, Math.min(i + 8, text.length()));
            List<Integer> blockBits = convertTextToBits(block);
            encryptedBits.addAll(encryptBlock(blockBits, keyBits));
        }
        return encryptedBits;
    }

    /**
     * Decrypts an encrypted message using the provided key.
     * The encrypted message is processed in blocks of 64 bits, and each block is decrypted using the key.
     * 
     * @param encryptedBits The list of encrypted bits to decrypt.
     * @param key The key used for decryption.
     * @return The decrypted message as a string.
     * @throws IOException If an error occurs during the decryption process.
     */
    private String decryptMessage(List<Integer> encryptedBits, String key) throws IOException 
    {
        List<Integer> keyBits = convertTextToBits(key);
        List<Integer> decryptedBits = new ArrayList<>();

        for (int i = 0; i < encryptedBits.size(); i += 64) 
        {
            List<Integer> blockBits = encryptedBits.subList(i, i + 64);
            decryptedBits.addAll(decryptBlock(blockBits, keyBits));
        }
        return convertBitsToText(decryptedBits);
    }
    
    public String encryptMessageToString(String text, String key) throws IOException {
        List<Integer> encryptedBits = encryptMessage(text, key);

        // Tam 64 bit bloğa doldur
        while (encryptedBits.size() % 64 != 0) {
            encryptedBits.add(0);
        }

        return encryptedBits.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(""));
    }
    
    public String decryptMessageFromString(String encryptedMessage, String key) throws IOException {
        // Convert binary string to List<Integer>
        List<Integer> encryptedBits = encryptedMessage.chars()
                .mapToObj(c -> c == '1' ? 1 : 0)
                .collect(Collectors.toList());

        // Tam 64 bit bloğa doldur
        while (encryptedBits.size() % 64 != 0) {
            encryptedBits.add(0);
        }

        return decryptMessage(encryptedBits, key);
    }
}   