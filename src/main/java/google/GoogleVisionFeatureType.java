package google;

/**
 * Created by wangshuqiang on 2017/2/22.
 */
public enum GoogleVisionFeatureType {
    TYPE_UNSPECIFIED("Unspecified feature type"),
    FACE_DETECTION("Run face detection"),
    LANDMARK_DETECTION("Run landmark detection"),
    LOGO_DETECTION("Run logo detection"),
    LABEL_DETECTION("Run label detection"),
    TEXT_DETECTION("Run OCR"),
    SAFE_SEARCH_DETECTION("precedence when both DOCUMENT_TEXT_DETECTION and TEXT_DETECTION are present. Run computer vision models to compute image safe-search properties"),
    IMAGE_PROPERTIES("Compute a set of image properties, such as the image's dominant colors"),

    ;

    GoogleVisionFeatureType(String desc) {
        this.desc = desc;
    }

    private String desc;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
