/**
 * Copyright 2019 ISTAT
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence. You may
 * obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl5
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * Licence for the specific language governing permissions and limitations under
 * the Licence.
 *
 * @author Francesco Amato <framato @ istat.it>
 * @author Mauro Bruno <mbruno @ istat.it>
 * @version 0.1.1
 */
/**
 * 
 */
package it.istat.is2.catalogue.relais.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import it.istat.is2.catalogue.relais.decision.contingencyTable.BlockPatternFreqVector;
import it.istat.is2.catalogue.relais.matchingVariables.MatchingVariables;
import it.istat.is2.catalogue.relais.metrics.DiceSimilarity;
import it.istat.is2.catalogue.relais.metrics.Jaro;
import it.istat.is2.catalogue.relais.metrics.JaroWinkler;
import it.istat.is2.catalogue.relais.metrics.Levenshtein;
import it.istat.is2.catalogue.relais.metrics.QGramsDistance;
import it.istat.is2.catalogue.relais.metrics.Soundex;
import it.istat.is2.catalogue.relais.metrics.added.NumericComparison;
import it.istat.is2.catalogue.relais.metrics.added.NumericEuclideanDistance;
import it.istat.is2.catalogue.relais.metrics.added.QGramsInclusion;
import it.istat.is2.catalogue.relais.metrics.added.WindowEquality;
import it.istat.is2.catalogue.relais.metrics.dataStructure.MetricMatchingVariable;
import it.istat.is2.catalogue.relais.metrics.dataStructure.MetricMatchingVariableVector;
import it.istat.is2.catalogue.relais.metrics.utility.AbstractStringMetric;
import it.istat.is2.catalogue.relais.project.ReconciledSchema;
import lombok.Data;

/**
 * @author framato
 *
 */
@Data
@Service
public class ContingencyService {
	private final int DIMMAX = 100000;
	private String blockingKey;
	private MetricMatchingVariableVector metricMatchingVariableVector;
	private BlockPatternFreqVector bpfv;
	private int numVar;
	private int dim;
	private int[][] combinations;
	private ReconciledSchema rsc;
	private AbstractStringMetric[] metrics;

	
	public void init() {
		metricMatchingVariableVector = new MetricMatchingVariableVector();
		MetricMatchingVariable mm1 = new MetricMatchingVariable("SURNAME", "da_SURNAME", "db_SURNAME", "Jaro", 0.8, 0);
		MetricMatchingVariable mm2 = new MetricMatchingVariable("NAME", "da_NAME", "db_NAME", "Jaro", 0.8, 0);
		MetricMatchingVariable mm3 = new MetricMatchingVariable("LASTCODE", "da_LASTCODE", "db_LASTCODE", "Jaro", 0.8, 0);
		// MetricMatchingVariable mm3=new
		// MetricMatchingVariable("LASTCODE","da_LASTCODE","db_LASTCODE","Equality",1,0);
		metricMatchingVariableVector.add(mm1);
		metricMatchingVariableVector.add(mm2);
		metricMatchingVariableVector.add(mm3);
		this.numVar = metricMatchingVariableVector.size();

		metrics = new AbstractStringMetric[numVar];

		for (int ind = 0; ind < numVar; ind++) {
			if (metricMatchingVariableVector.get(ind).getComparisonFunction().equals("Equality"))
				metrics[ind] = null;
			else if (metricMatchingVariableVector.get(ind).getComparisonFunction().equals("Jaro"))
				metrics[ind] = new Jaro();
			else if (metricMatchingVariableVector.get(ind).getComparisonFunction().equals("Dice"))
				metrics[ind] = new DiceSimilarity();
			else if (metricMatchingVariableVector.get(ind).getComparisonFunction().equals("JaroWinkler"))
				metrics[ind] = new JaroWinkler();
			else if (metricMatchingVariableVector.get(ind).getComparisonFunction().equals("Levenshtein"))
				metrics[ind] = new Levenshtein();
			else if (metricMatchingVariableVector.get(ind).getComparisonFunction().equals("3Grams"))
				metrics[ind] = new QGramsDistance();
			else if (metricMatchingVariableVector.get(ind).getComparisonFunction().equals("Soundex"))
				metrics[ind] = new Soundex();
			else if (metricMatchingVariableVector.get(ind).getComparisonFunction().equals("NumericComparison"))
				metrics[ind] = new NumericComparison();
			else if (metricMatchingVariableVector.get(ind).getComparisonFunction().equals("NumericEuclideanDistance"))
				metrics[ind] = new NumericEuclideanDistance();
			else if (metricMatchingVariableVector.get(ind).getComparisonFunction().equals("WindowEquality"))
				metrics[ind] = new WindowEquality(metricMatchingVariableVector.get(ind));
			else if (metricMatchingVariableVector.get(ind).getComparisonFunction().equals("Inclusion3Grams"))
				metrics[ind] = new QGramsInclusion();
		}

	}


	/**
	 * @param valuesI
	 * @return
	 */
	public String getPattern(Map<String, String> valuesI) {
		// TODO Auto-generated method stub
		 String pattern="";
			
          /* evaluation of patternd */
 
		 
           for (int ii=0; ii< numVar; ii++) {
        	   MetricMatchingVariable   metricMatchingVariable =  metricMatchingVariableVector.get(ii);
            String matchingVariableNameVariableA=valuesI.get(metricMatchingVariable.getMatchingVariableNameVariableA());
            String matchingVariableNameVariableB=valuesI.get(metricMatchingVariable.getMatchingVariableNameVariableB());
        	   
        	   if (matchingVariableNameVariableA==null || matchingVariableNameVariableB==null || matchingVariableNameVariableA.equals("")) {
                                                   
               pattern=pattern+"0";
              }
	      //Equality
	       else if (metrics[ii]==null) {
		   if (matchingVariableNameVariableA.equals(matchingVariableNameVariableB))
			pattern=pattern+"1";
		   else
			pattern=pattern+"0";
	       }
	       else {
                  
		   if (metrics[ii].getSimilarity(matchingVariableNameVariableA,matchingVariableNameVariableB)>=metricMatchingVariable.getMetricThreshold())
			pattern=pattern+"1";
		   else
			pattern=pattern+"0";
	       }
           }			
		return pattern;
	}

}