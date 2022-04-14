package com.itao.proxy.pinyin;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import net.sourceforge.pinyin4j.multipinyin.MultiPinyinConfig;

import java.util.Arrays;

import static net.sourceforge.pinyin4j.format.HanyuPinyinCaseType.LOWERCASE;
import static net.sourceforge.pinyin4j.format.HanyuPinyinToneType.WITH_TONE_MARK;
import static net.sourceforge.pinyin4j.format.HanyuPinyinVCharType.WITH_U_UNICODE;

public class Pinyin {

    public static void main(String[] args) throws Exception {
        pinyin();
    }

    private static void pinyin() throws BadHanyuPinyinOutputFormatCombination {
        // 加载自定义的多音词库
        MultiPinyinConfig.multiPinyinPath = Pinyin.class.getResource("/pinyin/pinyin.txt").getFile();
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(LOWERCASE);
        format.setToneType(WITH_TONE_MARK);
        format.setVCharType(WITH_U_UNICODE);
        String pinyin = PinyinHelper
                .toHanYuPinyinString("将进酒", format, " ", false);
        System.out.println(pinyin);
        String[] s1 = PinyinHelper.toTongyongPinyinStringArray('绿');
        System.out.println(Arrays.toString(s1));
        String[] s9 = PinyinHelper.toHanyuPinyinStringArray('绿', format);
        System.out.println(Arrays.toString(s9));

        String[] s2 = PinyinHelper.toHanyuPinyinStringArray('爫', format);
        System.out.println(Arrays.toString(s2));

        String[] s10 = PinyinHelper.toTongyongPinyinStringArray('爫');
        System.out.println(Arrays.toString(s10));

        String[] s3 = PinyinHelper.toHanyuPinyinStringArray('差', format);
        System.out.println(Arrays.toString(s3));

        String[] s8 = PinyinHelper.toHanyuPinyinStringArray('差');
        System.out.println(Arrays.toString(s8));

        String[] s4 = PinyinHelper.toGwoyeuRomatzyhStringArray('差');
        System.out.println(Arrays.toString(s4));

        String[] s5 = PinyinHelper.toMPS2PinyinStringArray('差');
        System.out.println(Arrays.toString(s5));

        String[] s6 = PinyinHelper.toWadeGilesPinyinStringArray('差');
        System.out.println(Arrays.toString(s6));

        String[] s7 = PinyinHelper.toYalePinyinStringArray('差');
        System.out.println(Arrays.toString(s7));
    }
}
