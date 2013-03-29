/*
 * Copyright (c) 2013.  Mark E. Madsen <mark@madsenlab.org>
 *
 * This work is licensed under the terms of the Creative Commons-GNU General Public Llicense 2.0, as "non-commercial/sharealike".  You may use, modify, and distribute this software for non-commercial purposes, and you must distribute any modifications under the same license.
 *
 * For detailed license terms, see:
 * http://creativecommons.org/licenses/GPL/2.0/
 */

package org.madsenlab.sim.tf.config;

import java.util.ArrayList;
import java.util.List;

/**
 * CLASS DESCRIPTION
 * <p/>
 * User: mark
 * Date: 2/28/13
 * Time: 2:05 PM
 */

public class PopulationConfiguration {
    private String agentclass;
    private String topologyclass;
    private String builderclass;
    private int numagents;
    private List<RulesetConfiguration> rulesets;

    public PopulationConfiguration() {
        this.rulesets = new ArrayList<RulesetConfiguration>();
    }

    public String getAgentclass() {
        return agentclass;
    }

    public void setAgentclass(String agentclass) {
        this.agentclass = agentclass;
    }

    public String getTopologyclass() {
        return topologyclass;
    }

    public void setTopologyclass(String topologyclass) {
        this.topologyclass = topologyclass;
    }

    public String getBuilderclass() {
        return builderclass;
    }

    public void setBuilderclass(String builderclass) {
        this.builderclass = builderclass;
    }

    public int getNumagents() {
        return numagents;
    }

    public void setNumagents(int numagents) {
        this.numagents = numagents;
    }

    public List<RulesetConfiguration> getRulesets() {
        return rulesets;
    }

    public void addRuleset(RulesetConfiguration ruleset) {
        this.rulesets.add(ruleset);
    }
}