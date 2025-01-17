/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc. and individual contributors
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
package org.richfaces.renderkit;

import java.util.Map;

import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIInput;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.ConverterException;

import org.richfaces.component.util.InputUtils;

/**
 * @author Nick Belaevski - nbelaevski@exadel.com created 23.01.2007
 */
public class InputRendererBase extends RendererBase {
    protected void doDecode(FacesContext context, UIComponent component) {
        String clientId = component.getClientId(context);
        Map<String, String> requestParameterMap = context.getExternalContext().getRequestParameterMap();
        String newValue = (String) requestParameterMap.get(clientId);

        if (null != newValue) {
            UIInput input = (UIInput) component;

            input.setSubmittedValue(newValue);
        }
    }

    public Object getConvertedValue(FacesContext context, UIComponent component, Object val) throws ConverterException {
        return InputUtils.getConvertedValue(context, component, val);
    }

    public String getInputValue(FacesContext context, UIComponent component) throws ConverterException {
        return InputUtils.getInputValue(context, component);
    }
}
