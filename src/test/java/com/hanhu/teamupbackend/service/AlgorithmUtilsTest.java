package com.hanhu.teamupbackend.service;

import com.hanhu.teamupbackend.utils.AlgorithmUtils;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

/**
 * 算法工具类测试
 */
public class AlgorithmUtilsTest {


    @Test
    void test() {
        String str1 = "我是狗";
        String str2 = "我不是狗";
        String str3 = "我是猫";
        String str4 = "我是猫不是狗";
        // 1
        int score1 = AlgorithmUtils.minDistance(str1, str2);
        // 1
        int score2 = AlgorithmUtils.minDistance(str1, str3);
        // 3
        int score3 = AlgorithmUtils.minDistance(str1,str4);
        System.out.println(score1);
        System.out.println(score2);
        System.out.println(score3);
    }

    @Test
    void testCompareTags() {
        List<String> tagList1 = Arrays.asList("Java", "大一", "男");
        List<String> tagList2 = Arrays.asList("Java", "大一", "女");
        List<String> tagList3 = Arrays.asList("Python", "大二", "女");
        // 1
        int score1 = AlgorithmUtils.minDistance(tagList1, tagList2);
        // 3
        int score2 = AlgorithmUtils.minDistance(tagList1, tagList3);
        System.out.println(score1);
        System.out.println(score2);
    }

}
