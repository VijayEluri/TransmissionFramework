/*
 * Copyright (c) 2011.  Mark E. Madsen <mark@mmadsen.org>
 *
 * This work is licensed under the terms of the Creative Commons-GNU General Public Llicense 2.0, as "non-commercial/sharealike".  You may use, modify, and distribute this software for non-commercial purposes, and you must distribute any modifications under the same license.
 *
 * For detailed license terms, see:
 * http://creativecommons.org/licenses/GPL/2.0/
 */

package org.madsenlab.sim.tf.test.examples.SimpleMoran;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.madsenlab.sim.tf.interfaces.*;
import org.madsenlab.sim.tf.utils.TraitIDComparator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * CLASS DESCRIPTION
 * <p/>
 * User: mark
 * Date: Aug 8, 2010
 * Time: 3:19:51 PM
 */

//@Ignore needed to prevent JUnit from trying to execute test helper classes

@Ignore
public class TraitFrequencyObserver implements ITraitStatisticsObserver<ITraitDimension> {
    private ISimulationModel model;
    private Logger log;
    private Map<ITrait, Double> traitFreqMap;
    private Integer lastTimeIndexUpdated;
    private PrintWriter pw;
    private Map<Integer,Map<ITrait,Double>> histTraitFreq;

    public TraitFrequencyObserver(ISimulationModel m) {
        this.model = m;
        this.log = this.model.getModelLogger(this.getClass());
        this.histTraitFreq = new HashMap<Integer,Map<ITrait, Double>>();

        try{
            this.pw = new PrintWriter(new BufferedWriter(new FileWriter("trait-frequencies-by-time.txt")));
        } catch(IOException ex) {
            log.error("ERROR CREATING TRAIT FREQUENCIES BY TIME LOG FILE");
            System.exit(1);
        }
    }


    public void updateTraitStatistics(ITraitStatistic<ITraitDimension> stat) {
        this.lastTimeIndexUpdated = stat.getTimeIndex();
        ITraitDimension dim = stat.getTarget();
        Map<ITrait,Double> freqMap = new HashMap<ITrait,Double>(dim.getCurGlobalTraitFrequencies());
        this.histTraitFreq.put(this.lastTimeIndexUpdated,freqMap);
    }

    public void perStepAction() {
        log.trace("entering perStepAction");
        //log.trace("histTraitFreq: " + this.histTraitFreq);
        this.printFrequencies();

    }

    public void endSimulationAction() {
        log.trace("entering endSimulationAction");
        this.logFrequencies();
    }

    public void finalizeObservation() {
        log.trace("entering finalizeObservation");
        this.pw.flush();
        this.pw.close();

    }

    private void printFrequencies() {
        Integer time = this.model.getCurrentModelTime();
        StringBuffer sb = prepareFrequencyLogString(time, this.histTraitFreq.get(time));
        log.debug(sb.toString());

    }

    private StringBuffer prepareFrequencyLogString(Integer time, Map<ITrait,Double> freqMap) {
        //log.debug("prepare string: freqMap: " + freqMap);
        StringBuffer sb = new StringBuffer();
        Set<ITrait> keys = freqMap.keySet();
        List<ITrait> sortedKeys = new ArrayList<ITrait>(keys);
        Collections.sort(sortedKeys, new TraitIDComparator());
        sb.append(time);
        for (ITrait aTrait : sortedKeys) {
            sb.append(",");
            sb.append(freqMap.get(aTrait));

        }
        return sb;
    }

    private void logFrequencies() {
        Set<Integer> keys = this.histTraitFreq.keySet();
        List<Integer> sortedKeys = new ArrayList<Integer>(keys);
        Collections.sort(sortedKeys);
        for(Integer time: sortedKeys) {
            Map<ITrait,Double> freqMap = this.histTraitFreq.get(time);
            StringBuffer sb = prepareFrequencyLogString(time, freqMap);
            sb.append('\n');
            this.pw.write(sb.toString());
            this.pw.flush();
        }

    }
}
