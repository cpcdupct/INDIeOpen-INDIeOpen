package es.upct.cpcd.indieopen.media;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import es.upct.cpcd.indieopen.media.domain.MediaLink;
import es.upct.cpcd.indieopen.media.domain.MediaLinkRepository;
import es.upct.cpcd.indieopen.media.dto.DeleteResourceRequest;

class MediaFinder {

    private final MediaLinkRepository mediaRepository;

    MediaFinder(MediaLinkRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    Set<MediaLink> findAllMediaByUnitAndUser(int unitId, String userId) {
        List<MediaLink> mediaList = mediaRepository.findByUnitAndUser(unitId, userId);
        return new HashSet<>(mediaList);

    }

    public Set<MediaLink> findMediaLinkByResource(String resource) {
        List<MediaLink> mediaList = mediaRepository.findByResource(resource);
        return new HashSet<>(mediaList);
    }

    public List<String> getMediaResourceStatus(List<DeleteResourceRequest> deleteResourceRequestList) {
        List<String> listOfResourceStatus = new ArrayList<>();

        for (DeleteResourceRequest deleteResourceRequest : deleteResourceRequestList) {
            Set<MediaLink> mediaByResource = findMediaLinkByResource(deleteResourceRequest.getResource());
            if (!mediaByResource.isEmpty())
                listOfResourceStatus.add(deleteResourceRequest.getResource());
        }

        return listOfResourceStatus;
    }

}
