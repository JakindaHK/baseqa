package edu.cmu.lti.oaqa.baseqa.gerp.retrieval;

import java.util.List;

import com.google.common.collect.Lists;

import edu.cmu.lti.oaqa.baseqa.data.kb.InterpretationWrapper;
import edu.cmu.lti.oaqa.baseqa.data.nlp.ParseWrapper;
import edu.cmu.lti.oaqa.baseqa.data.nlp.QuestionWrapper;
import edu.cmu.lti.oaqa.baseqa.data.nlp.TokenWrapper;
import edu.cmu.lti.oaqa.baseqa.data.retrieval.AbstractQueryWrapper;
import edu.cmu.lti.oaqa.baseqa.data.retrieval.QueryConceptWrapper;
import edu.cmu.lti.oaqa.baseqa.data.retrieval.QueryConceptWrapper.ConceptType;
import edu.cmu.lti.oaqa.baseqa.data.retrieval.QueryOperatorWrapper;
import edu.cmu.lti.oaqa.baseqa.data.retrieval.QueryOperatorWrapper.QueryOperatorName;

public class BagOfWordsQueryGenerator extends AbstractQueryGenerator {

  @Override
  protected AbstractQueryWrapper generate(QuestionWrapper question, ParseWrapper parse,
          InterpretationWrapper interpretation) {
    List<QueryConceptWrapper> queryConcepts = Lists.newArrayList();
    for (TokenWrapper token : parse.getTokens()) {
      queryConcepts.add(new QueryConceptWrapper(Lists.<String> newArrayList(),
              ConceptType.QATOKEN_TYPE, token.getLemmaForm(), "", new QueryOperatorWrapper(
                      QueryOperatorName.WEIGHT, Lists.newArrayList("1")), Lists
                      .<QueryConceptWrapper> newArrayList(), ""));
    }
    return new AbstractQueryWrapper(queryConcepts);
  }
}
