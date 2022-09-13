package es.upct.cpcd.indieopen.media;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.JSONArray;

import es.upct.cpcd.indieopen.media.domain.MediaLink;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class MediaHandler {
	private final MediaFinder mediaFinder;
	private final MediaCreator mediaCreator;

	MediaHandler(MediaFinder mediaFinder, MediaCreator mediaCreator) {
		this.mediaFinder = mediaFinder;
		this.mediaCreator = mediaCreator;
	}

	void syncrhonizeMediaResourcesWithSavedUnit(String userId, int unitId, JSONArray modelSections) {
		// 1 Extract the resource ids from the sections
		Set<MediaLink> updatedMedia = extractResourcesFromSections(userId, unitId, modelSections);

		// 2 Get the media from the existing unit and user id
		Set<MediaLink> existingMedia = mediaFinder.findAllMediaByUnitAndUser(unitId, userId);

		// 3 Get the relative complement of updated resources in existing media, which
		// is the set of media to be removed from the database
		Set<MediaLink> mediaToBeDeleted = relativeComplementOfSetAInSetB(existingMedia, updatedMedia);

		// 4 Get the relative complement of existing media in updated media, which is
		// the set of media to be saved in the database
		Set<MediaLink> mediaToBeSaved = relativeComplementOfSetAInSetB(updatedMedia, existingMedia);

		// 5 Delete the media
		mediaCreator.deleteSetOfMedia(mediaToBeDeleted);

		// 6 Add the rest of the media
		mediaCreator.saveSetOfMedia(mediaToBeSaved);
	}

	private Set<MediaLink> relativeComplementOfSetAInSetB(Set<MediaLink> setA, Set<MediaLink> setB) {
		return setA.stream().filter(val -> !setB.contains(val)).collect(Collectors.toSet());
	}

	private Set<MediaLink> extractResourcesFromSections(String userId, int unitId, JSONArray modelSections) {
		try (ContentProcessor contentProcessor = ContentProcessor.create(modelSections)) {
			Set<String> mediaURLs = contentProcessor.extractAllMediaURL();
			return mediaURLs.stream().map(m -> MediaLink.create(m, unitId, userId)).collect(Collectors.toSet());
		} catch (Exception e) {
			log.fatal("Cannot extract MediaLink for unit->" + unitId + "; user->" + userId, e);
			return Collections.emptySet();
		}
	}

	public void syncrhonizeMediaResourcesWithDeletedUnit(String userId, int unitId, JSONArray modelSections) {
		// 1 Extract the resource ids from the sections
		Set<MediaLink> mediaFromDeletedUnit = extractResourcesFromSections(userId, unitId, modelSections);
		mediaCreator.deleteSetOfMedia(mediaFromDeletedUnit);
	}

}
