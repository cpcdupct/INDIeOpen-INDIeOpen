package es.upct.cpcd.indieopen.editor.web;

import es.upct.cpcd.indieopen.common.exceptions.INDIeException;
import es.upct.cpcd.indieopen.common.response.JSONResponse;
import es.upct.cpcd.indieopen.editor.EditorService;
import es.upct.cpcd.indieopen.editor.web.resources.ModelEditorResource;
import es.upct.cpcd.indieopen.editor.web.resources.ModelTokenResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/editor")
class EditorController {

    private final EditorService editorService;

    @Autowired
    EditorController(EditorService editorService) {
        this.editorService = editorService;
    }

    @GetMapping(value = "/info/{token}")
    public ModelTokenResource tokenInfo(@PathVariable("token") String token) throws INDIeException {
        return ModelTokenResource.fromModelToken(editorService.getTokenInfo(token));
    }

    @GetMapping(value = "/load/{token}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ModelEditorResource load(@PathVariable("token") String token) throws INDIeException {
        return ModelEditorResource.from(editorService.getModelEditor(token));
    }

    @PostMapping(value = "/save/{token}")
    public ResponseEntity<?> save(@PathVariable("token") String token, @RequestBody String model)
            throws INDIeException {
        this.editorService.save(token, model);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/preview/{token}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> preview(@PathVariable("token") String token, @RequestBody String model)
            throws INDIeException {
        String url = this.editorService.previewUnit(token, model);
        return ResponseEntity.ok(JSONResponse.create().add("url", url).toBodyString());
    }
}
