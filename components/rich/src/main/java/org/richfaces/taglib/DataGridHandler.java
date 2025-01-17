/*
 * JBoss, Home of Professional Open Source
 * Copyright ${year}, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.richfaces.taglib;

import jakarta.faces.view.facelets.ComponentConfig;
import jakarta.faces.view.facelets.ComponentHandler;
import jakarta.faces.view.facelets.MetaRuleset;

import org.richfaces.view.facelets.RowKeyConverterRule;

/**
 * @author Anton Belevich
 *
 */
public class DataGridHandler extends ComponentHandler {
    private static final String ELEMENTS = "elements";
    private static final String ROWS = "rows";

    public DataGridHandler(ComponentConfig config) {
        super(config);
    }

    @Override
    protected MetaRuleset createMetaRuleset(Class type) {
        MetaRuleset metaRuleset = super.createMetaRuleset(type);
        metaRuleset.alias(ELEMENTS, ROWS);
        metaRuleset.addRule(RowKeyConverterRule.INSTANCE);
        return metaRuleset;
    }
}
