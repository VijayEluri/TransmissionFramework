/*
 * Copyright (c) 2011.  Mark E. Madsen <mark@madsenlab.org>
 *
 * This work is licensed under the terms of the Creative Commons-GNU General Public Llicense 2.0, as "non-commercial/sharealike".  You may use, modify, and distribute this software for non-commercial purposes, and you must distribute any modifications under the same license.
 *
 * For detailed license terms, see:
 * http://creativecommons.org/licenses/GPL/2.0/
 */

package org.madsenlab.sim.tf.test;

import atunit.AtUnit;
import atunit.Container;
import atunit.Unit;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Provider;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.madsenlab.sim.tf.interfaces.*;
import org.madsenlab.sim.tf.test.util.AbstractGuiceTestClass;
import org.madsenlab.sim.tf.test.util.TraitCountAccumulatorObserver;
import org.madsenlab.sim.tf.test.util.TraitCountPrinterObserver;

import static org.junit.Assert.assertTrue;


/**
 * Created by IntelliJ IDEA.
 * User: mark
 * Date: Jul 1, 2010
 * Time: 2:21:51 PM
 * To change this template use File | Settings | File Templates.
 */
@RunWith(AtUnit.class)
@Container(Container.Option.GUICE)
public class TraitDimensionObserverTest extends AbstractGuiceTestClass implements Module {
    @Inject
    public Provider<IAgent> agentProvider;
    @Inject
    public Provider<ITrait> traitProvider;
    @Inject
    public Provider<ITraitDimension> dimensionProvider;
    @Inject
    public Provider<IAgentTag> tagProvider;
    @Inject
    public
    ISimulationModel model;
    @Unit
    ITraitDimension unusedDim;  // this makes Junit4 happy
    Logger log;

    ITrait three, five;
    IAgentTag redTag, blueTag;
    Integer expectedThreeRed, expectedFiveBlue;

    @Before
    public void setUp() throws Exception {
        log = model.getModelLogger(this.getClass());
        model.initializeProviders();

    }

    @After
    public void cleanUp() throws Exception {
        model.initializeProviders();
    }


    @Test
    public void testObserverBasedTraitCounting() throws Exception {
        log.info("entering testObserverBasedTraitCounting");
        TraitCountPrinterObserver obs = new TraitCountPrinterObserver(model);
        TraitCountAccumulatorObserver accum = new TraitCountAccumulatorObserver(model);

        ITraitDimension dimension = dimensionProvider.get();
        this.redTag = tagProvider.get();
        redTag.setTagName("redTag");
        this.blueTag = tagProvider.get();
        blueTag.setTagName("blueTag");


        // We're going to add eight traits to a dimension
        for (Integer i = 2; i < 10; i++) {
            // we can't rely on injection here, so just construct them directly.
            ITrait newTrait = traitProvider.get();

            newTrait.setOwningDimension(dimension);
            newTrait.setTraitID(i.toString());
            dimension.addTrait(newTrait);
            newTrait.attach(obs);
            newTrait.attach(accum);

            //log.info("creating trait " + i );


            for (Integer j = 0; j < (i * 2); j++) {
                IAgent newAgent = model.getPopulation().createAgent();
                StringBuffer sb = new StringBuffer();
                sb.append("<" + i + "." + j + ">");
                newAgent.setAgentID(sb.toString());
                redTag.registerAgent(newAgent);
                newTrait.adopt(newAgent);
            }


        }

        Integer adoptionEvents = accum.getAdoptionEventCount();

        assertTrue(adoptionEvents.equals(88));

    }


}
