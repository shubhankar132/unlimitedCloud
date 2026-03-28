package project.google.cloudvault.model;

import lombok.Data;
@Data
public class PhotoItem {
    private String id;
    private String name;
    private String thumbnailLink;
    private String webViewLink;
    private String createdTime;
    private ImageMetadata imageMetadata;
}
