/*
 * Copyright (c) 2011.  Mark E. Madsen <mark@madsenlab.org>
 *
 * This work is licensed under the terms of the Creative Commons-GNU General Public Llicense 2.0, as "non-commercial/sharealike".  You may use, modify, and distribute this software for non-commercial purposes, and you must distribute any modifications under the same license.
 *
 * For detailed license terms, see:
 * http://creativecommons.org/licenses/GPL/2.0/
 */

package org.madsenlab.sim.tf.test.util;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.madsenlab.sim.tf.interfaces.ISimulationModel;
import org.madsenlab.sim.tf.interfaces.ITraitDimension;
import org.madsenlab.sim.tf.interfaces.ITraitStatistic;
import org.madsenlab.sim.tf.interfaces.ITraitStatisticsObserver;

/**
 * The only job of this observer is to receive notifications of trait
 * adoption events, and count these adoption events, so this total can be
 * queried later.
 * <p/>
 * <p/>
 * User: mark
 * Date: Aug 8, 2010
 * Time: 3:19:51 PM
 */

//@Ignore needed to prevent JUnit from trying to execute test helper classes

@Ignore
public class TraitCountAccumulatorObserver implements ITraitStatisticsObserver<ITraitDimension> {
    private ISimulationModel model;
    private Logger log;
    private Integer adoptionEvents;

    public TraitCountAccumulatorObserver(ISimulationModel m) {
        this.model = m;
        this.log = this.model.getModelLogger(this.getClass());
        this.adoptionEvents = 0;

    }

    public Integer getAdoptionEventCount() {
        return this.adoptionEvents;
    }

    public void updateTraitStatistics(ITraitStatistic<ITraitDimension> stat) {
        this.adoptionEvents++;

    }

    public void perStepAction() {
        Integer timeIndex = this.model.getCurrentModelTime();
        log.trace("Time: " + timeIndex + " Cum. Events: " + this.adoptionEvents);
    }

    public void endSimulationAction() {

    }

    public void finalizeObservation() {

    }
}
