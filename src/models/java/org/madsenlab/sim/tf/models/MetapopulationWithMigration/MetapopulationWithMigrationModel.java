/*
 * Copyright (c) 2013.  Mark E. Madsen <mark@madsenlab.org>
 *
 * This work is licensed under the terms of the Creative Commons-GNU General Public Llicense 2.0, as "non-commercial/sharealike".  You may use, modify, and distribute this software for non-commercial purposes, and you must distribute any modifications under the same license.
 *
 * For detailed license terms, see:
 * http://creativecommons.org/licenses/GPL/2.0/
 */

package org.madsenlab.sim.tf.models.MetapopulationWithMigration;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.apache.commons.cli.*;
import org.madsenlab.sim.tf.models.MoranDynamics;
import org.madsenlab.sim.tf.observers.*;
import org.madsenlab.sim.tf.config.GlobalModelConfiguration;
import org.madsenlab.sim.tf.interfaces.*;
import org.madsenlab.sim.tf.models.AbstractSimModel;
import org.madsenlab.sim.tf.rules.CopyOrMutateDecisionRule;
import org.madsenlab.sim.tf.rules.FiniteKAllelesMutationRule;
import org.madsenlab.sim.tf.rules.InfiniteAllelesMutationRule;
import org.madsenlab.sim.tf.rules.RandomCopyNeighborSingleDimensionRule;
import org.madsenlab.sim.tf.traits.InfiniteAllelesIntegerTraitFactory;
import org.madsenlab.sim.tf.enums.AgentTagType;
import org.madsenlab.sim.tf.enums.GenerationDynamicsMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CLASS DESCRIPTION
 * <p/>
 * User: mark
 * Date: Nov 26, 2010
 * Time: 10:19:32 AM
 */

public class MetapopulationWithMigrationModel extends AbstractSimModel {
    GlobalTraitCountObserver countObserver;
    GlobalTraitFrequencyObserver freqObserver;
    GlobalTraitLifetimeObserver lifetimeObserver;
    ITraitDimension dimension;
    Integer numAgents;
    Double mutationRate;
    Boolean isInfiniteAlleles = false;
    Integer maxTraits;
    Integer startingTraits;
    List<IAgentTag> demeTagList;
    @Inject
    Provider<IAgentTag> tagProvider;
    // Undecided yet how to structure interaction rules generically so keeping them here for the moment
    List<IActionRule> ruleList;


    public MetapopulationWithMigrationModel() {
        this.modelNamePrefix = "MetapopulationWithMigrationMoranModel";
    }

    public void initializeModel() {
        this.modelDynamicsDelegate = new MoranDynamics(this);

        this.demeTagList = new ArrayList<IAgentTag>();
        // needed only temporarily to later initialize demes and agents
        HashMap<Integer, IAgentTag> demeTagMap = new HashMap<Integer, IAgentTag>();

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
        this.traitObserverList.add(this.countObserver);
        this.traitObserverList.add(this.lifetimeObserver);


        // Construct trait frequency observers for each deme - need to do this now, so we can register
        // each with the traits they observe
        for (Integer i = 0; i < this.params.getNumDemes(); i++) {
            // Construct a tag that marks all agents in this deme
            IAgentTag demeTag = this.tagProvider.get();
            demeTag.setTagType(AgentTagType.DEME);
            demeTag.setTagName(i.toString());
            this.demeTagList.add(demeTag);
            demeTagMap.put(i, demeTag); // not used outside initializeModel()
            PerDemeTraitFrequencyObserver demeObs = new PerDemeTraitFrequencyObserver(this, demeTag);
            this.traitObserverList.add(demeObs);
        }
        // this must be done AFTER all observers have been constructed and added to the Observer list, otherwise we get
        // NPEs from dangling references when we notify() later...
        this.dimension.attach(this.traitObserverList);


        log.debug("demeTagMap: " + demeTagMap);

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
        Integer numDemes = this.params.getNumDemes();
        Integer numAgents = this.params.getNumAgents();
        Integer numAgentsPerDeme = numAgents / numDemes;

        if (numAgents % numDemes != 0) {
            log.info("numAgents is not an integer multiple of numDemes, using only " + numAgentsPerDeme + " agents");
        }

        // Debug only
        //XStream xs = new XStream();

        for (Integer i = 0; i < numDemes; i++) {
            IAgentTag demeTag = demeTagMap.get(i);

            log.debug("Creating " + numAgentsPerDeme + " agents with deme : " + demeTag);

            for (Integer j = 0; j < numAgentsPerDeme; j++) {
                log.trace("creating agent " + j + " for deme " + i);
                IAgent agent = this.getPopulation().createAgentWithTag(demeTag);
                agent.setAgentID(i.toString());
                agent.addActionRuleList(this.ruleList);
                ITrait randomTrait = this.dimension.getRandomTraitFromDimension();

                if (!agent.hasTag(demeTag)) {
                    throw new IllegalStateException("agent doesn't have the deme tag");
                }

                randomTrait.adopt(agent);
            }
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
        cliOptions.addOption("d", true, "number of demes in the metapopulation (make this an integer multiple of -n");
        cliOptions.addOption("t", true, "model time to start recording statistics (i.e., ignore initial transient");
        cliOptions.addOption("e", true, "size of sample to take for comparison to Ewens Sampling Distribution (per deme)");

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

        // This is a constant for Moran-style models
        this.params.setModelRateTimeRuns(GenerationDynamicsMode.CONTINUOUS);

        this.params.setLengthSimulation(Integer.parseInt(cmd.getOptionValue("l", "10000")));
        // this is the only parameter that's directly needed in a superclass implementation
        this.lengthSimulation = this.params.getLengthSimulation();
        this.params.setNumAgents(Integer.parseInt(cmd.getOptionValue("n", "1000")));
        this.params.setMutationRate(Double.parseDouble(cmd.getOptionValue("m", "0.000001")));
        this.params.setStartingTraits(Integer.parseInt(cmd.getOptionValue("s", "2")));
        this.propertiesFileName = cmd.getOptionValue("p", "tf-configuration.properties");
        this.params.setNumDemes(Integer.parseInt(cmd.getOptionValue("d", "2")));
        this.params.setTimeStartStatistics(Integer.parseInt(cmd.getOptionValue("t", "100")));
        this.params.setEwensSampleSize(Integer.parseInt(cmd.getOptionValue("e", "50")));

        // Report starting parameters
        //this.log.info("starting number of traits: " + this.params.getStartingTraits());
        //this.log.info("maximum number of traits: " + this.params.getMaxTraits());
        //this.log.info("number of agents: " + this.params.getNumAgents());
        //this.log.info("mutation rate: " + this.params.getMutationRate());
        //this.log.info("simulation length: " + this.params.getLengthSimulation());
        //this.log.info("number of demes: " + this.params.getNumDemes());
        //this.log.info("properties file: " + this.propertiesFileName);

        //this.log.trace("exiting parseCommandLineOptions");
    }


}
