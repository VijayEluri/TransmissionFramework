#!/bin/bash

java -Xms1g -Xmx1g -Dlog4j.configuration=log4j.config -ea -cp TransmissionFrameworkModels.jar:annotations.jar:aopalliance-1.0.jar:atunit-1.0.1.jar:collections-generic-4.01.jar:colt-1.2.0.jar:commons-cli-1.2.jar:commons-collections-3.2.1.jar:commons-math-2.2.jar:concurrent-1.3.4.jar:guava-r05.jar:guice-2.0.jar:jung-algorithms-2.0.1.jar:jung-api-2.0.1.jar:jung-graph-impl-2.0.1.jar:jung-io-2.0.1.jar:junit-4.8.1.jar:log4j-1.2.12.jar:stax-api-1.0.1.jar:trove4j-3.0.2.jar:wstx-asl-3.2.6.jar:xpp3_min-1.1.3.4.O.jar:xstream-1.2.2.jar org.madsenlab.sim.tf.test.examples.WrightFisherDriftModel.WrightFisherDriftSim -n 1000 -i -s 100 -m 0.005 -l 3000 -p ./tf.properties -t 150 -e 30 

