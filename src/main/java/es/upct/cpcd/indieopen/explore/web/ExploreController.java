package es.upct.cpcd.indieopen.explore.web;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.upct.cpcd.indieopen.common.exceptions.INDIeException;
import es.upct.cpcd.indieopen.explore.ExploreService;
import es.upct.cpcd.indieopen.explore.web.resources.RecentUnitResource;
import es.upct.cpcd.indieopen.explore.web.resources.TopRatedUnitResource;
import es.upct.cpcd.indieopen.unit.web.resources.AuthorResource;
import es.upct.cpcd.indieopen.unit.web.resources.UnitResource;

@RestController
@RequestMapping("/explore")
public class ExploreController {

	private final ExploreService exploreService;

	@Autowired
	public ExploreController(ExploreService exploreService) {
		this.exploreService = exploreService;
	}

	@GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
	public Page<UnitResource> searchUnit(
			@PageableDefault(sort = "publishedDate", direction = Direction.DESC) Pageable page,
			@RequestParam(name = "text", required = false) String text,
			@RequestParam(name = "category", required = false) String category,
			@RequestParam(name = "type", required = false) String type,
			@RequestParam(name = "usemode", required = false) String usemode,
			@RequestParam(name = "context", required = false) String context,
			@RequestParam(name = "language", required = false) String language,
			@RequestParam(name = "ageRange", required = false) String ageRange,
			@RequestParam(name = "author", required = false) String author) {
		return exploreService.searchUnits(page, text, category, type, usemode, context, language, ageRange, author);
	}

	@GetMapping(value = "/top", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<TopRatedUnitResource> findTopRatedUnits() {
		return exploreService.findTopRatedUnits().stream().map(TopRatedUnitResource::from).collect(Collectors.toList());
	}

	@GetMapping(value = "/recent", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<RecentUnitResource> findMostRecentUnitsShared(
			@RequestParam(required = false, name = "type") String type) {
		return exploreService.findMostRecentUnitsShared(type).stream().map(RecentUnitResource::from)
				.collect(Collectors.toList());
	}

	@GetMapping(value = "/details/{unitId}")
	public UnitResource findOpenUnitInformation(@PathVariable int unitId) throws INDIeException {
		return exploreService.findUnitDetailsById(unitId);
	}

	@GetMapping(value = "/authors", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<AuthorResource> findTopAuthors(@RequestParam(required = false) String name) throws INDIeException {
		return exploreService.getListOfAuthors(name);
	}

	@GetMapping(value = "/authors/{authorId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public AuthorResource findAuthor(@PathVariable String authorId) throws INDIeException {
		return exploreService.getAuthor(authorId);
	}
}
