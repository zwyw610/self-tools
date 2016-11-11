package com.duke.self;

import org.apache.commons.lang.StringUtils;

/**
 * Created by Dr.WangSQ on 2016/11/10.
 */
public class QuickSort {


    /**
     * @param attr 需要排序的数组
     * @param start 数组起始位置
     * @param end 数组结束为止
     * @param baseIndex 基准值下标
     *
     *
     */
    public void quickSort(int[] attr, int start, int end){
        int left = start;
        int right = end;

        if(left > right) {
            return;
        }
        int baseValue = attr[start];

        int middleValue = 0;
        while (left < right){
            //第一步，从最右侧向左扫描，寻找到第一个小于基准值的数字，然后停下
            while (right > left && attr[right] >= baseValue){
                right --;
            }
            //step2:from left to right, find the value which larger than baseValue
            while (right > left && attr[left] <= baseValue){
                left ++;
            }
            //如果left==right，那就是自己跟自己交换，没有意义
            if(left < right){
                //exchange left and right index
                middleValue = attr[right];
                attr[right] = attr[left];
                attr[left] = middleValue;
            }
        }
        //swap baseIndex
        middleValue = attr[right];
        attr[right] = baseValue;
        attr[start] = middleValue;
        quickSort(attr, start, right-1);
        quickSort(attr, right+1,end);
    }

    public static void main(String[] args){
        int[] attr = new int[]{5,2,5,3,6,4,8,2};
        QuickSort qs = new QuickSort();
        qs.quickSort(attr, 0, attr.length-1);
        System.out.println(org.apache.commons.lang3.StringUtils.join(attr, ","));

    }

}
