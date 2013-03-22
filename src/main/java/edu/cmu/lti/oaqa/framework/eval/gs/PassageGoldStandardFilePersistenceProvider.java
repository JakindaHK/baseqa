package edu.cmu.lti.oaqa.framework.eval.gs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.jcas.cas.FSArray;

import org.oaqa.model.Passage;
import org.oaqa.model.Search;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import edu.cmu.lti.oaqa.framework.eval.gs.DatasetSequenceId;
import edu.cmu.lti.oaqa.framework.eval.gs.GoldStandardSpan;

/**
 * A gold standard persistence provider that can read a file containing gold standard annotations
 * into the memory, and stored in a map structure, and populate gold standard labels for each input
 * element.
 * 
 * Required parameter: DataSet, LineSyntax (specifying what the line syntax of the gold standard
 * annotation, e.g. "(\d+)\s+(\d+)\s+(\d+)" represent the sequence id, begin and end are separated
 * by white-spaces), and PathPattern (refer to the PathMatchingResourcePatternResolver in the spring
 * framework for more detail)
 * 
 * @author Zi Yang <ziy@cs.cmu.edu>
 * 
 */
public class PassageGoldStandardFilePersistenceProvider extends
        AbstractGoldStandardPersistenceProvider {

  private static final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

  private Map<DatasetSequenceId, List<GoldStandardSpan>> id2gsSpans = new HashMap<DatasetSequenceId, List<GoldStandardSpan>>();

  @Override
  public boolean initialize(ResourceSpecifier aSpecifier, Map<String, Object> aAdditionalParams)
          throws ResourceInitializationException {
    boolean ret = super.initialize(aSpecifier, aAdditionalParams);
    String dataset = (String) getParameterValue("DataSet");
    Pattern lineSyntaxPattern = Pattern.compile((String) getParameterValue("LineSyntax"));
    try {
      Resource[] resources = resolver.getResources((String) getParameterValue("PathPattern"));
      for (Resource resource : resources) {
        Scanner scanner = new Scanner(resource.getInputStream());
        while (scanner.findInLine(lineSyntaxPattern) != null) {
          MatchResult result = scanner.match();
          DatasetSequenceId id = new DatasetSequenceId(dataset, result.group(1));
          if (!id2gsSpans.containsKey(id)) {
            id2gsSpans.put(id, new ArrayList<GoldStandardSpan>());
          }
          GoldStandardSpan annotation = new GoldStandardSpan(result.group(2),
                  Integer.parseInt(result.group(3)), Integer.parseInt(result.group(4)),
                  result.group(5));
          id2gsSpans.get(id).add(annotation);
          if (scanner.hasNextLine()) {
            scanner.nextLine();
          } else {
            break;
          }
        }
        scanner.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return ret;
  }

  @Override
  public void populateRetrievalGS(String dataset, String sequenceId, JCas gsView) {
    List<Passage> gsAnnotations = new ArrayList<Passage>();
    List<GoldStandardSpan> gsSpans = id2gsSpans.get(new DatasetSequenceId(dataset, sequenceId));
    if (gsSpans != null) {
      for (GoldStandardSpan gsSpan : gsSpans) {
        Passage passage = new Passage(gsView);
        passage.setUri(gsSpan.docId);
        passage.setBegin(gsSpan.begin);
        passage.setEnd(gsSpan.end);
        passage.setAspects(gsSpan.aspects);
        gsAnnotations.add(passage);
      }
    }
    
    if (!gsAnnotations.isEmpty()) {
      FSArray hitList = new FSArray(gsView, gsAnnotations.size());
      for (int i = 0; i < gsAnnotations.size(); i++) {
        hitList.set(i, gsAnnotations.get(i));
      }
      Search search = new Search(gsView);
      search.setHitList(hitList);
      search.addToIndexes();
    }
  }
}
