package com.itao.jdk.util;

import java.util.BitSet;

public class TestBitSet {

    public static void main(String[] args) {
        // 如果是无参构造 默认是 64 位
        BitSet bitSet = new BitSet();
        bitSet.set(1);  // 表示 将第 1  位的数字置为 1 即 。。。0000 0010 （省略前面的 56个0）
        bitSet.set(2);  // 表示 将第 2  位的数字置为 1 即 。。。0000 0110 （省略前面的 56个0）
        bitSet.set(4);  // 表示 将第 4  位的数字置为 1 即 。。。0001 0110 （省略前面的 56个0）
        bitSet.set(64); // 表示 将第 64 位的数字置为 1 即 。。。0000 0001 。。。0001 0110（省略前面的 56个0 和中间 56个0）
        // 以为默认是64位 (0-63) 当设置64时 需要扩容 即2个64位 并将 第二个64位中的第0位置为1
        // size 为总位数， 长度为 最高位的位数（即设置的最大值 +1）
        System.out.println("size: " + bitSet.size() + ", length: " + bitSet.length());
        System.out.println(bitSet.get(1));
        System.out.println(bitSet);
        // 最终结果如下（省略前面56个0）：
        // 。。。0000 0001 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0001 0110
    }
}
