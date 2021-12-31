package com.itao.emoji;

import cn.hutool.extra.emoji.EmojiUtil;

public class Emoji {
    public static void main(String[] args) {
        com.vdurmont.emoji.Emoji smile = EmojiUtil.get("smile");
        System.out.println(smile.getUnicode());
    }
}
