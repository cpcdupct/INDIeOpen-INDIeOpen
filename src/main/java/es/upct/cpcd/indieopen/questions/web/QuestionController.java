package es.upct.cpcd.indieopen.questions.web;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.upct.cpcd.indieopen.common.BaseController;
import es.upct.cpcd.indieopen.common.exceptions.INDIeException;
import es.upct.cpcd.indieopen.questions.QuestionService;
import es.upct.cpcd.indieopen.questions.beans.QuestionBean;
import es.upct.cpcd.indieopen.questions.domain.Question;
import es.upct.cpcd.indieopen.questions.resources.QuestionGroupResource;
import es.upct.cpcd.indieopen.questions.resources.QuestionResource;

@RestController
@RequestMapping("/questions")
public class QuestionController extends BaseController {

    private final QuestionService questionService;

    @Autowired
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<QuestionResource> findQuestions(
            @PageableDefault(sort = "questionText", direction = Direction.ASC) Pageable page,
            @RequestParam(required = false, name = "group") String group,
            @RequestParam(required = false, name = "type") String type) {
        return questionService.findQuestionsByAuthor(getCurrentUserId(), page, group, type)
                .map(QuestionResource::fromQuestion);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<QuestionResource> createQuestion(@RequestBody QuestionBean questionBean)
            throws INDIeException {
        Question question = questionService.createQuestion(getCurrentUserId(), questionBean);
        return ResponseEntity.created(URI.create("/api/questions/detail" + question.getID()))
                .body(QuestionResource.fromQuestion(question));
    }

    @GetMapping(path = "/detail/{questionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public QuestionResource findQuestionDetailById(@PathVariable("questionId") String questionId)
            throws INDIeException {
        return QuestionResource.fromQuestion(questionService.findQuestionById(getCurrentUserId(), questionId));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/groups")
    public List<QuestionGroupResource> listQuestionGroups() {
        return questionService.getQuestionGroupsFromUser(getCurrentUserId()).stream().map(QuestionGroupResource::from)
                .collect(Collectors.toList());
    }

    @PutMapping(path = "/update/{questionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateQuestion(@PathVariable String questionId, @RequestBody QuestionBean questionBean)
            throws INDIeException {
        questionService.updateQuestion(getCurrentUserId(), questionId, questionBean);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "/delete/{questionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteQuestion(@PathVariable("questionId") String questionId) throws INDIeException {
        questionService.deleteQuestion(getCurrentUserId(), questionId);
        return ResponseEntity.ok().build();
    }
}
