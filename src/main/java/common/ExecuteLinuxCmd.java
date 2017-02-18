package common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by wangshuqiang on 2017/2/18.
 */
public class ExecuteLinuxCmd {

    public void exec(){

        //-c参数是告诉它读取随后的字符串，而最后的参数是你要运行的脚本
        String cmd = "cp ~/Downloads/IMG_0338.MP4 ~/Downloads/IMG_0338_copy.MP4";
        String[] cmdAttr = new String[]{"/bin/sh", "-c", cmd};
        cmdAttr = new String[]{"/bin/sh", "-c", "ls ~/"};
        //cmd = "ls";
        try {
            Process p = Runtime.getRuntime().exec(cmdAttr);
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            br.lines().forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
