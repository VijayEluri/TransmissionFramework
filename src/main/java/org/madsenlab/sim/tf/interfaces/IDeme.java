/*
 * Copyright (c) 2013.  Mark E. Madsen <mark@madsenlab.org>
 *
 * This work is licensed under the terms of the Creative Commons-GNU General Public Llicense 2.0, as "non-commercial/sharealike".  You may use, modify, and distribute this software for non-commercial purposes, and you must distribute any modifications under the same license.
 *
 * For detailed license terms, see:
 * http://creativecommons.org/licenses/GPL/2.0/
 */

package org.madsenlab.sim.tf.interfaces;

import org.madsenlab.sim.tf.utils.AgentPredicate;
import org.madsenlab.sim.tf.enums.TraitCopyingMode;

import java.util.List;

/**
 * Subpopulations in TransmissionFramework are called "demes," and are not represented by persistent
 * objects but rather are "projections" of the population (by tag, by geography, by trait group, etc).
 * Most operations one would routinely perform on a group of agents (e.g., selecting a random agent,
 * selecting a subset of agents which have a particular trait or tag) are done through the IDeme
 * abstraction.
 * <p/>
 * User: mark
 * Date: Sep 26, 2010
 * Time: 8:57:34 AM
 */

public interface IDeme {
    /**
     * Takes a passed List<IAgent> and makes it the agentList for this deme.  This shouldn't be
     * called by user code much, if at all, but it's an essential method for query methods which
     * take a deme, apply a query or filter, and construct another deme of results.
     */

    void setAgentList(List<IAgent> agentList);

    /**
     * Returns a typed List of agents currently in the population.  This list is a shallow
     * clone of the internal list of agents, so modifying the returned list does not affect
     * the underlying population's list of agents.
     *
     * @return
     */
    List<IAgent> getAgents();

    /**
     * Shorthand query interface, returning a subpopulation of agents which currently possess the
     * given tag.  This is implemented internally by creating a Predicate which returns TRUE if
     * an agent possesses a given tag, and then passing this predicate to getDemeMatchingPredicate().
     *
     * @param tag
     * @return
     * @see #getDemeMatchingPredicate
     */
    IDeme getDemeForTag(IAgentTag tag);

    /**
     * Returns one agent from the given population, chosen at random from a uniform
     * distribution.
     *
     * @return agent
     */
    IAgent getAgentAtRandom();

    /**
     * Returns a typed list of agents currently in the population, in a randomized order each time
     * it is called.  This allows one to then perform actions on all agents, but ensure that results are
     * not dependent upon the single fixed order that the underlying Collections framework will return
     * the agents in.  This is meant for models like Wright-Fisher, where we do something to all agents
     * at a step.
     *
     * @return agentListShuffled
     */

    List<IAgent> getAgentsShuffledOrder();

    /**
     * Query interface, returning an IPopulation instance which is populated by the agents for which
     * a Predicate (whether simple or compound/chained) returns TRUE.
     *
     * @param pred
     * @return subpopulation
     */
    IDeme getDemeMatchingPredicate(AgentPredicate pred);

    /**
     * Returns the current size of the agent population for a given simulation instance.
     * This value is not assumed to be constant over the lifetime of a simulation run,
     * to allow models with population dynamics.  The value is guaranteed, however, to be
     * constant for a model "tick", following the usual stochastic model convention that
     * the probability of two events in the same infinitesimal interval is O(dt^2).
     *
     * @return popSize The number of individual agents in the simulated population at the current time
     */

    Integer getCurrentPopulationSize();

    /**
     * Simple method to indicate whether a query resulting in a deme actually had any results,
     * so that we don't have issues with trying to do processing on empty lists.
     *
     * @return
     */
    Boolean hasMemberAgents();

    /**
     * Returns a sample of agents of size N from the deme, taken without replacement.  In other words, no agent
     * is represented more than once in the sample.
     *
     * @return List<IAgent>
     */
    List<IAgent> getRandomAgentSampleWithoutReplacement(Integer size);


    /**
     * Returns the least frequent trait from the agents in this deme.  This can either be from a
     * snapshot of the previous generation, or the current one, depending upon the type of copying dynamics
     * involved (WF vs. Moran)
     *
     * @param mode
     * @return ITrait
     */
    public ITrait getLeastFrequentTrait(TraitCopyingMode mode);

    /**
     * Returns the most frequent trait from the agents in this deme.  This can either be from a
     * snapshot of the previous generation, or the current one, depending upon the type of copying dynamics
     * involved (WF vs. Moran)
     *
     * @param mode
     * @return ITrait
     */
    public ITrait getMostFrequentTrait(TraitCopyingMode mode);
}
