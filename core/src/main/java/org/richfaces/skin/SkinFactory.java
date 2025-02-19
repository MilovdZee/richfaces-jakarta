/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and individual contributors
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
package org.richfaces.skin;

import jakarta.faces.context.FacesContext;

import org.richfaces.application.ServiceTracker;

/**
 * Base factory class ( implement Singleton design pattern ). Produce self instance to build current skin configuration. At
 * present, realised as lazy creation factory.
 *
 * @author asmirnov@exadel.com (latest modification by $Author: alexsmirnov $)
 * @version $Revision: 1.1.2.1 $ $Date: 2007/01/09 18:59:43 $
 */
public abstract class SkinFactory {
    @Deprecated
    public static final SkinFactory getInstance() {
        return getInstance(FacesContext.getCurrentInstance());
    }

    public static final SkinFactory getInstance(FacesContext context) {
        return ServiceTracker.getService(context, SkinFactory.class);
    }

    /**
     * Get default {@link Skin} implementation.
     */
    public abstract Skin getDefaultSkin(FacesContext context);

    /**
     * Get current {@link Skin} implementation.
     */
    public abstract Skin getSkin(FacesContext context);

    /**
     * Get base {@link Skin} implementation
     */
    public abstract Skin getBaseSkin(FacesContext facesContext);

    public abstract Theme getTheme(FacesContext facesContext, String name);

    public abstract Skin getSkin(FacesContext context, String name);
}
