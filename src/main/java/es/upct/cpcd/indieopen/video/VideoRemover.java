package es.upct.cpcd.indieopen.video;

import es.upct.cpcd.indieopen.common.exceptions.ErrorCodes;
import es.upct.cpcd.indieopen.common.exceptions.INDIeException;
import es.upct.cpcd.indieopen.common.exceptions.INDIeException.Status;
import es.upct.cpcd.indieopen.common.exceptions.INDIeExceptionBuilder;
import es.upct.cpcd.indieopen.common.exceptions.INDIeExceptionFactory;
import es.upct.cpcd.indieopen.services.document.DocumentDBCollection;
import es.upct.cpcd.indieopen.services.document.DocumentDBManager;
import es.upct.cpcd.indieopen.services.document.DocumentDataException;
import es.upct.cpcd.indieopen.video.domain.Video;
import es.upct.cpcd.indieopen.video.domain.VideoRepository;

class VideoRemover {

    private final VideoRepository videoRepository;
    private final DocumentDBManager documentDbManager;

    VideoRemover(VideoRepository videoRepository, DocumentDBManager documentDbManager) {
        this.videoRepository = videoRepository;
        this.documentDbManager = documentDbManager;
    }

    public void removeVideo(String userId, int videoId) throws INDIeException {
        Video video = videoRepository.findByIdAndAuthorId(videoId, userId)
                .orElseThrow(() -> new INDIeExceptionBuilder("Video not found").status(Status.USER_ERROR)
                        .code(ErrorCodes.ENTITY_NOT_ACCESSIBLE).build());

        try {
            documentDbManager.deleteDocument(DocumentDBCollection.VIDEOS, video.getDocumentID());
            videoRepository.delete(video);
        } catch (DocumentDataException e) {
            throw INDIeExceptionFactory.createInternalException(e);
        }
    }
}
