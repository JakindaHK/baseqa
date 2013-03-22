package edu.cmu.lti.oaqa.cse.basephase.ie;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import edu.cmu.lti.oaqa.ecd.log.AbstractLoggedComponent;
import edu.cmu.lti.oaqa.framework.BaseJCasHelper;
import edu.cmu.lti.oaqa.framework.QALogEntry;
import edu.cmu.lti.oaqa.framework.ViewManager;
import edu.cmu.lti.oaqa.framework.data.base.JCasHelper;
import edu.cmu.lti.oaqa.framework.types.InputElement;
import edu.cmu.lti.oaqa.framework.data.AnswerWrapper;
import edu.cmu.lti.oaqa.framework.data.AnswerArray;
import edu.cmu.lti.oaqa.framework.data.RetrievalResult;
import edu.cmu.lti.oaqa.framework.data.RetrievalResultArray;


public abstract class AbstractInformationExtractor extends AbstractLoggedComponent {
  
  @Override
  public void initialize(UimaContext c) throws ResourceInitializationException {
    super.initialize(c); 
  }

	public abstract List<AnswerWrapper> extractAnswerCandidates(String qid,
			String questionText, String answerType, List<String> keyterms,
			List<String> keyphrases, List<RetrievalResult> documents);

	@Override
	public void process(JCas FullJCas) throws AnalysisEngineProcessException {
		try {
      JCas jcasSource = ViewManager.getDocumentView(FullJCas);
      JCas jcasTarget = ViewManager.getCandidateView(FullJCas);

			InputElement input = (InputElement) BaseJCasHelper.getAnnotation(
					                                               FullJCas, InputElement.type);
			String question = input.getQuestion();
			String qid = input.getSequenceId();
			String answerType = JCasHelper.loadAnswerType(FullJCas);
			List<String> keyterms = new ArrayList<String>();
			List<String> keyphrases = new ArrayList<String>();
			JCasHelper.loadKeyTermsAndPhrases(FullJCas, keyterms, keyphrases);
			
			ArrayList<String> AllSourceIds = new ArrayList<String>();
			
			RetrievalResultArray.GetAllSourceIds(jcasSource, AllSourceIds);
			
			for (String SourceId: AllSourceIds) {
  			List<RetrievalResult> documents
  								= RetrievalResultArray.retrieveRetrievalResults(SourceId, jcasSource);
  			
        log("Loaded " + documents.size() + 
            " candidates to extract answers from Source: "+ SourceId);
  			
  			List<AnswerWrapper> CurrAnswers = extractAnswerCandidates(qid, question, 
  																	answerType, keyterms, keyphrases, documents);
  			
  			// Produced by other annotators
  			List<AnswerWrapper> answers = AnswerArray.retrieveAnswers(SourceId, jcasTarget);
  			
  			answers.addAll(CurrAnswers);
  			
  			AnswerArray.storeAnswers(SourceId, jcasTarget, answers);
  			log("Extracted " + answers.size() + " answer(s) from Source: "+ SourceId);
/*
        int i = 0;
        for (AnswerWrapper ans: answers) {
          System.out.println("## " + i + " " + ans.getScore() + " >>> " + ans.getText());
          if (i++ >= 1000) break;
        }
*/
			}
		} catch (Exception e) {
			throw new AnalysisEngineProcessException(e);
		}
	}
	
  protected final void log(String message) {
    super.log(QALogEntry.INFORMATION_EXTRACTION, message);
  }	
}
