package google;

/**
 * Created by wangshuqiang on 2017/2/22.
 */
public class GoogleVisionImage {

    public GoogleVisionImage(String content){
        this.content = content;
    }

    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
