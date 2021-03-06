/*
 * Copyright (c) 2013.  Mark E. Madsen <mark@madsenlab.org>
 *
 * This work is licensed under the terms of the Creative Commons-GNU General Public Llicense 2.0, as "non-commercial/sharealike".  You may use, modify, and distribute this software for non-commercial purposes, and you must distribute any modifications under the same license.
 *
 * For detailed license terms, see:
 * http://creativecommons.org/licenses/GPL/2.0/
 */

package org.madsenlab.sim.tf.traits;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.madsenlab.sim.tf.interfaces.ISimulationModel;
import org.madsenlab.sim.tf.interfaces.ITraitDimension;

/**
 * CLASS DESCRIPTION
 * <p/>
 * User: mark
 * Date: Jul 14, 2010
 * Time: 10:55:30 AM
 */

@Deprecated
public class UnstructuredTraitDimensionProvider implements Provider<ITraitDimension> {
    @Inject
    private ISimulationModel model;

    public ITraitDimension get() {
        ITraitDimension dim = new UnstructuredTraitDimension<Integer>();
        dim.setSimulationModel(model);
        return dim;
    }
}
