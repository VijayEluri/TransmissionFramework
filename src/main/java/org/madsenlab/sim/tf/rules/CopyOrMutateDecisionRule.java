/*
 * Copyright (c) 2013.  Mark E. Madsen <mark@madsenlab.org>
 *
 * This work is licensed under the terms of the Creative Commons-GNU General Public Llicense 2.0, as "non-commercial/sharealike".  You may use, modify, and distribute this software for non-commercial purposes, and you must distribute any modifications under the same license.
 *
 * For detailed license terms, see:
 * http://creativecommons.org/licenses/GPL/2.0/
 */

package org.madsenlab.sim.tf.rules;

import org.madsenlab.sim.tf.interfaces.*;
import org.madsenlab.sim.tf.enums.TraitCopyingMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * CLASS DESCRIPTION
 * <p/>
 * User: mark
 * Date: 4/11/11
 * Time: 10:15 AM
 */

public class CopyOrMutateDecisionRule extends AbstractInteractionRule implements ICopyingRule {
    private List<IActionRule> copyingRuleList;
    private List<IActionRule> mutationRuleList;
    private Double mutationRate;
    private Map<String, String> parameterMap;

    public CopyOrMutateDecisionRule(ISimulationModel m) {
        this.model = m;
        this.log = model.getModelLogger(this.getClass());

        this.copyingRuleList = new ArrayList<IActionRule>();
        this.mutationRuleList = new ArrayList<IActionRule>();

        this.setRuleName("CopyOrMutateDecisionRule");
        this.setRuleDescription("Ensure that either copying or mutation happens in a single time step in a continuous-time simulation");
    }

    @Override
    public void setParameters(Map<String, String> parameters) {
        this.parameterMap = parameters;
    }

    public void ruleBody(Object o) {
        log.trace("entering rule body for: " + this.getRuleName());
        this.mutationRate = Double.parseDouble(this.parameterMap.get("innovationrate"));


        // Generate a random double between 0 and 1, if this value is less than the mutation rate,
        // a mutation "event" has occurred.  If not, the rule body does nothing.
        Double draw = this.model.getUniformDouble();
        if (draw < this.mutationRate) {
            // fire the mutation rule stack
            log.debug("mutation occurred with random draw: " + draw + " < rate: " + this.mutationRate);
            for (IActionRule rule : this.mutationRuleList) {
                rule.execute(o);
            }
        } else {
            // fire the copying rule stack
            log.debug("no mutation - firing copying rule stack");
            for (IActionRule rule : this.copyingRuleList) {
                rule.execute(o);
            }
        }
    }

    public void registerSubRule(IActionRule rule) {
        if (rule instanceof ICopyingRule) {
            this.copyingRuleList.add(rule);
        } else if (rule instanceof IMutationRule) {
            this.mutationRuleList.add(rule);
        } else {
            log.error("FATAL: Rule object being registered in CopyOrMutateDecisionRule of unknown rule type" + rule.getRuleDescription());
            System.exit(1);
        }

    }

    public void deregisterSubRule(IActionRule rule) {
        if (rule instanceof ICopyingRule) {
            this.copyingRuleList.remove(rule);
        } else if (rule instanceof IMutationRule) {
            this.mutationRuleList.remove(rule);
        }
    }

    @Override
    public void setTraitCopyingMode(TraitCopyingMode mode) {

    }
}
