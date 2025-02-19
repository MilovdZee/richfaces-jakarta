/**
 * License Agreement.
 *
 * Rich Faces - Natural Ajax for Java Server Faces (JSF)
 *
 * Copyright (C) 2007 Exadel, Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 */
package org.richfaces.renderkit;

import java.util.Map;

import jakarta.faces.application.ResourceDependencies;
import jakarta.faces.application.ResourceDependency;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ActionEvent;

import org.richfaces.component.AbstractCommandButton;
import org.richfaces.log.Logger;
import org.richfaces.log.RichfacesLogger;
import org.richfaces.renderkit.util.HandlersChain;

/**
 * @author asmirnov@exadel.com (latest modification by $Author: alexsmirnov $)
 * @version $Revision: 1.1.2.3 $ $Date: 2007/02/12 17:46:53 $
 *
 */
@ResourceDependencies({ @ResourceDependency(library = "jakarta.faces", name = "faces.js"),
        @ResourceDependency(library = "org.richfaces", name = "jquery.js"),
        @ResourceDependency(library = "org.richfaces", name = "richfaces.js"),
        @ResourceDependency(library = "org.richfaces", name = "richfaces-queue.reslib") })
public abstract class AjaxCommandRendererBase extends RendererBase {
    private static final Logger LOG = RichfacesLogger.RENDERKIT.getLogger();
    private static final String SOURCE_ID = "jakarta.faces.source";

    @Override
    protected void queueComponentEventForBehaviorEvent(FacesContext context, UIComponent component, String eventName) {
        super.queueComponentEventForBehaviorEvent(context, component, eventName);

        if ("action".equals(eventName) || "click".equals(eventName)) {
            new ActionEvent(component).queue();
        }
    }

    @Override
    protected void doDecode(FacesContext facesContext, UIComponent uiComponent) {
        if (isSubmitted(facesContext, uiComponent)) {
            new ActionEvent(uiComponent).queue();
        }
    }

    protected boolean isSubmitted(FacesContext facesContext, UIComponent uiComponent) {
        if (getUtils().isBooleanAttribute(uiComponent, "disabled")) {
            return false;
        }

        String clientId = uiComponent.getClientId(facesContext);
        Map<String, String> paramMap = facesContext.getExternalContext().getRequestParameterMap();
        Object value = paramMap.get(clientId);

        /*
         * RF-13559, RF-13018, prevent commandButton with @type="button" or "reset" from executing its action if it didn't
         * submit the form
         */
        boolean preventButtonExecution = false;
        if (uiComponent instanceof AbstractCommandButton) {
            AbstractCommandButton button = (AbstractCommandButton) uiComponent;
            String type = ((String) button.getAttributes().get("type")).toLowerCase();
            String source = paramMap.get(SOURCE_ID);

            if ((type.equals("button") || type.equals("reset")) && !button.getClientId().equals(source)) {
                preventButtonExecution = true;
            }
        }

        boolean submitted = null != value && !preventButtonExecution;

        if (submitted && LOG.isDebugEnabled()) {
            LOG.debug("Decode submit of the Ajax component " + clientId);
        }

        return submitted;
    }

    public String getOnClick(FacesContext context, UIComponent component) {
        StringBuilder onClick = new StringBuilder();

        if (!getUtils().isBooleanAttribute(component, "disabled")) {
            HandlersChain handlersChain = new HandlersChain(context, component);

            handlersChain.addInlineHandlerFromAttribute("onclick");
            handlersChain.addBehaviors("click", "action");
            handlersChain.addAjaxSubmitFunction();

            String handlerScript = handlersChain.toScript();

            if (handlerScript != null) {
                onClick.append(handlerScript);
            }

            if (!"reset".equals(component.getAttributes().get("type"))) {
                onClick.append(";return false;");
            }
        } else {
            onClick.append("return false;");
        }

        return onClick.toString();
    }

    /*
     * public String getStringValue(UIComponent component) { Object value = getValue(component); return convertToString(value);
     * }
     *
     * protected String convertToString(Object obj ) { return ( obj == null ? "" : obj.toString() ); }
     *
     * protected String convertToString(boolean b ) { return String.valueOf(b); }
     *
     * protected String convertToString(int b ) { return b!=Integer.MIN_VALUE?String.valueOf(b):""; }
     *
     * protected String convertToString(long b ) { return b!=Long.MIN_VALUE?String.valueOf(b):""; }
     *
     * public void encodeChildren(FacesContext context, UIComponent component) throws IOException { renderChildren(context,
     * component); }
     *
     * public Object getValue(UIComponent component) { if (component instanceof ValueHolder) { return ((ValueHolder)
     * component).getValue(); } return component.getAttributes().get("value"); }
     *
     *
     * public String getType(UIComponent uiComponent) { String type; if (uiComponent instanceof HtmlCommandButton) { type =
     * ((HtmlCommandButton) uiComponent).getType(); } else { type = (String) uiComponent.getAttributes().get("type"); } if (type
     * == null) { type = "button"; } return type; }
     */
}
