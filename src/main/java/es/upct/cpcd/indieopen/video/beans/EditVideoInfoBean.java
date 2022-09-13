package es.upct.cpcd.indieopen.video.beans;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class EditVideoInfoBean {
    @NotBlank
    private String name;

}
