package project.google.cloudvault.model;

import java.util.List;

import lombok.Data;
@Data
public class DriveFileResponse {
    private List<PhotoItem> Files;
}
