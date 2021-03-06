/*
 * Copyright (c) 2013.  Mark E. Madsen <mark@madsenlab.org>
 *
 * This work is licensed under the terms of the Creative Commons-GNU General Public Llicense 2.0, as "non-commercial/sharealike".  You may use, modify, and distribute this software for non-commercial purposes, and you must distribute any modifications under the same license.
 *
 * For detailed license terms, see:
 * http://creativecommons.org/licenses/GPL/2.0/
 */

package org.madsenlab.sim.tf.models.SimpleMoran;

import org.apache.commons.cli.*;
import org.madsenlab.sim.tf.models.MoranDynamics;
import org.madsenlab.sim.tf.observers.EwensSampleFullPopulationObserver;
import org.madsenlab.sim.tf.observers.GlobalTraitCountObserver;
import org.madsenlab.sim.tf.observers.GlobalTraitFrequencyObserver;
import org.madsenlab.sim.tf.observers.GlobalTraitLifetimeObserver;
import org.madsenlab.sim.tf.config.GlobalModelConfiguration;
import org.madsenlab.sim.tf.interfaces.*;
import org.madsenlab.sim.tf.models.AbstractSimModel;
import org.madsenlab.sim.tf.rules.CopyOrMutateDecisionRule;
import org.madsenlab.sim.tf.rules.FiniteKAllelesMutationRule;
import org.madsenlab.sim.tf.rules.InfiniteAllelesMutationRule;
import org.madsenlab.sim.tf.rules.RandomCopyNeighborSingleDimensionRule;
import org.madsenlab.sim.tf.traits.InfiniteAllelesIntegerTraitFactory;
import org.madsenlab.sim.tf.enums.GenerationDynamicsMode;

import java.util.*;

/**
 * CLASS DESCRIPTION
 * <p/>
 * User: mark
 * Date: Nov 26, 2010
 * Time: 10:19:32 AM
 */

public class SimpleMoranDriftModel extends AbstractSimModel {
    GlobalTraitCountObserver countObserver;
    GlobalTraitFrequencyObserver freqObserver;
    GlobalTraitLifetimeObserver lifetimeObserver;
    EwensSampleFullPopulationObserver ewensSampler;
    ITraitDimension dimension;
    Integer numAgents;
    Double mutationRate;
    Boolean isInfiniteAlleles = false;
    Integer maxTraits;
    Integer startingTraits;

    List<IActionRule> ruleList;


    public SimpleMoranDriftModel() {
        this.modelNamePrefix = "SimpleMoranDrift";
    }

    public void initializeModel() {
        this.modelDynamicsDelegate = new MoranDynamics(this);


        // We create a single dimension, with
        //
        ITraitFactory traitFactory = null;
        if (isInfiniteAlleles == Boolean.TRUE) {
            traitFactory = new InfiniteAllelesIntegerTraitFactory(this);
        } else {
            log.error("NEED IMPLEMENTATION FOR NON INFINITE ALLELES TRAIT FACTORY!!!");
            System.exit(1);
        }
        this.dimension = this.dimensionProvider.get();
        this.dimension.setTraitVariationModel(traitFactory);
        this.dimensionList.add(this.dimension);


        // Now can initialize Observers
        this.countObserver = new GlobalTraitCountObserver(this);
        this.lifetimeObserver = new GlobalTraitLifetimeObserver(this);
        this.ewensSampler = new EwensSampleFullPopulationObserver(this);
        this.traitObserverList.add(this.countObserver);
        this.traitObserverList.add(this.lifetimeObserver);
        this.traitObserverList.add(this.ewensSampler);
        this.dimension.attach(this.traitObserverList);

        // set up the stack of rules, to be fired in the order given in the list
        // in this first simulation, all agents get the same rule, but this need not be the
        // case - plan for heterogeneity!!
        this.ruleList = new ArrayList<IActionRule>();
        IInteractionRule decisionRule = new CopyOrMutateDecisionRule(this);
        IInteractionRule rcmRule = new RandomCopyNeighborSingleDimensionRule(this);
        decisionRule.registerSubRule(rcmRule);


        if (this.isInfiniteAlleles) {
            IInteractionRule iiMutation = new InfiniteAllelesMutationRule(this);
            decisionRule.registerSubRule(iiMutation);
        } else {
            IInteractionRule fkMutation = new FiniteKAllelesMutationRule(this);
            decisionRule.registerSubRule(fkMutation);
        }

        // Just add the decision rule, since it has two subrules
        this.ruleList.add(decisionRule);


        this.log.debug("Creating one dimension and " + this.params.getStartingTraits() + " traits to begin");
        for (Integer i = 0; i < this.params.getStartingTraits(); i++) {
            this.dimension.getNewUniqueUniformVariant();
        }

        this.log.debug("Creating " + this.params.getNumAgents() + " agents with random starting traits");
        for (Integer i = 0; i < this.params.getNumAgents(); i++) {
            IAgent agent = this.getPopulation().createAgent();
            agent.setAgentID(i.toString());
            agent.addActionRuleList(this.ruleList);
            ITrait randomTrait = this.dimension.getRandomTraitFromDimension();
            randomTrait.adopt(agent);
        }

        // Verify proper initialization
        Map<ITrait, Double> freqMap = this.dimension.getCurGlobalTraitFrequencies();
        for (Map.Entry<ITrait, Double> entry : freqMap.entrySet()) {
            log.trace("Trait " + entry.getKey().getTraitID() + " Freq: " + entry.getValue().toString());
        }


    }

    public void parseCommandLineOptions(String[] args) {
        //this.log.debug("entering parseCommandLineOptions");

        this.params = new GlobalModelConfiguration(this);

        Options cliOptions = new Options();
        cliOptions.addOption("n", true, "number of agents in simulation");
        cliOptions.addOption("m", true, "mutation rate in decimal form (e.g., 0.001");
        cliOptions.addOption("s", true, "starting number of traits (must not be greater than max traits value");
        cliOptions.addOption("l", true, "length of simulation in steps");
        cliOptions.addOption("p", true, "pathname of properties file giving general model configuration (e.g., log file locations)");
        cliOptions.addOption("t", true, "model time to start recording statistics (i.e., ignore initial transient");
        cliOptions.addOption("e", true, "size of sample to take for comparison to Ewens Sampling Distribution");


        // Option group for handling traits
        Option infiniteAllelesOption = new Option("i", false, "use infinite alleles model");
        Option finiteAllelesOption = new Option("f", true, "specify K alleles model (e.g., 2, 100");
        OptionGroup traitOptionGroup = new OptionGroup();
        traitOptionGroup.setRequired(true);
        traitOptionGroup.addOption(infiniteAllelesOption);
        traitOptionGroup.addOption(finiteAllelesOption);
        cliOptions.addOptionGroup(traitOptionGroup);

        CommandLineParser parser = new PosixParser();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(cliOptions, args);
        } catch (ParseException ex) {
            System.out.println("ERROR: Command line exception: " + ex.toString());
            System.exit(1);
        }

        if (cmd.hasOption("i")) {
            this.params.setInfiniteAlleles(true);
            this.isInfiniteAlleles = true;
            //this.log.info("infinite alleles model selected - no maximum trait number");
            this.params.setMaxTraits(Integer.MAX_VALUE);
        } else {
            this.params.setInfiniteAlleles(false);
            this.isInfiniteAlleles = false;
            //this.log.info("finite alleles model selected");
            this.params.setMaxTraits(Integer.parseInt(cmd.getOptionValue("f", "2")));
        }

        // This is a constant for MORAN models
        this.params.setModelRateTimeRuns(GenerationDynamicsMode.CONTINUOUS);

        this.params.setLengthSimulation(Integer.parseInt(cmd.getOptionValue("l", "10000")));
        // this is the only parameter that's directly needed in a superclass implementation
        this.lengthSimulation = this.params.getLengthSimulation();
        this.params.setNumAgents(Integer.parseInt(cmd.getOptionValue("n", "1000")));
        this.params.setMutationRate(Double.parseDouble(cmd.getOptionValue("m", "0.000001")));
        this.params.setStartingTraits(Integer.parseInt(cmd.getOptionValue("s", "2")));
        this.propertiesFileName = cmd.getOptionValue("p", "tf-configuration.properties");
        System.out.println("properties File: " + this.propertiesFileName);
        this.params.setTimeStartStatistics(Integer.parseInt(cmd.getOptionValue("t", "100")));
        this.params.setEwensSampleSize(Integer.parseInt(cmd.getOptionValue("e", "50")));

        // Report starting parameters
        //this.log.info("starting number of traits: " + this.params.getStartingTraits());
        //this.log.info("maximum number of traits: " + this.params.getMaxTraits());
        //this.log.info("number of agents: " + this.params.getNumAgents());
        //this.log.info("mutation rate: " + this.params.getMutationRate());
        //this.log.info("simulation length: " + this.params.getLengthSimulation());
        //this.log.info("properties file: " + this.propertiesFileName);

        //this.log.trace("exiting parseCommandLineOptions");
    }


    // DEBUG ONLY
    private void checkPopulationTraits() {
        List<IAgent> agentList = this.population.getAgents();
        Map<String, Integer> traitCounts = new HashMap<String, Integer>();
        for (IAgent agent : agentList) {
            Set<ITrait> traitList = agent.getCurrentlyAdoptedTraits();
            if (traitList.size() > 1) {
                log.error("agent " + agent.getAgentID() + " has " + traitList.size() + " traits - ERROR");
            }
            for (ITrait trait : traitList) {
                Integer cnt = traitCounts.get(trait.getTraitID());
                if (cnt == null) {
                    traitCounts.put(trait.getTraitID().toString(), 1);
                } else {
                    cnt++;
                    traitCounts.put(trait.getTraitID().toString(), cnt);
                }
            }
        }
        Set<String> traitSet = traitCounts.keySet();
        log.info("====================================================================");
        for (String trait : traitSet) {
            log.info(trait + "\t" + traitCounts.get(trait));
        }
        log.info("number of traits found: " + traitCounts.size());
        log.info("====================================================================");
    }


}
