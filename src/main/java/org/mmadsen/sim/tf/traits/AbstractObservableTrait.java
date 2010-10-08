/*
 * Copyright (c) 2010.  Mark E. Madsen <mark@mmadsen.org>
 *
 * This work is licensed under the terms of the Creative Commons-GNU General Public Llicense 2.0, as "non-commercial/sharealike".  You may use, modify, and distribute this software for non-commercial purposes, and you must distribute any modifications under the same license.
 *
 * For detailed license terms, see:
 * http://creativecommons.org/licenses/GPL/2.0/
 */

package org.mmadsen.sim.tf.traits;

import org.apache.log4j.Logger;
import org.mmadsen.sim.tf.interfaces.*;
import org.perfectjpattern.core.api.behavioral.observer.IObserver;
import org.perfectjpattern.core.api.behavioral.observer.ISubject;

import java.util.List;

/**
 * CLASS DESCRIPTION
 * <p/>
 * User: mark
 * Date: Aug 4, 2010
 * Time: 10:59:12 AM
 */

public abstract class AbstractObservableTrait implements ITrait
{
    protected List<ITraitStatisticsObserver> observers;
    protected ISimulationModel model;
    protected Logger log;

    public void attach(ITraitStatisticsObserver obs) {
        synchronized(this.observers) {
            this.observers.add(obs);
        }
    }

    public void detach(ITraitStatisticsObserver obs) {
        synchronized(this.observers) {
            this.observers.remove(obs);
        }

    }

    public Integer getNumObservers() {
        return this.observers.size();
    }

    public void notifyObservers() {
        log.debug("entering notifyObservers");

        ITraitStatistic stat = this.getChangeStatistic();
        for(ITraitStatisticsObserver obs: this.observers) {
            obs.updateTraitStatistics(stat);
        }
        
    }

    public abstract ITraitStatistic getChangeStatistic();

/*
    public void notifyTraitFrequencyObservers(TraitFrequencyData tfd) {
        for(IObserver<TraitFrequencyData> obs: this.traitObservers) {
            obs.update(tfd);
        }
    }

    public void notifyTraitObservers()

    public void notifyTraitTagObservers()

    protected void notifyTraitObservers() {
        TraitFrequencyData tfd = new TraitFrequencyData(this, this.getCurrentAdoptionCount(), this.model.getCurrentModelTime());
        this.notifyTraitObservers(tfd);
    }

    protected void notifyTraitTagObservers(IAgentTag tag) {
        TraitTagFrequencyData ttp = new TraitTagFrequencyData<ITrait, IAgentTag>(this, tag);
        TraitFrequencyData tfd = new TraitFrequencyData(ttp, this.getCurrentAdoptionCountForTag(tag), model.getCurrentModelTime());
        this.notifyObservers(tfd);
    }*/
}