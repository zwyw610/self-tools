package com.duke.self;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Read on 2017/1/9.
 */
public class WebUtil {

    volatile static int i = 1;

    public static void main(String[] args) {

//        final HttpClient client = new HttpClient();
//        client.initClientParam();
//        final Map<String, String> param = new HashMap<String, String>();
//        param.put("Host", "m.hongbao.link.lianjia.com");
//        param.put("Proxy-Connection", "keep-alive");
//        param.put("X-Requested-With", "XMLHttpRequest");
//        param.put("platform", "iOS");
//        param.put("Accept", "application/json");
//        param.put("Origin", "http://m.hongbao.link.lianjia.com");
//        param.put("Referer", "http://m.hongbao.link.lianjia.com/springfestival/page/index");
//        param.put("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 10_2 like Mac OS X) AppleWebKit/602.3.12 (KHTML, like Gecko) Mobile/14C92 Lianjia/HomeLink/2.8.1.1");
//        param.put("Accept-Language", "zh-Hans-CN;q=1");
//
//        final String cookie = "lianjia_uuid=5809c8a7-f38d-164e-ad92-9534794455fb;lianjia_token=2.00409b7bfed670f85d5132c54c58a7d354;select_city=110000;lianjia_ssid=F8A3D9D3-DDC9-4F0B-A809-E12C232BA36C";
//
//        String fengyueCookie = "lianjia_ssid=C4A67C15-041D-4955-AFAD-F6C1E4EE2B1D; lianjia_token=2.0054ad541fc246c9e74504eaad0af26d9e; select_city=110000; lianjia_uuid=5809c8a7-f38d-164e-ad92-9534794455fb";
//
//
//        //WebUtil.print();
//        Executor singlePool = Executors.newSingleThreadExecutor();
//
//        for(;;){
//            long t1 = System.currentTimeMillis();
//            String ret = client.executePostWithCookie(
//                    "http://m.hongbao.link.lianjia.com/springfestival/page/ajax/packet/prise",
//                    param, null,
//                    "utf-8", cookie
//            );
//            System.out.println(System.currentTimeMillis()-t1);
//
//
//            singlePool.execute(new Runnable() {
//                public void run() {
//                    {
//                        CloseableHttpResponse response = null;
//                        try {
//                            response = HttpClient.queue.take();
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        HttpEntity entity = response.getEntity();
//                        try {
//                            String content = EntityUtils.toString(entity, "utf-8");
//                            System.out.println(content);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }finally {
//                            if(null != response){
//                                try {
//                                    response.close();
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }
//                    }
//                }
//            });
//        }

        //System.out.println(ret);


        //WebUtil.submitBeiFang();
        //WebUtil.senMsg();
        WebUtil.sendAJKMsg();
    }

    public static void sendAJKMsg() {
        HttpClient client = new HttpClient();
        client.initClientParam();
//        String ret = client.executeGet("http://my.anjuke.com/ajax/account/ajaxcheckmobile?mobile=18358330330&math=0.7771724566113667", "utf-8");
//        System.out.println(ret);
//        ret = client.executeGet("http://my.anjuke.com/broker/resetpwd/?mobile=18358330330&checkcode=9971", "utf-8");
//        System.out.println(ret);
        Map<String, String> param = new HashMap<String, String>();
        param.put("action", "listintfurlstat");
        param.put("func_name", "getNetworkType");
        param.put("begin_date", "2017-06-10");
        param.put("end_date", "2017-06-10");
        param.put("token", "756669216");
        param.put("lang", "zh_CN");


        String ret = client.executePostWithCookie("https://mp.weixin.qq.com/misc/webpageanalysis", param, null, null, "tvfe_boss_uuid=080061bd6d997f82; AMCV_248F210755B762187F000101%40AdobeOrg=793872103%7CMCIDTS%7C17134%7CMCMID%7C65357535325749063650544250941520723050%7CMCAAMLH-1480915135%7C11%7CMCAAMB-1480915135%7CNRX38WO0n5BH8Th-nqAG_A%7CMCAID%7CNONE; mobileUV=1_158bd4c9124_4647a; pac_uid=1_1871901067; h_uid=H09161207866; RK=cpvDebQuUD; pgv_pvi=8655442122; pt2gguin=o1871901067; ptcz=22178bf79770aa6278bac4b98f91e3eeccf0e6fb483b7312c272cc4dfff08bad; o_cookie=1871901067; pgv_info=ssid=s6226331000; pgv_pvid=2987487580; pgv_si=s4181786624; uuid=aafba6f755c271268ca9c9dc5ec3564f; ticket=866b51243390914cc88a4c7190307910894988f9; ticket_id=gh_27598ad74cec; account=1879667597@qq.com; cert=nVNJqlYk9vQbN_wyxCuh7TtQjo_Xddxd; noticeLoginFlag=1; data_bizuin=2392918751; data_ticket=tZeku0SAokNioUYnQ68UwCeGonBwniPAtLPchpe88FRwmKgyh6G1CkOcKs4luzy9; ua_id=0OJ5tG7bwyPOmiXRAAAAAEu8SuacCMrZfqNnE7q_yxk=; xid=d52a56aefb2925888afce8c52f91feef; openid2ticket_o3wqhjj39I00xF6fCSkjxjxtVSI4=IdS82jeLBX+EgVL7+yog0vEnDZyvRGk7+VBd8TCi0nc=; slave_user=gh_27598ad74cec; slave_sid=cHJEZnNiZzFHcDJKd2o1ZHFVZ3dlMnNZYTlRVEtRZ1ZBdUNWcXVSa3N1WE44bmZaZGVTWElpem1MSndSa2k4NnBKdlR0dm1UbnRHUkFvSnAxY2xhNGtMWUNwQjU0M21ldzVZd2RrUm9fVjVHZTFuaFVjeGI4b05mcHc2ckpkVmFhMWxQZGRoZjhreEpMTG1M; bizuin=2390966311");
        System.out.println(ret);
        ret = client.executeGetHouseInfo("https://mp.weixin.qq.com/misc/webpageanalysis?action=listintfurlstat&func_name=getNetworkType&begin_date=2017-06-10&end_date=2017-06-10&token=756669216&lang=zh_CN", "utf-8","tvfe_boss_uuid=080061bd6d997f82; AMCV_248F210755B762187F000101%40AdobeOrg=793872103%7CMCIDTS%7C17134%7CMCMID%7C65357535325749063650544250941520723050%7CMCAAMLH-1480915135%7C11%7CMCAAMB-1480915135%7CNRX38WO0n5BH8Th-nqAG_A%7CMCAID%7CNONE; mobileUV=1_158bd4c9124_4647a; pac_uid=1_1871901067; h_uid=H09161207866; RK=cpvDebQuUD; pgv_pvi=8655442122; pt2gguin=o1871901067; ptcz=22178bf79770aa6278bac4b98f91e3eeccf0e6fb483b7312c272cc4dfff08bad; o_cookie=1871901067; pgv_info=ssid=s6226331000; pgv_pvid=2987487580; pgv_si=s4181786624; uuid=aafba6f755c271268ca9c9dc5ec3564f; ticket=866b51243390914cc88a4c7190307910894988f9; ticket_id=gh_27598ad74cec; account=1879667597@qq.com; cert=nVNJqlYk9vQbN_wyxCuh7TtQjo_Xddxd; noticeLoginFlag=1; data_bizuin=2392918751; data_ticket=tZeku0SAokNioUYnQ68UwCeGonBwniPAtLPchpe88FRwmKgyh6G1CkOcKs4luzy9; ua_id=0OJ5tG7bwyPOmiXRAAAAAEu8SuacCMrZfqNnE7q_yxk=; xid=d52a56aefb2925888afce8c52f91feef; openid2ticket_o3wqhjj39I00xF6fCSkjxjxtVSI4=IdS82jeLBX+EgVL7+yog0vEnDZyvRGk7+VBd8TCi0nc=; slave_user=gh_27598ad74cec; slave_sid=cHJEZnNiZzFHcDJKd2o1ZHFVZ3dlMnNZYTlRVEtRZ1ZBdUNWcXVSa3N1WE44bmZaZGVTWElpem1MSndSa2k4NnBKdlR0dm1UbnRHUkFvSnAxY2xhNGtMWUNwQjU0M21ldzVZd2RrUm9fVjVHZTFuaFVjeGI4b05mcHc2ckpkVmFhMWxQZGRoZjhreEpMTG1M; bizuin=2390966311");
        System.out.println(ret);

    }


    public static void senMsg() {
        HttpClient client = new HttpClient();
        client.initClientParam();
        Map<String, String> param = new HashMap<String, String>();
        param.put("scene", "mobileReg");
        param.put("mobile", "86-18358330330");
        //param.put("mobile","86-15101594723");
        param.put("_input_charset", "utf-8");
        param.put("ctoken", "N06yhuJscWYMmPZj");
        String cookie = "_umdata=995747CFE8386C50F1D072C16F8B1391B7C8A7C36D8A7C8A37442FB6994F0E30182271AC5748332ECD43AD3E795C914CD235423FFE97FD5B0B7497E19A214A53; JSESSIONID=RZ04kE6QMNznLa9xOmXXxvOPZC736QauthRZ04; cna=IdrXD5ouC0gCAXlFgHp1oKkI; l=AnR0otfl6nsJ1N6sG11nh/1ixDjmTZg3; isg=Ag8PUqlQhO4is4AS3kenqnK2nqNszWNWf8_C0iEcq36F8C_yKQTzpg3jhJc0; mobileSendTime=-1; credibleMobileSendTime=-1; ctuMobileSendTime=-1; riskMobileBankSendTime=-1; riskMobileAccoutSendTime=-1; riskMobileCreditSendTime=-1; riskCredibleMobileSendTime=-1; riskOriginalAccountMobileSendTime=-1; ALIPAYJSESSIONID.sig=l8K6oWAekwKMoqXRjO6ZQyp41AI67jxtoQwRiTzauOw; ctoken=N06yhuJscWYMmPZj; umt=HB15f0ea68b8c81de4b5f6dc91dc07bb84; zone=RZ04A; JSESSIONID=5570982C235F43619A536A772458062E; ALIPAYJSESSIONID=RZ04kE6QMNznLa9xOmXXxvOPZC736QauthRZ04GZ00; spanner=lzrBVfiysjSTQElbo0oJkITKFbhCEjeU";
        String url = "https://memberprod.alipay.com/account/reg/section/reSendVerifyCode.json";
        for (int i = 0; i < 10; i++) {
            String vet = client.executeSendPostWithCookie(url, param, null, null, cookie);
            System.out.println(vet);
        }


    }


    public static void submitBeiFang() {
        HttpClient client = new HttpClient();
        client.initClientParam();
        Map<String, String> param = new HashMap<String, String>();
        param.put("keyword", "投诉");
        param.put("truename", "guest");
        param.put("phone", "15184593578");
        param.put("email", "tousu.beifang.com");
        param.put("deptId", "1001029000000000");

        param.put("qu", "津南区");
        param.put("jiedao", "双港");
        param.put("addr", "双港新家园");
        param.put("title", "双港新家园拆迁及管理问题");
        param.put("content", "<p>\n" +
                "\t&nbsp;&nbsp;双港新家园建设自14年至今几乎没有什么动作，感觉都处于停滞状态，咨询政府回复的信息很多都是由客观原因阻碍，其中拆迁问题是个重点。</p>\n" +
                "<p>\n" +
                "\t&nbsp;&nbsp;1、微山南路何时能由外环线全线打通至海河教育园区，政府回复目前有蓟汕高速桥墩冲突、拆迁问题阻碍，请问此类是否已解决，政府的工作计划具体是何时完工？</p>\n" +
                "<p>\n" +
                "\t&nbsp;&nbsp;2、双港新家园规划有一处公园，政府回复信息已处于图纸审核阶段，目前拆迁影响</p>\n" +
                "<p>\n" +
                "\t&nbsp;&nbsp;3、自双新街道成立后，市容环境有所改善，但最近又有所抬头，例如在领世路（香堤苑西侧），原为绿地，现已被破坏，变成了洗车场。这种行为不是占路这么简单了，破坏公共资源，但无人管理。</p>\n" +
                "<p>\n" +
                "\t&nbsp;&nbsp;4、景荷道上，微山路以西部分，占路经营严重，绿地破坏严重，一直无人管理，将来修复又要投入资金。</p>\n" +
                "<p>\n" +
                "\t&nbsp;&nbsp;5、新家园地区拆迁收尾工作谁是责任主体，有无工作计划，有无监督落实，零星未拆除建筑物已经成为垃圾厂、废品回收厂，已成为严重的安全隐患，曾经数次着火。</p>\n" +
                "<p>\n" +
                "\t&nbsp;&nbsp;以上问题希望领导能给与直接正面回复。</p>\n");

        param.put("isPublic", "是");

        param.put("button3", "\n" +
                "                         \n" +
                "                ");
        param.put("imgcodevalue", "8297");
        String ret = client.executeSubmitPostWithCookie("http://zw.enorth.com.cn/gov_open/question/addQuestion.do", param, null, null,
                "uid=1476095452866_3983759510; JSESSIONID=7481FD468ABE6151F34B53925D083447; td_cookie=18446744070071050818; pgv_pvi=7956659952; CNZZDATA1253815889=774928004-1476090730-http%253A%252F%252Fzw.enorth.com.cn%252F%7C1484618694");
        System.out.println(ret);


    }

}
