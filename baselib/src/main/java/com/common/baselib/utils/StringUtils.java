package com.common.baselib.utils;

import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.EditText;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.functions.Function;

/**
 * 字符串工具类
 */
public class StringUtils {

    /**
     * 高于10000的数字转换为X.X万
     *
     * @param count
     * @return
     */
    public static String getCharmCount(String count, boolean plus) {

        if (count != null) {
            long charm = 0;
            try {
                charm = Long.parseLong(count);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (charm >= 10000) {
                DecimalFormat df = new DecimalFormat("#.#");
                if (plus) {
                    return "+" + df.format((double) charm / 10000) + "万";
                } else {
                    return df.format((double) charm / 10000) + "万";
                }

            } else {
                if (plus) {
                    return "+" + count;
                } else {
                    return count + "";
                }
            }
        }

        return "0";
    }

    /**
     * @param string
     * @return
     */
    public static boolean isNotNull(String string) {
        return !isNull(string);
    }

    /**
     * @param string
     * @return
     */
    public static boolean isNull(String string) {
        if (null == string || "".equals(string.trim()) || "null".equals(string)) {
            return true;
        }
        return false;
    }

    /**
     * 验证是否是手机号码
     *
     * @param phoneNum
     * @return
     */
    public static boolean isPhoneNum(String countryCode, String phoneNum) {
        if ("86".equals(countryCode)) {
//            Pattern p = Pattern.compile("^0?\\d{11}$");
//            Matcher m = p.matcher(phoneNum);
//            return m.matches();

//            String regex = "^1(3[0-9]|4[0-9]|5[0-9]|7[0-9]|8[0-9]|9[0-9])\\d{8}$";
            String regex = "^1\\d{10}$";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(phoneNum);

            return m.find();


        } else {
            return true;
        }
    }

    public static boolean isPhoneNum(String phoneNum) {
        return isPhoneNum("86", phoneNum);
    }

    /**
     * 验证邮箱格式是否正确
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     * 验证邮政编码是否正确
     *
     * @param postcode
     * @return
     */
    public static boolean isPostCode(String postcode) {
        String reg = "[0-9]{6}";
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(postcode);
        return m.matches();
    }


    public static String format2(Float value) {
        DecimalFormat df = new DecimalFormat("0.00");
        df.setRoundingMode(RoundingMode.HALF_UP);
        return df.format(value);
    }

    /**
     * 昵称是否含有特殊字符
     */
    public static boolean isUsernameAvailable(String nickname) {
        if (TextUtils.isEmpty(nickname)) {
            RxToast.showToast("请填写姓名");
            return false;
        }
        if (nickname.length() < 2) {
            RxToast.showToast("姓名不少于2个字");
            return false;
        }
        if (!nickname.matches("[\\u4e00-\\u9fa5_a-zA-Z0-9]{1,14}[\\?:•.]{0,1}[\\u4e00-\\u9fa5_a-zA-Z0-9]{1,13}+$")) {
            RxToast.showToast("你输入的姓名含特殊符号");
            return false;
        } else {
            return true;
        }
    }

    public static boolean checkPasswordAndTipError(String pwd) {
        if (TextUtils.isEmpty(pwd)) {
            RxToast.showToast("请输入密码");
            return false;
        }
        if (pwd.length() < 6 || pwd.length() > 14) {
            RxToast.showToast("请输入6~14位密码");
            return false;
        }

        Pattern urlPattern = Pattern.compile("^([a-z]|[A-Z]|[0-9])*$");
        Matcher urlMatcher = urlPattern.matcher(pwd);
        if (!urlMatcher.find()) {
            RxToast.showToast("密码要是数字和字母的组合哦！");
            return false;
        }

        return true;
    }


    /**
     * 将null转成“”
     */
    public static String nullToEmptyString(String str) {
        return str == null ? "" : str;
    }

    public static String[] str2UrlsInAction(String imagesStr) {
        if (!TextUtils.isEmpty(imagesStr)) {
            if (imagesStr.contains(",")) {
                String[] split = imagesStr.split(",");
                return split;
            } else {
                return new String[]{imagesStr};
            }
        }
        return null;
    }

    public static String getCityName(String cityName) {
        if (isNull(cityName)) {
            return "";
        }
        try {
            String[] citys = cityName.split(" ");
            if (citys != null && citys.length == 3) {
                return citys[1] + " " + citys[2];
            } else {
                return cityName;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 表情过滤器
     */
    public static class EmojiFilter implements InputFilter {
        Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]", Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            LogUtil.log(source + " " + start + " " + end + " " + dest + " " + dstart + "  " + dend);
            Matcher emojiMatcher = emoji.matcher(source);
            if (emojiMatcher.find()) {
                return "";
            }
            return null;
        }
    }

    /**
     * 过滤表情
     *
     * @param editText
     */
    public static void FilterEmoji(EditText editText) {
        editText.setFilters(new InputFilter[]{new EmojiFilter()});
    }

    /**
     * 获取上色的文字
     *
     * @param content
     * @param color
     * @return
     */
    public static String getColorText(String content, String color) {
        return "<font color='#" + color + "'>" + content + "</font>";
    }

    public static Spanned getHtmlStr(String... strs) {
        if (strs == null || strs.length == 0) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        for (String s : strs) {
            sb.append(s);
        }
        return Html.fromHtml(sb.toString());
    }

    /**
     * list转化为String by ,
     *
     * @param list
     * @return
     */
    public static String listToString(List<String> list) {
        if (CommonUtils.isListNull(list)) {
            return null;
        } else {
            StringBuffer sb = new StringBuffer();
            for (String str : list) {
                sb.append(str + ",");
            }
            return sb.deleteCharAt(sb.length() - 1).toString();
        }
    }


    public static <T> String listToString(List<T> list, Function<T, String> function) {
        if (CommonUtils.isListNull(list) || function == null) {
            return null;
        } else {
            StringBuffer sb = new StringBuffer();
            for (T t : list) {
                try {
                    sb.append(function.apply(t) + ",");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return sb.deleteCharAt(sb.length() - 1).toString();
        }
    }

    public static List<String> stringToList(String str) {
        if (isNull(str)) {
            return null;
        } else {
            List<String> list = new ArrayList<String>();
            String[] strs = str.split(",");
            StringBuffer sb = new StringBuffer();
            for (String s : strs) {
                list.add(s);
            }
            return list;
        }
    }

    public static boolean isLetter(char c) {
        int k = 0x80;
        return c / k == 0 ? true : false;
    }

    /**
     * 得到一个字符串的长度,显示的长度,一个汉字或日韩文长度为2,英文字符长度为1
     *
     * @return int 得到的字符串长度
     */
    public static int length(String s) {
        if (s == null)
            return 0;
        char[] c = s.toCharArray();
        int len = 0;
        for (int i = 0; i < c.length; i++) {
            len++;
            if (!isLetter(c[i])) {
                len++;
            }
        }
        return len;
    }


    /**
     * 得到一个字符串的长度,显示的长度,一个汉字或日韩文长度为1,英文字符长度为0.5
     */
    public static double getLength(String s) {
        double valueLength = 0;
        String chinese = "[\u4e00-\u9fa5]";
        // 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
        for (int i = 0; i < s.length(); i++) {
            // 获取一个字符
            String temp = s.substring(i, i + 1);
            // 判断是否为中文字符
            if (temp.matches(chinese)) {
                // 中文字符长度为1
                valueLength += 1;
            } else {
                // 其他字符长度为0.5
                valueLength += 0.5;
            }
        }
        //进位取整
        return Math.ceil(valueLength);
    }

    /**
     * 限制的长度
     *
     * @param s
     * @param limitLength
     * @return
     */
    public static int length(String s, int limitLength) {
        if (s == null)
            return 0;
        char[] c = s.toCharArray();

        int index = 0;

        int len = 0;
        for (int i = 0; i < c.length; i++) {
            index = i;
            len++;
            if (!isLetter(c[i])) {
                len++;
            }
            if (len == limitLength) {
                break;
            } else if (len > limitLength) {
                index = i - 1;
                break;
            }
        }
        return index + 1;
    }

    public static boolean checkStringlength(String s, int limitLength) {
        if (s == null)
            return false;
        char[] c = s.toCharArray();
        int len = 0;
        for (int i = 0; i < c.length; i++) {
            len++;
            if (!isLetter(c[i])) {
                len++;
            }
        }
        return limitLength >= len;
    }


    public static String getHtml(String content, String color) {
        return "<font color='" + color + "'>" + content + "</font>";

    }

    public static String addHttp(String link) {
        link = link.toLowerCase();
        if (link.indexOf("http://") != -1) {
            return link;
        } else if (link.indexOf("https://") != -1) {
            return link;
        } else {
            link = "http://" + link;
        }
        return link;
    }

    public static List<String> strToList(String str) {
        if (StringUtils.isNotNull(str)) {
            if (str.contains(",")) {
                String[] arrStr = str.split(",");
                return Arrays.asList(arrStr);
            } else {
                List<String> list = new ArrayList<>();
                list.add(str);
                return list;
            }
        }
        return null;
    }

    public static String urlRegularExpression = "(((http|ftp|https)://)|www)(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\\&%_\\./-~-]*)?";


    /**
     * 解析 输入的文字里有无url
     *
     * @param rawUrl
     * @return
     */
    public static String getReloveUrl(String rawUrl) {
        String includeUrl = null;
        if (!rawUrl.contains("://")) {
            rawUrl = "http://" + rawUrl;
        }
        Pattern urlPattern = Pattern.compile(urlRegularExpression, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = urlPattern.matcher(rawUrl);
        while (urlMatcher.find()) {
            includeUrl = rawUrl.subSequence(urlMatcher.start(), urlMatcher.end()).toString();
            /**
             * 最后有表情 [] 过滤掉
             */
            if (StringUtils.isNotNull(includeUrl) && includeUrl.length() > 1) {
                String lastChar = includeUrl.charAt(includeUrl.length() - 1) + "";
                if ("[".equals(lastChar)) {
                    includeUrl = includeUrl.substring(0, includeUrl.length() - 1);
                }
            }
        }
        return includeUrl;
    }

    public static boolean isNumBeyond2(String s) {
        if (s.contains(".")) {
            if (s.startsWith(".") || s.endsWith(".")) {
                return false;
            } else {
                int position = s.length() - (s.indexOf(".") + 1);
                if (position > 2) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String setNullToStr(String s) {
        if (StringUtils.isNull(s)) {
            return "";
        }
        return s;
    }

    public static double setDouble2(double num) {
        DecimalFormat df = new DecimalFormat("#.##");
        return Double.parseDouble(df.format(num));
    }

    public static float setFloat2(float num) {
        DecimalFormat df = new DecimalFormat("#.#");
        return Float.parseFloat(df.format(num));
    }


    public static String doubleToLong(String DoubleStr) {
        if (StringUtils.isNull(DoubleStr)) {
            return "";
        }
        if (DoubleStr.contains(".")) {
            try {
                double mon = Double.parseDouble(DoubleStr);
                long ln = (long) mon;
                if (mon == ln) {
                    return String.valueOf(ln);
                } else {
                    return DoubleStr;
                }
            } catch (Exception e) {
                return DoubleStr;
            }
        } else {
            return DoubleStr;
        }
    }


    /**
     * 格式化时间
     *
     * @param time
     * @return
     */
    public static String formatTime(long time) {
        long second = time / 1000;

        long min = second / 60;
        long sec = second % 60;

        if (min == 0) {
            if (sec < 10) {
                return "00:0" + sec;
            } else {
                return "00:" + sec;
            }
        } else if (min > 0 && min < 10) {
            if (sec < 10) {
                return "0" + min + ":0" + sec;
            } else {
                return min + ":" + sec;
            }
        } else {
            if (sec < 10) {
                return min + ":0" + sec;
            } else {
                return min + ":" + sec;
            }
        }
    }


    public static String big(double d) {
        NumberFormat nf = NumberFormat.getInstance();
        // 是否以逗号隔开, 默认true以逗号隔开,如[123,456,789.128]
        nf.setGroupingUsed(false);
        // 结果未做任何处理
        return nf.format(d);
    }
}
