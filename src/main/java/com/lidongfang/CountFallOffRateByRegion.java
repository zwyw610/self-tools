package com.lidongfang;

import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Dr.WangSQ on 2016/11/2.
 *
 * 统计脱落率
 */
public class CountFallOffRateByRegion {




    public static void main(String[] args) throws IOException {
        //北一区
        String region = "92,232,89,268,66,61,122,42,128,88,98,9,377,127,400,78,402,56,118";
        //东北区
        //String region = "240,337,282,160,159,265,290,308,76,131,119,157,237,236,233,158,161,239,235,246,403,238,245";
        //东区
        //String region = "261,293,104,164,109,170,110,103,388,373,250,114,382,357,364,253,264,356,133,145,167,149,106,390,105,156,353,140,111,258,381,147,398";
        //南区
        //String region = "277,130,338,168,134,288,24,117,120,136,137,251,397,291,319,37,188,142,249,198,242,202,199,380,401,243,241,192";
        //华北区
        //String region = "263,351,153,273,392,355,262,107,155,334,280,121,82,305,404,94,378,48,386,7,376,385,384";
        //西北区
        //String region = "275,269,254,259,256,234,267,230,169,375,406,231,391,244";
        //西南区
        //String region = "371,185,368,183,184,176,172,181,182,144,174,372,178,173,346,150,276,196,358,203,272,129,116,17,68,166,194";
        //中一区
        //String region = "223,208,220,226,207,15,206,214,19,26,212,224,213,225,25,221,222,216,217,227,53,54,58,209,211,210,218,215,219";
        //中二区
        //String region = "228,162,171,14,60,329,44,302,297,18,328,112,139,165,252,324,396,36,395,393,394";
        String[] regionAttr = region.split(",");
        List<String> regionList = new ArrayList<String>();
        for(String r : regionAttr){
            regionList.add(r);
        }

        String path = "D://fall_off.txt";
        BufferedReader br = new BufferedReader(new FileReader(path));
        String line = null;
        Map<String, Integer> retMap = new HashMap<String, Integer>();
        int regionCount = 0;
        while ((line = br.readLine()) != null){
            if(StringUtils.isEmpty(line))
                continue;
            String[] attr = line.split(",");
            if(attr.length != 3){
                System.err.println(line);
                continue;
            }
            if(StringUtils.isEmpty(attr[0]))
                continue;
            if(regionList.contains(attr[0].trim()) == false)
                continue;
            regionCount ++;
            if("0".equals(attr[1].trim()))
                continue;
            Integer count = retMap.get(attr[2].trim());
            if(count == null){
                count = 1;
            }else{
                count ++;
            }
            retMap.put(attr[2].trim(), count);
        }
        System.out.println("患者总数:" + regionCount);
        System.out.println(retMap);



    }

}
