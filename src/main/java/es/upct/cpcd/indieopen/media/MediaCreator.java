package es.upct.cpcd.indieopen.media;

import java.util.Set;

import es.upct.cpcd.indieopen.media.domain.MediaLink;
import es.upct.cpcd.indieopen.media.domain.MediaLinkRepository;

class MediaCreator {
    private final MediaLinkRepository mediaRepository;

    MediaCreator(MediaLinkRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    void deleteSetOfMedia(Set<MediaLink> mediaToBeDeleted) {
        this.mediaRepository.deleteAll(mediaToBeDeleted);
    }

    void saveSetOfMedia(Set<MediaLink> mediaToBeSaved) {
        this.mediaRepository.saveAll(mediaToBeSaved);
    }

}
