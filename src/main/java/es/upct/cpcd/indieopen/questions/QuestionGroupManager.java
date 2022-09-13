package es.upct.cpcd.indieopen.questions;

import java.util.List;
import java.util.Optional;

import com.google.common.collect.Lists;

import es.upct.cpcd.indieopen.questions.domain.QuestionGroup;
import es.upct.cpcd.indieopen.questions.domain.QuestionRepository;

class QuestionGroupManager {

    private final QuestionRepository questionRepository;

    QuestionGroupManager(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }


    public List<QuestionGroup> getQuestionGroups(String userId) {
        return Lists.newArrayList(questionRepository.findQuestionGroupsByAuthor(userId));
    }

    public Optional<QuestionGroup> getOrCreateQuestionGroup(String userId, String group) {
        if (group == null)
            return Optional.empty();

        List<QuestionGroup> existingGroups = getQuestionGroups(userId);

        // Check if group is a key
        Optional<QuestionGroup> groupKeyQuery = existingGroups.stream().filter(g -> g.getGroupKey().equals(group))
                .findFirst();

        if (groupKeyQuery.isPresent())
            return groupKeyQuery;

        // Check if group is a duplicated name

        Optional<QuestionGroup> groupNameQuery = existingGroups.stream().filter(g -> g.getGroupName().equals(group))
                .findFirst();

        if (groupNameQuery.isPresent())
            return groupNameQuery;

        QuestionGroup newGroup = new QuestionGroup(group);
        return Optional.of(newGroup);
    }
}
