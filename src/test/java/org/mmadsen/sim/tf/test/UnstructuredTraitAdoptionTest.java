/*
 * Copyright (c) 2010.  Mark E. Madsen <mark@mmadsen.org>
 *
 * This work is licensed under the terms of the Creative Commons-GNU General Public Llicense 2.0, as "non-commercial/sharealike".  You may use, modify, and distribute this software for non-commercial purposes, and you must distribute any modifications under the same license.
 *
 * For detailed license terms, see:
 * http://creativecommons.org/licenses/GPL/2.0/
 */

package org.mmadsen.sim.tf.test;

import static org.junit.Assert.*;

import com.google.inject.*;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import atunit.*;

import org.junit.runner.RunWith;
import org.mmadsen.sim.tf.agent.SimpleAgentProvider;
import org.mmadsen.sim.tf.interfaces.IAgent;
import org.mmadsen.sim.tf.interfaces.ISimulationModel;
import org.mmadsen.sim.tf.interfaces.ITrait;
import org.mmadsen.sim.tf.interfaces.ITraitDimension;
import org.mmadsen.sim.tf.traits.UnstructuredTrait;
import org.mmadsen.sim.tf.traits.UnstructuredTraitDimensionProvider;
import org.mmadsen.sim.tf.traits.UnstructuredTraitProvider;


/**
 * Created by IntelliJ IDEA.
 * User: mark
 * Date: Jul 2, 2010
 * Time: 4:26:25 PM
 * To change this template use File | Settings | File Templates.
 */

@RunWith(AtUnit.class)
@Container(Container.Option.GUICE)
public class UnstructuredTraitAdoptionTest implements Module  {
    @Inject
    @Unit public ITrait trait;
    @Inject public ISimulationModel model;
    @Inject public Provider<IAgent> agentProvider;
    Logger log;

    @Before
    public void setup() {
        log = model.getModelLogger(this.getClass());
    }

    @Test
    public void simpleAdoption() {
        log.info("Entering simpleAdoption test");
        IAgent agent = agentProvider.get();
        trait.setTraitID("TestTrait1");
        agent.setAgentID("TestAgent1");
        trait.adopt(agent);
        Integer adoptCount = trait.getCurrentAdoptionCount();
        log.info("expecting count:  1 observed count: " + adoptCount);
        assertTrue(adoptCount == 1);
        log.info("Exiting simpleAdoption test");
    }

    @Test
    public void multipleAdoption() {
        log.info("Entering multipleAdoption test, adoption by 15 agents");
        IAgent agent = agentProvider.get();
        trait.adopt(agent);
        for(Integer i = 2; i < 15; i++) {
            IAgent newAgent = agentProvider.get();
            newAgent.setAgentID(i.toString());
            trait.adopt(newAgent);
        }
        IAgent lastAgent = agentProvider.get();
        lastAgent.setAgentID("15");
        trait.adopt(lastAgent);
        log.info("expecting: 15 observed: " + trait.getCurrentAdoptionCount());
        assertTrue(trait.getCurrentAdoptionCount() == 15);

        log.info("now 2 agents unadopt the trait");
        trait.unadopt(agent);
        trait.unadopt(lastAgent);
        log.info("expecting: 13 observed: " + trait.getCurrentAdoptionCount());
        assertTrue(trait.getCurrentAdoptionCount() == 13);
        log.info("Exiting multipleAdoption test");
    }


    public void configure(Binder binder) {
        binder.bind(ITrait.class).toProvider(UnstructuredTraitProvider.class);
        binder.bind(ITraitDimension.class).toProvider(UnstructuredTraitDimensionProvider.class);
        binder.bind(IAgent.class).toProvider(SimpleAgentProvider.class);
        binder.bind(ISimulationModel.class).to(SimulationModelFixture.class);

    }
}

