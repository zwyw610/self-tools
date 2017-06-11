package com.duke.self;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Read on 2017/6/11.
 * 好收成微信后台调用量统计
 */
public class HaoShouChengRunner {

    public static Map<String, String> cateMap = new HashMap<String, String>();
    public static List<String> sortList = new ArrayList<String>();


    static {
        cateMap.put("cate/38", "酒水饮料(cate/38)");
        cateMap.put("cate/44", "时令水果(cate/44)");
        cateMap.put("cate/37", "乳品蛋类(cate/37)");
        cateMap.put("cate/39", "副食调味(cate/39)");
        cateMap.put("cate/52", "米面粮油(cate/52)");
        cateMap.put("cate/22", "清洁用品(cate/22)");
        cateMap.put("cate/45", "休闲零食(cate/45)");
        cateMap.put("cate/55", "整箱水果(cate/55)");
        cateMap.put("cate/40", "冷热速食(cate/40)");
        cateMap.put("cate/59", "进口专区(cate/59)");
        cateMap.put("cate/46", "冲调食品(cate/46)");
        cateMap.put("cate/41", "家居日用(cate/41)");
        cateMap.put("cate/132", "康师傅专区(cate/132)");
        cateMap.put("cate/53", "美容护理(cate/53)");

        sortList.add("cate/38");
        sortList.add("cate/44");
        sortList.add("cate/37");
        sortList.add("cate/39");
        sortList.add("cate/52");
        sortList.add("cate/22");
        sortList.add("cate/45");
        sortList.add("cate/55");
        sortList.add("cate/40");
        sortList.add("cate/59");
        sortList.add("cate/46");
        sortList.add("cate/41");
        sortList.add("cate/132");
        sortList.add("cate/53");

    }


    public static void main(String[] args) throws Throwable {
        HttpClient client = new HttpClient();
        client.initClientParam();
        List<HaoShouShengEntity> list = new ArrayList<HaoShouShengEntity>();
        String[] dateList = new String[]{"2017-05-27","2017-05-28","2017-05-29","2017-05-30"};
        for(String date : dateList){
            String weixinUrl = "https://mp.weixin.qq.com/misc/webpageanalysis?action=listintfurlstat&func_name=getNetworkType&begin_date=" + date +"&end_date="+date+"&token=756669216&lang=zh_CN";
            weixinUrl = weixinUrl.replaceAll("$date", date);
            String ret = client.executeGetHouseInfo(weixinUrl, "utf-8", "这里需要设置cookie信息");
            int startIndex = ret.indexOf("list: [");
            int endIndex = ret.indexOf("queryParams: {");
            String info = ret.substring(startIndex + 6, endIndex - 1);
            while (true) {
                int s = info.indexOf("{");
                int e = info.indexOf("}");
                if (s == -1)
                    break;
                System.out.println(s + "," + e);
                String tmp = info.substring(s, e);
                info = info.substring(e + 1);
                String[] attr = tmp.split(",");
                HaoShouShengEntity entity = new HaoShouShengEntity();
                String[] url = attr[0].replaceAll("\n", "").replaceAll("\"", "").split(":");
                entity.setUrl(url[1] + ":" + url[2]);
                String[] pvAttr = attr[1].replaceAll("\n", "").split(":");
                entity.setPv(Integer.parseInt(pvAttr[1].trim()));
                String[] uvAttr = attr[2].replaceAll("\n", "").split(":");
                entity.setUv(Integer.parseInt(uvAttr[1].trim()));
                list.add(entity);
                if (StringUtils.isEmpty(tmp)) {
                    break;
                }
            }
        }
        Map<String, String> resultMap = new HashMap<String, String>();
        for(Map.Entry<String, String> entry : cateMap.entrySet()){
            String key = entry.getKey();
            int pvCount = 0;
            int uvCount = 0;
            for(HaoShouShengEntity hs : list){
                if(hs.getUrl().contains(key)){
                    pvCount += hs.getPv();
                    uvCount += hs.getUv();
                }
            }
            resultMap.put(key, pvCount + "_" + uvCount);
        }

        System.out.println("PV-------------");
        for(String cate : sortList){
            String value = resultMap.get(cate);
            String[] attr = value.split("_");
            System.out.println(attr[0]);
        }

        System.out.println("UV-------------");
        for(String cate : sortList){
            String value = resultMap.get(cate);
            String[] attr = value.split("_");
            System.out.println(attr[1]);
        }




    }

}
