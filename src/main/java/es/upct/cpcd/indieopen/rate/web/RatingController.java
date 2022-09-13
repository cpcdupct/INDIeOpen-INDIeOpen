package es.upct.cpcd.indieopen.rate.web;

import es.upct.cpcd.indieopen.common.BaseController;
import es.upct.cpcd.indieopen.common.exceptions.INDIeException;
import es.upct.cpcd.indieopen.rate.RatingService;
import es.upct.cpcd.indieopen.rate.request.AddRatingRequest;
import es.upct.cpcd.indieopen.rate.resources.RatingAverageResource;
import es.upct.cpcd.indieopen.rate.resources.RatingResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rating")
public class RatingController extends BaseController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @GetMapping(value = "/list/{unitId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<RatingResource> getRatingsByUnit(@PathVariable("unitId") int unitId, Pageable page) {
        return this.ratingService.getRatingsByUnit(unitId, page);
    }

    @PostMapping(value = "/unit/{unitId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RatingResource> addRating(@PathVariable("unitId") int unitId,
                                                    @RequestBody AddRatingRequest request) throws INDIeException {
        RatingResource resource = this.ratingService.createRating(getCurrentUserId(), unitId, request);
        return ResponseEntity.ok(resource);
    }

    @GetMapping(value = "/find/{unitId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RatingResource> findRating(@PathVariable("unitId") int unitId) throws INDIeException {
        RatingResource resource = this.ratingService.findRating(getCurrentUserId(), unitId);
        return ResponseEntity.ok(resource);
    }

    @GetMapping(value = "/average/{unitId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RatingAverageResource> getRatingAverage(@PathVariable("unitId") int unitId)
            throws INDIeException {
        return ResponseEntity.ok(this.ratingService.findAverageRatingByUnit(unitId));
    }
}
