package es.upct.cpcd.indieopen.video;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.dao.DataAccessException;

import es.upct.cpcd.indieopen.common.exceptions.INDIeException;
import es.upct.cpcd.indieopen.common.exceptions.INDIeExceptionBuilder;
import es.upct.cpcd.indieopen.common.exceptions.INDIeException.Status;
import es.upct.cpcd.indieopen.services.document.DocumentDBCollection;
import es.upct.cpcd.indieopen.services.document.DocumentDBManager;
import es.upct.cpcd.indieopen.services.document.DocumentDataException;
import es.upct.cpcd.indieopen.services.document.DocumentHelper;
import es.upct.cpcd.indieopen.services.transform.TransformService;
import es.upct.cpcd.indieopen.services.transform.TransformServiceException;
import es.upct.cpcd.indieopen.video.domain.Video;
import es.upct.cpcd.indieopen.video.domain.VideoRepository;
import es.upct.cpcd.indieopen.video.dto.VideoWithContent;
import lombok.extern.log4j.Log4j2;

@Log4j2
class VideoPublisher {
    private final VideoRepository videoRepository;
    private final DocumentDBManager documentDbManager;
    private final VideoFinder videoFinder;
    private TransformService transformService;

    VideoPublisher(VideoRepository videoRepository, DocumentDBManager documentDbManager, VideoFinder videoFinder,
                   TransformService transformService) {
        this.videoRepository = videoRepository;
        this.documentDbManager = documentDbManager;
        this.videoFinder = videoFinder;
        this.transformService = transformService;
    }

    void publishVideo(String userId, int videoId) throws INDIeException {
        VideoWithContent videoWithContent = videoFinder.findVideoWithContentById(userId, videoId);

        Video video = videoWithContent.getVideo();
        Document document = videoWithContent.getDocument();

        try {
            JSONArray interactiveData = transformService.transformVideo(videoWithContent);

            // Update document
            DocumentHelper.updatePublishedVideoDocument(document, interactiveData);
            documentDbManager.replaceDocument(DocumentDBCollection.VIDEOS, video.getDocumentID(), document);

            // Update the video
            video.publishVideo();
            videoRepository.save(video);
        } catch (DocumentDataException | DataAccessException | JSONException | TransformServiceException e) {
            log.error("Error in publish video ", e);
            throw new INDIeExceptionBuilder("Error in createVideo  ", e).status(Status.INTERNAL_ERROR).build();
        }
    }

    void setTransformService(TransformService transformService) {
        this.transformService = transformService;
    }
}
