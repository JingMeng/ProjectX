package am.project.x.utils;

import androidx.annotation.NonNull;
import android.util.Base64;

import java.util.regex.Pattern;


/**
 * 瀛楃涓插伐鍏? *
 * @author Alex
 */
@SuppressWarnings("unused")
public class StringUtils {

    /**
     * 瀛楃涓叉鏌?     *
     * @param str 寰呮鏌ュ瓧绗︿覆
     * @return 鍒ゆ柇缁撴灉
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * 瀛楃涓插尮閰?     *
     * @param src 婧?     * @param des 鍖归厤鏍峰紡
     * @return 鏄惁鍖归厤
     */
    public static boolean isMatching(@NonNull String src, @NonNull String des) {
        String regex = des.replace("*", "\\w*").replace("?", "\\w{1}");
        return Pattern.compile(regex).matcher(src).matches();
    }

    /**
     * 鍏嬮殕
     *
     * @param target 婧?     * @return 鍏嬮殕瀛楃涓?     */
    public static String clone(String target) {
        if (target == null)
            return null;
        return target + "";
    }

    /**
     * 鑾峰彇鍝堝笇鍊?     *
     * @param target 婧?     * @return 鍝堝笇鍊?     */
    public static int hashCode(String target) {
        if (target == null)
            return 0;
        return target.hashCode();
    }

    /**
     * 瀛楃涓叉帓搴?     *
     * @param str1 瀛楃涓?
     * @param str2 瀛楃涓?
     * @return 鎺掑簭鍊?     */
    public static int compareTo(String str1, String str2) {
        if (str1 == null || str2 == null)
            return 0;
        final int lexicographically = str1.compareTo(str2);
        if (lexicographically < 0) {
            return -1;
        } else if (lexicographically > 0) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * 鍒ゆ柇瀛楃涓叉槸鍚︿负鏁板瓧
     *
     * @param str 瀛楃涓?     * @return 鏄惁涓烘暟瀛?     */
    public static boolean isNumeric(String str) {
        return Pattern.compile("[0-9]*").matcher(str).matches();
    }

    /**
     * 灏嗙┖瀛楃涓茶浆涓?"
     *
     * @param str 寰呬慨鏀瑰瓧绗︿覆
     * @return 淇敼缁撴灉
     */
    public static String setNullToEmpty(String str) {
        return str == null ? "" : str;
    }

    /**
     * 灏嗙┖瀛楃涓茶浆涓?null"
     *
     * @param str 寰呬慨鏀瑰瓧绗︿覆
     * @return 淇敼缁撴灉
     */
    public static String setNullToString(String str) {
        return str == null ? "null" : str;
    }


    /**
     * BASE64 缂栫爜
     *
     * @param str         寰呯紪鐮佸瓧绗︿覆
     * @param encodingIn  瑙ｇ爜缂栫爜鏍煎紡
     * @param encodingOut 杈撳嚭缂栫爜鏍煎紡
     * @return 缂栫爜瀛楃涓?     */
    public static String encodingBASE64(String str, String encodingIn, String encodingOut) {
        try {
            return new String(Base64.encode(str.getBytes(encodingIn), Base64.DEFAULT), encodingOut);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * BASE64 瑙ｇ爜
     *
     * @param str         寰呯紪鐮佸瓧绗︿覆
     * @param encodingIn  瑙ｇ爜缂栫爜鏍煎紡
     * @param encodingOut 杈撳嚭缂栫爜鏍煎紡
     * @return 缂栫爜瀛楃涓?     */
    public static String decodingBASE64(String str, String encodingIn, String encodingOut) {
        try {
            return new String(Base64.decode(str.getBytes(encodingIn), Base64.DEFAULT), encodingOut);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 鑾峰彇闀垮
     *
     * @param size 闀垮
     */
    public static void getSizeByUrl(String url, int[] size) {
        if (url == null || url.length() <= 0 || size == null || size.length < 2)
            return;
        int width = 0;
        int height = 0;
        String[] wxh = url.split("_|\\.jpg|\\.jpeg|\\.png|\\.bmp");
        if (wxh.length == 2) {
            String[] wh = wxh[1].split("x");
            if (wh.length == 2) {
                try {
                    width = Integer.parseInt(wh[0]);
                    height = Integer.parseInt(wh[1]);
                } catch (NumberFormatException e) {
                    width = 0;
                    height = 0;
                }
            }
        }
        size[0] = width;
        size[1] = height;
    }

    public static boolean isMobilePhoneNum(String phoneNum) {
        return phoneNum != null && phoneNum.length() != 0 && phoneNum.matches("[1][3458]\\d{9}");
    }

    /**
     * 鎵嬫満鍙风爜锛岃韩浠借瘉璇佺瓑闅愮鍐呭鍔?
     *
     * @param str       瑕佸姞*鐨勫瓧绗︿覆
     * @param frontLen  *鍓嶉潰棰勭暀闀垮害
     * @param behindLen *鍚庨潰棰勭暀闀垮害
     * @return 鍔?鍚庣殑瀛楃涓?     */
    public static String getPrivateStr(String str, int frontLen, int behindLen) {
        int strLen = str.length();
        if (frontLen >= strLen || behindLen >= strLen || frontLen + behindLen >= strLen) {
            return str;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(str.substring(0, frontLen));
        int starCount = strLen - frontLen - behindLen;
        for (int i = 0; i < starCount; i++) {
            builder.append("\u002A");
        }
        return builder.append(str.substring(strLen - behindLen, strLen)).toString();
    }

    /**
     * 鍘绘帀瀛楃涓插墠鍚庢墍鏈夌殑绌烘牸
     *
     * @param str 瀛楃涓?     * @return 瀛楃涓?     */
    public static String trimStartAndEnd(String str) {
        while (str.startsWith(" ")) {
            str = str.substring(1, str.length()).trim();
        }
        while (str.endsWith(" ")) {
            str = str.substring(0, str.length() - 1).trim();
        }
        return str;
    }

    /**
     * 鍒ゆ柇鏄惁鏄竴涓狪P
     *
     * @param IP 瀛楃涓?     * @return 鏄惁鏄竴涓狪P
     */
    public static boolean isIp(String IP) {
        boolean b = false;
        IP = trimStartAndEnd(IP);
        if (IP.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
            String s[] = IP.split("\\.");
            if (Integer.parseInt(s[0]) < 255)
                if (Integer.parseInt(s[1]) < 255)
                    if (Integer.parseInt(s[2]) < 255)
                        if (Integer.parseInt(s[3]) < 255)
                            b = true;
        }
        return b;
    }

    /**
     * 鍒ゆ柇鏄惁涓枃
     * GENERAL_PUNCTUATION 鍒ゆ柇涓枃鐨勨€滃彿
     * CJK_SYMBOLS_AND_PUNCTUATION 鍒ゆ柇涓枃鐨勩€傚彿
     * HALFWIDTH_AND_FULLWIDTH_FORMS 鍒ゆ柇涓枃鐨勶紝鍙?     *
     * @param c 瀛楃
     * @return 鏄惁涓枃
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION;
    }

    /**
     * 鍒ゆ柇瀛楃涓叉槸鍚﹀寘鍚腑鏂?     *
     * @param str 瀛楃涓?     * @return 瀛楃涓叉槸鍚﹀寘鍚腑鏂?     */
    public static boolean containsChinese(String str) {
        if (str == null)
            return false;
        for (char c : str.toCharArray()) {
            if (StringUtils.isChinese(c))
                return true;
        }
        return false;
    }

    /**
     * 鑾峰彇鏂囧瓧瀛楄妭鏁?2瀛楄妭浠ヤ笂鐨勫瓧绗︿竴寰嬫寜2瀛楄妭缁熻)
     *
     * @param s 瀛楃涓?     * @return 瀛楄妭鏁?     */
    public static int getWordCount(String s) {
        if (s == null)
            return 0;
        return s.replaceAll("[^\\x00-\\xff]", "\u002A\u002A").length();
    }

    /**
     * 妫€娴嬫槸鍚︽湁emoji瀛楃
     *
     * @param source 瀛楃涓?     * @return 涓€鏃﹀惈鏈夊氨鎶涘嚭
     */

    public static boolean containsEmoji(String source) {
        if (isNullOrEmpty(source)) {
            return false;
        }
        int len = source.length();
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (!isNotEmojiCharacter(codePoint)) {
                return true;
            }
        }
        return false;
    }


    private static boolean isNotEmojiCharacter(char codePoint) {
        return codePoint == 0x0 ||
                codePoint == 0x9 ||
                codePoint == 0xA ||
                codePoint == 0xD ||
                (codePoint >= 0x20 && codePoint <= 0xD7FF) ||
                (codePoint >= 0xE000 && codePoint <= 0xFFFD) ||
                codePoint >= 0x10000;
    }

    /**
     * 鑾峰彇缂栫爜绫诲瀷锛堥潪绮惧噯锛?     *
     * @param first3bytes 鍓嶄笁涓瓧鑺?     * @return 缂栫爜绫诲瀷
     */
    public static String getCharset(byte[] first3bytes) {
        String charset = "GBK";
        if (first3bytes == null || first3bytes.length < 3) {
            return charset;
        }
        if (first3bytes[0] == (byte) 0xEF && first3bytes[1] == (byte) 0xBB
                && first3bytes[2] == (byte) 0xBF) {
            // utf-8
            charset = "utf-8";
        } else if (first3bytes[0] == (byte) 0xFF && first3bytes[1] == (byte) 0xFE) {
            charset = "unicode";
        } else if (first3bytes[0] == (byte) 0xFE && first3bytes[1] == (byte) 0xFF) {
            charset = "utf-16be";
        } else if (first3bytes[0] == (byte) 0xFF && first3bytes[1] == (byte) 0xFF) {
            charset = "utf-16le";
        } else {
            // 娣诲姞鏇村鍒ゆ柇鏂规硶
            charset = "GBK";
        }
        return charset;
    }
}

