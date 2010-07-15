/*
 * Copyright (c) 2010.  Mark E. Madsen <mark@mmadsen.org>
 *
 * This work is licensed under the terms of the Creative Commons-GNU General Public Llicense 2.0, as "non-commercial/sharealike".  You may use, modify, and distribute this software for non-commercial purposes, and you must distribute any modifications under the same license.
 *
 * For detailed license terms, see:
 * http://creativecommons.org/licenses/GPL/2.0/
 */

package org.mmadsen.sim.tf.interfaces;

/**
 * Created by IntelliJ IDEA.
 * User: mark
 * Date: Jun 27, 2010
 * Time: 11:26:05 AM
 * To change this template use File | Settings | File Templates.
 */
public interface IAgent {

    public void setSimulationModel(ISimulationModel m);

    public String getAgentID();

    public void setAgentID(String id);

    public void adoptTrait(ITrait trait);

    public void adoptTrait(ITraitDimension dimension, ITrait trait);

    /**
     * Adds a trait dimension to an agent
     * @param dimension
     */

    public void addTraitDimension(ITraitDimension dimension);

}
