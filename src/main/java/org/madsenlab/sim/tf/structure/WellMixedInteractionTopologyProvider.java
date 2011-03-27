/*
 * Copyright (c) 2011.  Mark E. Madsen <mark@mmadsen.org>
 *
 * This work is licensed under the terms of the Creative Commons-GNU General Public Llicense 2.0, as "non-commercial/sharealike".  You may use, modify, and distribute this software for non-commercial purposes, and you must distribute any modifications under the same license.
 *
 * For detailed license terms, see:
 * http://creativecommons.org/licenses/GPL/2.0/
 */

package org.madsenlab.sim.tf.structure;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.madsenlab.sim.tf.interfaces.*;


/**
 * CLASS DESCRIPTION
 * <p/>
 * User: mark
 * Date: 12/11/10
 * Time: 3:28 PM
 */


public class WellMixedInteractionTopologyProvider implements Provider<IInteractionTopology> {
    @Inject
    public ISimulationModel model;


    public IInteractionTopology get() {
        WellMixedInteractionTopology topology = new WellMixedInteractionTopology();
        topology.initialize(model);
        return topology;
    }
}
