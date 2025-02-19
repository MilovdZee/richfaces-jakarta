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
package org.richfaces.resource.optimizer.resource.handler.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import jakarta.faces.application.Resource;
import jakarta.faces.context.FacesContext;

import org.richfaces.resource.optimizer.FileNameMapper;
import org.richfaces.resource.optimizer.vfs.VirtualFile;
import org.richfaces.application.ServiceTracker;

/**
 * @author Nick Belaevski
 *
 */
public class VFSResource extends Resource {
    private VirtualFile file;
    private String relativePath;

    public VFSResource(VirtualFile file, String relativePath) {
        super();

        assert file.isFile();
        assert relativePath != null;

        this.file = file;
        this.relativePath = relativePath;
    }

    private FileNameMapper getFileNameMapper() {
        return ServiceTracker.getService(FileNameMapper.class);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return file.getInputStream();
    }

    @Override
    public Map<String, String> getResponseHeaders() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRequestPath() {
        return getFileNameMapper().createName(relativePath);
    }

    @Override
    public URL getURL() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean userAgentNeedsUpdate(FacesContext context) {
        throw new UnsupportedOperationException();
    }
}
