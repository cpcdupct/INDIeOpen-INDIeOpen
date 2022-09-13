package es.upct.cpcd.indieopen.unit.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class AgeRange implements Serializable {
    private static final long serialVersionUID = -8572516794624179868L;

    @Column(nullable = false)
    private int min;

    @Column(nullable = false)
    private int max;

    public AgeRange(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public Integer[] toRangeArray() {
        return new Integer[] { min, max };
    }
}
