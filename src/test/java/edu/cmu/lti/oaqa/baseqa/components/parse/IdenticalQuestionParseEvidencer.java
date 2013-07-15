package edu.cmu.lti.oaqa.baseqa.components.parse;

import edu.cmu.lti.oaqa.baseqa.data.gerp.DefaultEvidenceWrapper;
import edu.cmu.lti.oaqa.baseqa.data.gerp.EvidenceWrapper;
import edu.cmu.lti.oaqa.baseqa.data.nlp.ParseWrapper;
import edu.cmu.lti.oaqa.baseqa.gerpphase.parse.AbstractQuestionParseEvidencer;

public class IdenticalQuestionParseEvidencer extends AbstractQuestionParseEvidencer {

  @Override
  protected EvidenceWrapper<?, ?> evidence(ParseWrapper gerpable) {
    return new DefaultEvidenceWrapper(1.0f);
  }

}
