package com.duke.self;

import org.apache.commons.lang3.StringUtils;

/**
 *  Created by Dr.WangSQ on 2016/11/29.
 *  求最大不重复的字字符串算法
 *  已知一个字符串，只含有小写字母，求这个字符串的每个字符都不相同的最长子串的长度。
 *   比如：
 *   abcd 结果是4
 *   abcab 结果是3
 *
 *    思路：
 *    用一个26个元素的整形数组表示一个字符串中是否含有某个字符。a~b分别映射到数组元素0~25。
 *    用两个指针分别指向字符串的第一个和第二个元素，用第二个指针从左往右扫描字符串。每扫描一个字符，根据数组中对应的值来判断这个字符是否已经出现。
 *    如果没出现，则继续扫描，同时更新已知不相同子串的最大长度。
 *    如果出现过，那么向右移动第一个指针，直到刚才重复的那个字符不再重复，然后继续上面的工作。
 */
public class MaxSubNonRepeatStringAlgorithm {

    public static String getMaxSubNonRepeateStringAlgorithm(String pStr){
        if(StringUtils.isEmpty(pStr))
            return null;
        int preIndex = 0;
        int endIndex = 0;
        int max = 0;
        String maxSubStr = "";
        int length = pStr.length();
        while (endIndex < length){
            char subchar = pStr.charAt(endIndex);
            int repeateIndex = getRepeateIndex(pStr, preIndex, endIndex, subchar);
            //repeateIndex返回的是重复字符串的位置，所以要跳过这个重复的位置
            if(repeateIndex != -1){
                preIndex = repeateIndex+1;
            }
            //判断当前字字符串长度是否大于已有的最大字字符串长度
            if((endIndex-preIndex) > max){
                max = endIndex - preIndex;
                maxSubStr = pStr.substring(preIndex, endIndex+1);
            }
            endIndex ++;
        }
        return maxSubStr;
    }

    /**
     * 获取重复字符的位置，若没有，则返回-1
     */
    public static int getRepeateIndex(String substr, int preIndex, int endIndex, char subchar){
        while (preIndex < endIndex){
            char idex = substr.charAt(preIndex);
            if(idex == subchar)
                return preIndex;
            preIndex ++;
        }
        return -1;
    }


    public static void main(String[] args){
        System.out.println(getMaxSubNonRepeateStringAlgorithm("acbadecafdba"));
    }

}
