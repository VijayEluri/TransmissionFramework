package org.mmadsen.sim.tf.interfaces;

/**
 * Created by IntelliJ IDEA.
 * User: mark
 * Date: Jun 27, 2010
 * Time: 11:26:05 AM
 * To change this template use File | Settings | File Templates.
 */
public interface IAgent {

    public String getAgentID();

    public void adoptTrait(ITrait trait);

    public void adoptTrait(ITraitDimension dimension, ITrait trait);

    /**
     * Adds a trait dimension to an agent
     * @param dimension
     */

    public void addTraitDimension(ITraitDimension dimension);

}
