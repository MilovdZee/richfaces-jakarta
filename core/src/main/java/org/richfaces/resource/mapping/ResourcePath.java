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
package org.richfaces.resource.mapping;

import java.net.URL;

import jakarta.faces.application.Resource;

import org.richfaces.resource.ResourceKey;
import org.richfaces.webapp.ResourceServlet;
import org.richfaces.util.URLUtils;

/**
 * <p>Represents a path to the resource.</p>
 *
 * <p>In correspondance to {@link Resource}, it does not allow to obtain defails of the requested resource, it knows just its path.</p>
 *
 * <p>This class primarily serves as a base for configuring Resource Mapping that drives resources through {@link ResourceServlet}.</p>
 *
 * @author Lukas Fryc
 */
public class ResourcePath {

    private final String path;

    /**
     * Creates resource path from given that can be either absolute or relative URL
     */
    public ResourcePath(String resourcePath) {
        this.path = resourcePath;
    }

    /**
     * Creates resource path from provided absolute URL
     */
    public ResourcePath(URL url) {
        this.path = url.toExternalForm();
    }

    /**
     * Creates a relative resource path from given {@link ResourceKey}
     */
    public ResourcePath(ResourceKey key) {
        if (key.getLibraryName() != null && !key.getLibraryName().isEmpty()) {
            this.path = key.getLibraryName() + "/" + key.getResourceName();
        } else {
            this.path = key.getResourceName();
        }
    }

    /**
     * Exports given resource path to its external form that is
     */
    public String toExternalForm() {
        return path;
    }

    /**
     * Is given resource path an absolute URL?
     */
    public boolean isAbsoluteURL() {
        return URLUtils.isValidURL(path);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ResourcePath other = (ResourcePath) obj;
        if (path == null) {
            if (other.path != null)
                return false;
        } else if (!path.equals(other.path))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ResourcePath [path=" + path + "]";
    }
}
