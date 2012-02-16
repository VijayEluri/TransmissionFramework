/*
 * Copyright (c) 2012.  Mark E. Madsen <mark@madsenlab.org>
 *
 * This work is licensed under the terms of the Creative Commons-GNU General Public Llicense 2.0, as "non-commercial/sharealike".  You may use, modify, and distribute this software for non-commercial purposes, and you must distribute any modifications under the same license.  
 *
 * For detailed license terms, see:
 * http://creativecommons.org/licenses/GPL/2.0/
 */

package org.madsenlab.sim.tf.analysis;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;
import org.madsenlab.sim.tf.interfaces.ISimulationModel;
import org.madsenlab.sim.tf.interfaces.ITrait;
import org.madsenlab.sim.tf.utils.TraitIDComparator;

import java.io.PrintWriter;
import java.util.*;

/**
 * The task of a TimeAveragedWindowProcessor is to handle sampling at a specific window size (e.g., 50 generations),
 * when handed a trait count update.  It does this by creating a sequence of TraitCountsOverInterval objects with
 * that specific windowsize, passing each TCOI object in sequence the incoming trait counts.  The sequence of TCOI
 * objects then represents the sequence of trait counts for the simulation run, aggregated into blocks of windowsize.
 *
 * There may be, and usually will be, more than one TimeAveragedWindowProcessor receiving the same stream of trait
 * counts, at different window sizes.  To prevent memory explosion, when each TCOI object is "full" (i.e., has received
 * windowsize samples), this class will write the accumulated counts to the per-windowsize log file, along with start
 * and end time indices.  That TCOI object is then destroyed and a new one created.
 *
 * <p/>
 * User: mark
 * Date: 2/15/12
 * Time: 12:58 PM
 */

public class TimeAveragedWindowProcessor {
    private ISimulationModel model;
    private Logger log;
    private Integer windowSize;
    private PrintWriter wsPW;
    private PrintWriter statPW;
    private TraitCountsOverInterval currentAggregateSample;
    private List<Integer> totalTraitsPerWindow;
    
    public TimeAveragedWindowProcessor(ISimulationModel model, Integer windowSize, String logBaseName, String logStatBaseName) {
        this.model = model;
        this.log = this.model.getModelLogger(this.getClass());
        this.windowSize = windowSize;
        StringBuffer sb = new StringBuffer();
        sb.append(logBaseName);
        sb.append(windowSize);
        
        StringBuffer sb2 = new StringBuffer();
        sb2.append(logStatBaseName);
        sb2.append(windowSize);

        this.wsPW = this.model.getLogFileHandler().getFileWriterForPerRunOutput(sb.toString());
        this.statPW = this.model.getLogFileHandler().getFileWriterForPerRunOutput(sb2.toString());
        this.currentAggregateSample = new TraitCountsOverInterval(this.model, this.windowSize);
        this.currentAggregateSample.startCounting(this.model.getCurrentModelTime());

        this.totalTraitsPerWindow = new ArrayList<Integer>();
    }

    public void updateTraitStatistics(Map<ITrait, Integer> countMap) {
        if(this.currentAggregateSample.hasCapacity()) {
            // add the current count map to the current sample
            this.currentAggregateSample.putTraitCountsForTick(countMap);
        } else {
            // No more room in the current sample, so log it, nuke it, and start a new one...
            this.currentAggregateSample.endCounting(this.model.getCurrentModelTime());
            // first log the previous sample
            StringBuffer sb = this.prepareCountLogString(this.currentAggregateSample);
            this.wsPW.write(sb.toString());
            this.wsPW.write("\n");

            // record the total number of traits for stats
            this.totalTraitsPerWindow.add(this.currentAggregateSample.getIntervalCountMap().keySet().size());
            
            
            // destroy the previous sample
            this.currentAggregateSample = null;

            // create a new sample
            this.currentAggregateSample = new TraitCountsOverInterval(this.model, this.windowSize);
            this.currentAggregateSample.startCounting(this.model.getCurrentModelTime());
            // add the current countMap to the new sample
            this.currentAggregateSample.putTraitCountsForTick(countMap);
        }


    }

    public void endSimulationAction() {
        // we're done and write their final logs, even if we have a partial sample, which could happen
        // for the end of the simulation given integer rounding in determining the window sizes.
        StringBuffer sb = this.prepareCountLogString(this.currentAggregateSample);
        this.wsPW.write(sb.toString());
        this.wsPW.write("\n");

        // Now calculate stats
        DescriptiveStatistics stats = new DescriptiveStatistics();
        for (Integer val : this.totalTraitsPerWindow) {
            stats.addValue((double) val);
        }
        log.info("TA Trait Count for Window " + this.windowSize + ": =============");
        log.info(stats.toString());

        this.statPW.write(stats.toString());

    }

    public void finalizeObservation() {
        this.wsPW.flush();
        this.wsPW.close();
        this.statPW.flush();
        this.statPW.close();
    }


    private StringBuffer prepareCountLogString(TraitCountsOverInterval traitCountsOverInterval) {
        Integer numNonZeroTraits = 0;
        Integer totalOfTraitCounts = 0;
        StringBuffer sb = new StringBuffer();
        Map<ITrait,Integer> countMap = traitCountsOverInterval.getIntervalCountMap();
        Set<ITrait> keys = countMap.keySet();
        List<ITrait> sortedKeys = new ArrayList<ITrait>(keys);
        Collections.sort(sortedKeys, new TraitIDComparator());
        sb.append(traitCountsOverInterval.getIntervalStart());
        sb.append(",");
        sb.append(traitCountsOverInterval.getIntervalEnd());
        sb.append(",");
        for (ITrait aTrait : sortedKeys) {
            Integer count = countMap.get(aTrait);
            sb.append(aTrait.getTraitID());
            sb.append(":");
            sb.append(count);
            sb.append(",");
            if (count != 0) {
                numNonZeroTraits++;
            }
            totalOfTraitCounts += count;
        }
        sb.append("tot:");
        sb.append(totalOfTraitCounts);
        sb.append(",numtrait:");
        sb.append(numNonZeroTraits);
        return sb;
    }


    public Integer getWindowSize() {
        return windowSize;
    }
}