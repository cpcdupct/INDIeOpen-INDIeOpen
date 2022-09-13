package es.upct.cpcd.indieopen.media.domain;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
class MediaKey implements Serializable {
    private static final long serialVersionUID = 1L;

    private String resource;
    private int unit;
    private String user;

}
