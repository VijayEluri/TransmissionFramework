/*
 * Copyright (c) 2013.  Mark E. Madsen <mark@madsenlab.org>
 *
 * This work is licensed under the terms of the Creative Commons-GNU General Public Llicense 2.0, as "non-commercial/sharealike".  You may use, modify, and distribute this software for non-commercial purposes, and you must distribute any modifications under the same license.
 *
 * For detailed license terms, see:
 * http://creativecommons.org/licenses/GPL/2.0/
 */

package org.madsenlab.sim.tf.interfaces;


import org.madsenlab.sim.tf.enums.AgentTagType;
import org.madsenlab.sim.tf.enums.TraitCopyingMode;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: mark
 * Date: Jun 27, 2010
 * Time: 11:26:05 AM
 * To change this template use File | Settings | File Templates.
 */
public interface IAgent {


    /* Agents may not be in a consistent state until initialization is complete, so certain checks are bypassed until then*/
    public void setAgentInitialized(Boolean state);

    public void setSimulationModel(ISimulationModel m);

    public String getAgentID();

    public void setAgentID(String id);

    /*  This defines the number of "loci" or distinct trait dimensions that each agent will hold */
    public void setNumTraitDimensionsExpected(Integer numTraitDimensionsExpected);

    /* Trait adoption methods */

    public void adoptTrait(ITrait trait);

    public void adoptTrait(ITraitDimension dimension, ITrait trait);

    public void unadoptTrait(ITrait trait);

    public void unadoptTrait(ITraitDimension dimension, ITrait trait);


    /* Tag handling methods */

    public void addTag(IAgentTag tag);

    public void removeTag(IAgentTag tag);

    public Set<IAgentTag> getAgentTags();

    public Set<IAgentTag> getAgentTagsMatchingType(AgentTagType type);

    public boolean hasTag(IAgentTag tag);

    /* Interaction and Observation Rule related methods */

    public void addActionRule(IActionRule rule);

    public void addActionRuleList(List<IActionRule> ruleList);

    public void fireRules();


    // Not sure this interaction-versus-observation rule distinction should stand....?


    /* Adoption related methods */


    public Map<ITraitDimension, ITrait> getCurrentlyAdoptedDimensionsAndTraits();

    public Map<ITraitDimension, ITrait> getPreviousStepAdoptedDimensionsAndTraits();

    public Set<ITrait> getCurrentlyAdoptedTraits();

    public ITrait getCurrentlyAdoptedTraitForDimension(ITraitDimension dim);

    public ITrait getPreviouslyAdoptedTraitForDimension(ITraitDimension dim);

    public void savePreviousStepTraits();

    public Set<ITrait> getPreviousStepAdoptedTraits();

    public ITrait getRandomTraitFromAgent(TraitCopyingMode mode);

    public ITrait getTraitFromDimensionFromAgent(TraitCopyingMode mode, ITraitDimension dim);


    ITraitDimension getRandomTraitDimensionFromAgent();
}
