package google;

import java.util.List;

/**
 * Created by wangshuqiang on 2017/2/22.
 */
public class GoogleVisionRequest {

    private GoogleVisionImage image;

    private List<GoogleVisionFeature> features;

    public GoogleVisionImage getImage() {
        return image;
    }

    public GoogleVisionRequest setImage(GoogleVisionImage image) {
        this.image = image;
        return this;
    }

    public List<GoogleVisionFeature> getFeatures() {
        return features;
    }

    public GoogleVisionRequest setFeatures(List<GoogleVisionFeature> features) {
        this.features = features;
        return this;
    }

    public static GoogleVisionRequest build(){
        return new GoogleVisionRequest();
    }


}
