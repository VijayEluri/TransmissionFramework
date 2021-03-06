/*
 * Copyright (c) 2013.  Mark E. Madsen <mark@madsenlab.org>
 *
 * This work is licensed under the terms of the Creative Commons-GNU General Public Llicense 2.0, as "non-commercial/sharealike".  You may use, modify, and distribute this software for non-commercial purposes, and you must distribute any modifications under the same license.
 *
 * For detailed license terms, see:
 * http://creativecommons.org/licenses/GPL/2.0/
 */

package org.madsenlab.sim.tf.enums;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CLASS DESCRIPTION
 * <p/>
 * User: mark
 * Date: 9/5/11
 * Time: 10:26 AM
 */
public enum ArtifactTagType {
    DEME("DEME"),
    ARTIFACT_FUNCTION("ARTIFACT_FUNCTION");

    private final String tagType;

    private static final Map<String, ArtifactTagType> stringToEnum = new ConcurrentHashMap<String, ArtifactTagType>();

    static {
        for (ArtifactTagType set : values()) {
            stringToEnum.put(set.tagType, set);
        }
    }

    private ArtifactTagType(String setting) {
        this.tagType = setting;
    }

    public String toString() {
        return this.tagType;
    }

    public static ArtifactTagType fromString(String setting) {
        return stringToEnum.get(setting);
    }
}

