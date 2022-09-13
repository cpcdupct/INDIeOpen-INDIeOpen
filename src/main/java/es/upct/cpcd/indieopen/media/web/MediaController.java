package es.upct.cpcd.indieopen.media.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.upct.cpcd.indieopen.media.MediaService;
import es.upct.cpcd.indieopen.media.dto.DeleteResourceRequest;

@RestController
@RequestMapping("/media")
public class MediaController {

    private final MediaService mediaService;

    @Autowired
    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @GetMapping(path = "/canDelete", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getMediaResourceStatus(@RequestBody List<DeleteResourceRequest> deleteResourceRequestList) {
        return mediaService.getMediaResourceStatus(deleteResourceRequestList);
    }
}
