package es.upct.cpcd.indieopen.media.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DeleteResourceRequest implements Serializable {
    private static final long serialVersionUID = "MY_SERIAL_VERSION";

    private final String resource;
}
