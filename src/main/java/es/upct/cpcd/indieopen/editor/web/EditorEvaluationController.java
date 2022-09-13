package es.upct.cpcd.indieopen.editor.web;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.upct.cpcd.indieopen.common.BaseController;
import es.upct.cpcd.indieopen.common.exceptions.INDIeException;
import es.upct.cpcd.indieopen.editor.EditorService;
import es.upct.cpcd.indieopen.questions.QuestionService;
import es.upct.cpcd.indieopen.questions.resources.QuestionResource;
import es.upct.cpcd.indieopen.token.ModelToken;

@RestController
@RequestMapping("/editor/evaluation")
public class EditorEvaluationController extends BaseController {

    private final EditorService editorService;
    private final QuestionService questionService;

    @Autowired
    EditorEvaluationController(EditorService editorService, QuestionService questionService) {
        this.editorService = editorService;
        this.questionService = questionService;
    }

    @GetMapping(value = "/questions/{token}")
    public List<QuestionResource> searchQuestions(@PathVariable("token") String token) throws INDIeException {
        ModelToken tokenInfo = this.editorService.getTokenInfo(token);

        return this.questionService.findQuestionsByAuthor(tokenInfo.getUser()).stream()
                .map(QuestionResource::fromQuestion).collect(Collectors.toList());
    }

}
