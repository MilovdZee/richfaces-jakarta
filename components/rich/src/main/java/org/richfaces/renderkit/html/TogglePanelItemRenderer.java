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
package org.richfaces.renderkit.html;

import static org.richfaces.renderkit.HtmlConstants.DIV_ELEM;
import static org.richfaces.renderkit.HtmlConstants.ID_ATTRIBUTE;
import static org.richfaces.renderkit.HtmlConstants.STYLE_ATTRIBUTE;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jakarta.faces.application.ResourceDependencies;
import jakarta.faces.application.ResourceDependency;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;

import org.ajax4jsf.javascript.JSObject;
import org.richfaces.cdk.annotations.JsfRenderer;
import org.richfaces.component.AbstractTogglePanel;
import org.richfaces.component.AbstractTogglePanelItem;
import org.richfaces.component.AbstractTogglePanelItemInterface;
import org.richfaces.component.VisitChildrenRejectable;

/**
 * @author akolonitsky
 * @since -4712-01-01
 */
@ResourceDependencies({ @ResourceDependency(library = "jakarta.faces", name = "faces.js"),
        @ResourceDependency(library = "org.richfaces", name = "jquery.js"),
        @ResourceDependency(library = "org.richfaces", name = "richfaces.js"),
        @ResourceDependency(library = "org.richfaces", name = "richfaces-queue.reslib"),
        @ResourceDependency(library = "org.richfaces", name = "richfaces-base-component.js"),
        @ResourceDependency(library = "org.richfaces", name = "richfaces-event.js"),
        @ResourceDependency(library = "org.richfaces", name = "togglePanelItem.js") })
@JsfRenderer(type = "org.richfaces.TogglePanelItemRenderer", family = AbstractTogglePanelItem.COMPONENT_FAMILY)
public class TogglePanelItemRenderer extends DivPanelRenderer {
    private static final String LEAVE = "leave";
    private static final String ENTER = "enter";
    private final boolean hideInactiveItems;

    public TogglePanelItemRenderer() {
        this(true);
    }

    protected TogglePanelItemRenderer(boolean hideInactiveItems) {
        this.hideInactiveItems = hideInactiveItems;
    }

    @Override
    protected String getStyleClass(UIComponent component) {
        return concatClasses("rf-tgp-itm", attributeAsString(component, "styleClass"));
    }

    @Override
    protected JSObject getScriptObject(FacesContext context, UIComponent component) {
        return new JSObject("RichFaces.ui.TogglePanelItem", component.getClientId(context), getScriptObjectOptions(context,
            component));
    }

    @Override
    protected Map<String, Object> getScriptObjectOptions(FacesContext context, UIComponent component) {
        AbstractTogglePanelItemInterface panelItem = (AbstractTogglePanelItemInterface) component;

        Map<String, Object> options = new HashMap<>();
        options.put("name", panelItem.getName());
        options.put("togglePanelId", panelItem.getParentPanel().getClientId(context));
        options.put("switchMode", panelItem.getSwitchType());

        AbstractTogglePanel panel = panelItem.getParentPanel();
        options.put("index", panel.getIndexByName(panelItem.getName()));

        TogglePanelRenderer.addEventOption(context, component, options, LEAVE);
        TogglePanelRenderer.addEventOption(context, component, options, ENTER);

        return options;
    }

    @Override
    protected Class<? extends UIComponent> getComponentClass() {
        return AbstractTogglePanelItem.class;
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    protected void encodePlaceHolder(FacesContext context, UIComponent item) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        writer.startElement(DIV_ELEM, null);
        writer.writeAttribute(ID_ATTRIBUTE, item.getClientId(context), null);
        writer.writeAttribute(STYLE_ATTRIBUTE, "display:none;", null);
        writer.endElement(DIV_ELEM);
    }

    protected void encodePlaceHolderWithJs(FacesContext context, UIComponent item) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement(DIV_ELEM, null);
        writer.writeAttribute(ID_ATTRIBUTE, item.getClientId(context), null);
        writer.writeAttribute(STYLE_ATTRIBUTE, "display:none;", null);

        writeJavaScript(writer, context, item);

        writer.endElement(DIV_ELEM);
    }

    @Override
    protected void doEncodeBegin(ResponseWriter writer, FacesContext context, UIComponent component) throws IOException {
        if (((VisitChildrenRejectable) component).shouldVisitChildren()) {
            doEncodeItemBegin(writer, context, component);
        }
    }

    @Override
    protected void doEncodeChildren(ResponseWriter writer, FacesContext context, UIComponent component) throws IOException {

        if (((VisitChildrenRejectable) component).shouldVisitChildren()) {
            renderChildren(context, component);
        }
    }

    @Override
    protected void doEncodeEnd(ResponseWriter writer, FacesContext context, UIComponent component) throws IOException {
        if (((VisitChildrenRejectable) component).shouldVisitChildren()) {
            doEncodeItemEnd(writer, context, component);
        } else {
            encodePlaceHolderWithJs(context, component);
        }
    }

    @Override
    protected String getStyle(UIComponent component) {
        String attributeStyle = super.getStyle(component);
        if (hideInactiveItems && !((AbstractTogglePanelItemInterface) component).isActive()) {
            return concatStyles(attributeStyle, "display: none");
        } else {
            return attributeStyle;
        }
    }
}
