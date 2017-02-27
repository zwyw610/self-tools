package google;

/**
 * Created by wangshuqiang on 2017/2/22.
 */
public class GoogleVisionFeature {

    public GoogleVisionFeature(GoogleVisionFeatureType type, int maxResults){
        this.type = type;
        this.maxResults = maxResults;
    }

    private GoogleVisionFeatureType type;

    private int maxResults = 10;

    public GoogleVisionFeatureType getType() {
        return type;
    }

    public void setType(GoogleVisionFeatureType type) {
        this.type = type;
    }

    public int getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    public static GoogleVisionFeature getOcr(){
        return new GoogleVisionFeature(GoogleVisionFeatureType.TEXT_DETECTION, 10);
    }
}
