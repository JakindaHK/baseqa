package edu.cmu.lti.oaqa.baseqa.data.nlp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.oaqa.model.retrieval.AbstractQuery;
import org.oaqa.model.retrieval.QueryConcept;

import edu.cmu.lti.oaqa.baseqa.data.core.FSListWrapper;
import edu.cmu.lti.oaqa.framework.BaseJCasHelper;

@Deprecated
public class KeytermList extends FSListWrapper<QueryConcept> {

  public KeytermList(JCas jcas) {
    super(jcas);
  }

  @Override
  public void clear() {
    Iterator<?> it = jcas.getJFSIndexRepository().getAllIndexedFS(AbstractQuery.type);
    while (it.hasNext()) {
      AbstractQuery query = (AbstractQuery) it.next();
      query.removeFromIndexes();
    }
  }

  @Override
  public void complete() {
    AbstractQuery query = new AbstractQuery(jcas);
    query.setConcepts(list);
    query.addToIndexes();
  }

  public void setKeyterms(List<Keyterm> keyterms) throws AnalysisEngineProcessException {
    setList(keyterms);
  }

  private void appendKeyterms(List<Keyterm> keyterms) throws AnalysisEngineProcessException {
    appendList(keyterms);
  }

  public static void storeKeyterms(JCas jcas, List<Keyterm> keyterms, boolean overwrite)
          throws AnalysisEngineProcessException {
    if (overwrite) {
      new KeytermList(jcas).setKeyterms(keyterms);
    } else {
      new KeytermList(jcas).appendKeyterms(keyterms);
    }
  }

  @Deprecated
  public static void storeKeyterms(JCas jcas, List<Keyterm> keyterms)
          throws AnalysisEngineProcessException {
    storeKeyterms(jcas, keyterms, true);
  }

  public List<Keyterm> getKeyterms() throws AnalysisEngineProcessException {
    AbstractQuery query = (AbstractQuery) BaseJCasHelper.getFS(jcas, AbstractQuery.type);
    if (query != null) {
      list = query.getConcepts();
      return getList(Keyterm.class);
    } else {
      return new ArrayList<Keyterm>();
    }
  }

  public static List<Keyterm> retrieveKeyterms(JCas jcas) throws AnalysisEngineProcessException {
    return new KeytermList(jcas).getKeyterms();
  }

}