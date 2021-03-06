/*
 * Copyright (c) 2013.  Mark E. Madsen <mark@madsenlab.org>
 *
 * This work is licensed under the terms of the Creative Commons-GNU General Public Llicense 2.0, as "non-commercial/sharealike".  You may use, modify, and distribute this software for non-commercial purposes, and you must distribute any modifications under the same license.
 *
 * For detailed license terms, see:
 * http://creativecommons.org/licenses/GPL/2.0/
 */

package org.madsenlab.sim.tf.test.util;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.madsenlab.sim.tf.interfaces.*;
import org.madsenlab.sim.tf.utils.TraitIDComparator;

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
public class TraitCountPrinterObserver implements IStatisticsObserver<ITraitDimension> {
    private ISimulationModel model;
    private Logger log;
    private Map<ITrait, Integer> traitCountMap;

    public TraitCountPrinterObserver(ISimulationModel m) {
        this.model = m;
        this.log = this.model.getModelLogger(this.getClass());
    }


    @Override
    public void setParameterMap(Map<String, String> parameterMap) {

    }

    public void updateStatistics(IStatistic<ITraitDimension> stat) {
        Integer timeIndex = stat.getTimeIndex();
        ITraitDimension dim = stat.getTarget();
        this.traitCountMap = dim.getCurGlobalTraitCounts();


    }

    public void perStepAction() {
        StringBuffer sb = new StringBuffer();
        Set<ITrait> keys = this.traitCountMap.keySet();
        List<ITrait> sortedKeys = new ArrayList<ITrait>(keys);
        Collections.sort(sortedKeys, new TraitIDComparator());
        for (ITrait aTrait : sortedKeys) {
            sb.append("[" + aTrait.getTraitID() + "] ");
            sb.append(this.traitCountMap.get(aTrait));
            sb.append(" ");
        }
        Integer timeIndex = this.model.getCurrentModelTime();
        log.info("Time: " + timeIndex + " Counts: " + sb.toString());

    }

    public void endSimulationAction() {

    }

    public void finalizeObservation() {

    }
}
