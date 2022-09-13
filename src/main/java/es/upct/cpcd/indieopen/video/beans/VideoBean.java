package es.upct.cpcd.indieopen.video.beans;

import javax.validation.constraints.NotBlank;

import lombok.*;
import org.hibernate.validator.constraints.URL;

@Data
@Builder
public class VideoBean {
    @NotBlank
    private String name;

    @NotBlank
    @URL
    private String url;

}
