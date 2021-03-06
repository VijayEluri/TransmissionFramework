/*
 * Copyright (c) 2013.  Mark E. Madsen <mark@madsenlab.org>
 *
 * This work is licensed under the terms of the Creative Commons-GNU General Public Llicense 2.0, as "non-commercial/sharealike".  You may use, modify, and distribute this software for non-commercial purposes, and you must distribute any modifications under the same license.
 *
 * For detailed license terms, see:
 * http://creativecommons.org/licenses/GPL/2.0/
 */

package org.madsenlab.sim.tf.interfaces;

import org.apache.commons.collections.Closure;

import java.util.Map;

/**
 * CLASS DESCRIPTION
 * <p/>
 * User: mark
 * Date: 9/6/11
 * Time: 10:43 AM
 */

public interface IActionRule extends Closure {
    public void ruleBody(Object o);

    String getRuleName();

    void setRuleName(String ruleName);

    void setRuleDescription(String ruleDescription);

    String getRuleDescription();

    public void registerSubRule(IActionRule rule);

    public void deregisterSubRule(IActionRule rule);

    public void setParameters(Map<String, String> parameters);
}
