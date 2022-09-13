package es.upct.cpcd.indieopen.rate.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddRatingRequest {
    @Min(1)
    @Max(5)
    private int rating;

}
