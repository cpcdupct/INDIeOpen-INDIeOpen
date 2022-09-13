package es.upct.cpcd.indieopen.media;

import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.upct.cpcd.indieopen.common.exceptions.INDIeException;
import es.upct.cpcd.indieopen.media.domain.MediaLink;
import es.upct.cpcd.indieopen.media.domain.MediaLinkRepository;
import es.upct.cpcd.indieopen.media.dto.DeleteResourceRequest;

@Service
@Transactional(rollbackFor = INDIeException.class)
public class MediaService {

	// Modules
	private final MediaHandler mediaHandler;
	private final MediaFinder mediaFinder;

	@Autowired
	public MediaService(MediaLinkRepository mediaRepository) {
		this.mediaFinder = new MediaFinder(mediaRepository);
		MediaCreator mediaCreator = new MediaCreator(mediaRepository);
		this.mediaHandler = new MediaHandler(mediaFinder, mediaCreator);
	}

	public Set<MediaLink> findAllResources(String userId, int unitId) {
		return mediaFinder.findAllMediaByUnitAndUser(unitId, userId);
	}

	public List<String> getMediaResourceStatus(List<DeleteResourceRequest> deleteResourceRequestList) {
		return this.mediaFinder.getMediaResourceStatus(deleteResourceRequestList);
	}

	public void syncrhonizeMediaResourcesWithSavedUnit(String userId, int unitId, JSONArray modelSections) {
		mediaHandler.syncrhonizeMediaResourcesWithSavedUnit(userId, unitId, modelSections);
	}

	public void syncrhonizeMediaResourcesWithDeletedUnit(String userId, int unitId, JSONArray jsonArray) {
		mediaHandler.syncrhonizeMediaResourcesWithDeletedUnit(userId, unitId, jsonArray);
	}
}
