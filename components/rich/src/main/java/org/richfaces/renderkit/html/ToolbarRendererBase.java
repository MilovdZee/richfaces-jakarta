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

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jakarta.faces.application.ResourceDependencies;
import jakarta.faces.application.ResourceDependency;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;

import org.richfaces.component.AbstractToolbar;
import org.richfaces.component.AbstractToolbarGroup;
import org.richfaces.renderkit.ComponentAttribute;
import org.richfaces.renderkit.HtmlConstants;
import org.richfaces.renderkit.RenderKitUtils;
import org.richfaces.renderkit.RenderKitUtils.ScriptHashVariableWrapper;
import org.richfaces.renderkit.util.HtmlDimensions;
import org.richfaces.renderkit.RendererBase;

@ResourceDependencies({ @ResourceDependency(library = "jakarta.faces", name = "faces.js"),
        @ResourceDependency(library = "org.richfaces", name = "jquery.js"),
        @ResourceDependency(library = "org.richfaces", name = "richfaces.js"),
        @ResourceDependency(library = "org.richfaces", name = "richfaces-base-component.js"),
        @ResourceDependency(library = "org.richfaces", name = "toolbar.js"),
        @ResourceDependency(library = "org.richfaces", name = "toolbar.ecss") })
public abstract class ToolbarRendererBase extends RendererBase {
    public static final String RENDERER_TYPE = "org.richfaces.ToolbarRenderer";
    public static final Map<String, ComponentAttribute> ITEMS_HANDLER_ATTRIBUTES = Collections
        .unmodifiableMap(ComponentAttribute.createMap(
            new ComponentAttribute(HtmlConstants.ONCLICK_ATTRIBUTE).setEventNames("itemclick").setComponentAttributeName(
                "onitemclick"),
            new ComponentAttribute(HtmlConstants.ONDBLCLICK_ATTRIBUTE).setEventNames("itemdblclick").setComponentAttributeName(
                "onitemdblclick"),
            new ComponentAttribute(HtmlConstants.ONMOUSEDOWN_ATTRIBUTE).setEventNames("itemmousedown")
                .setComponentAttributeName("onitemmousedown"),
            new ComponentAttribute(HtmlConstants.ONMOUSEUP_ATTRIBUTE).setEventNames("itemmouseup").setComponentAttributeName(
                "onitemmouseup"),
            new ComponentAttribute(HtmlConstants.ONMOUSEOVER_ATTRIBUTE).setEventNames("itemmouseover")
                .setComponentAttributeName("onitemmouseover"),
            new ComponentAttribute(HtmlConstants.ONMOUSEMOVE_ATTRIBUTE).setEventNames("itemmousemove")
                .setComponentAttributeName("onitemmousemove"),
            new ComponentAttribute(HtmlConstants.ONMOUSEOUT_ATTRIBUTE).setEventNames("itemmouseout").setComponentAttributeName(
                "onitemmouseout"),
            new ComponentAttribute(HtmlConstants.ONKEYPRESS_ATTRIBUTE).setEventNames("itemkeypress").setComponentAttributeName(
                "onitemkeypress"), new ComponentAttribute(HtmlConstants.ONKEYDOWN_ATTRIBUTE).setEventNames("itemkeydown")
                .setComponentAttributeName("onitemkeydown"), new ComponentAttribute(HtmlConstants.ONKEYUP_ATTRIBUTE)
                .setEventNames("itemkeyup").setComponentAttributeName("onitemkeyup")));

    public enum ItemSeparators {
        NONE,
        SQUARE,
        DISC,
        GRID,
        LINE
    }

    public enum Locations {
        RIGHT,
        LEFT
    }

    private void writeColElement(ResponseWriter writer, UIComponent component) throws IOException {
        writer.startElement(HtmlConstants.COL_ELEMENT, component);
        writer.writeAttribute(HtmlConstants.STYLE_ATTRIBUTE, "width : 1px", null);
        writer.endElement(HtmlConstants.COL_ELEMENT);
    }

    private boolean isSeparatorFacetRendered(UIComponent component) {
        UIComponent separatorFacet = component.getFacet("itemSeparator");
        return (separatorFacet != null) ? separatorFacet.isRendered() : false;
    }

    private boolean isSeparatorAttributeRendered(UIComponent component) {
        String itemSeparator = (String) component.getAttributes().get("itemSeparator");

        if (itemSeparator != null && itemSeparator.trim().length() != 0
            && !itemSeparator.equalsIgnoreCase(ItemSeparators.NONE.toString())) {
            return true;
        }
        return false;
    }

    private int getColumnCount(List<UIComponent> components) {
        int result = 0;
        for (UIComponent component : components) {
            if (component instanceof AbstractToolbarGroup) {
                result += ((AbstractToolbarGroup) component).getRenderedChildren().size();
            } else {
                result++;
            }
        }
        return result;
    }

    private int getCountSeparators(AbstractToolbar toolBar, List<UIComponent> components) {
        int result = 0;
        if (components != null && (isSeparatorFacetRendered(toolBar) || isSeparatorAttributeRendered(toolBar))) {
            result += components.size() - 1;
        }

        for (UIComponent component : components) {
            if (component instanceof AbstractToolbarGroup) {
                result += getCountSeparators((AbstractToolbarGroup) component, ((AbstractToolbarGroup) component).getRenderedChildren());
            }
        }
        return result;
    }

    private int getCountSeparators(AbstractToolbarGroup toolBarGroup, List<UIComponent> components) {
        if (components != null && (isSeparatorFacetRendered(toolBarGroup) || isSeparatorAttributeRendered(toolBarGroup))) {
            return components.size() - 1;
        }
        return 0;
    }

    protected void renderColElements(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        List<UIComponent> childrenToTheLeft = new LinkedList<>();
        List<UIComponent> childrenToTheRight = new LinkedList<>();

        getChildrenToLeftAndRight(context, component, childrenToTheLeft, childrenToTheRight);
        int columnAmount = getCountSeparators((AbstractToolbar) component, childrenToTheLeft) + getColumnCount(childrenToTheLeft);
        for (int i = 0; i < columnAmount; i++) {
            writeColElement(writer, component);
        }

        writer.startElement(HtmlConstants.COL_ELEMENT, component);
        writer.writeAttribute(HtmlConstants.STYLE_ATTRIBUTE, "width : 100%", null);
        writer.endElement(HtmlConstants.COL_ELEMENT);

        columnAmount = getCountSeparators((AbstractToolbar) component, childrenToTheRight) + getColumnCount(childrenToTheRight);
        for (int i = 0; i < columnAmount; i++) {
            writeColElement(writer, component);
        }
    }

    private void getChildrenToLeftAndRight(FacesContext context, UIComponent component,
        final List<UIComponent> childrenToTheLeft, final List<UIComponent> childrenToTheRight) {

        AbstractToolbar toolbar = (AbstractToolbar) component;
        List<UIComponent> children = toolbar.getChildren();
        if (children != null) {
            for (UIComponent child : children) {
                if (child.isRendered()) {
                    if (child instanceof AbstractToolbarGroup) {
                        AbstractToolbarGroup group = (AbstractToolbarGroup) child;
                        String location = group.getLocation();
                        if (location != null && location.equalsIgnoreCase(Locations.RIGHT.toString())) {
                            childrenToTheRight.add(child);
                        } else {
                            childrenToTheLeft.add(child);
                        }
                    } else {
                        childrenToTheLeft.add(child);
                    }
                }
            }
        }
    }

    @Override
    public void doEncodeChildren(ResponseWriter writer, FacesContext context, UIComponent component) throws IOException {
        AbstractToolbar toolbar = (AbstractToolbar) component;
        String itemClass = (String) toolbar.getAttributes().get("itemClass");
        String itemStyle = (String) toolbar.getAttributes().get("itemStyle");

        List<UIComponent> children = toolbar.getChildren();

        if (children != null) {
            List<UIComponent> childrenToTheLeft = new LinkedList<>();
            List<UIComponent> childrenToTheRight = new LinkedList<>();

            getChildrenToLeftAndRight(context, component, childrenToTheLeft, childrenToTheRight);

            for (Iterator<UIComponent> it = childrenToTheLeft.iterator(); it.hasNext();) {

                UIComponent child = it.next();

                if (!(child instanceof AbstractToolbarGroup)) {
                    writer.startElement(HtmlConstants.TD_ELEM, component);
                    writer.writeAttribute(HtmlConstants.ID_ATTRIBUTE, encodeClientItemID(child), null);
                    writer.writeAttribute(HtmlConstants.CLASS_ATTRIBUTE, concatClasses("rf-tb-itm", itemClass), null);
                    if (isPropertyRendered(itemStyle)) {
                        writer.writeAttribute(HtmlConstants.STYLE_ATTRIBUTE, itemStyle, null);
                    }
                }

                child.encodeAll(context);

                if (!(child instanceof AbstractToolbarGroup)) {
                    writer.endElement(HtmlConstants.TD_ELEM);
                }

                if (it.hasNext()) {
                    insertSeparatorIfNeed(context, toolbar, writer);
                }
            }

            writer.startElement(HtmlConstants.TD_ELEM, component);
            writer.writeAttribute(HtmlConstants.CLASS_ATTRIBUTE, concatClasses("rf-tb-emp", itemClass), null);
            writer.writeText("\u00a0", null);
            writer.endElement(HtmlConstants.TD_ELEM);

            for (Iterator<UIComponent> it = childrenToTheRight.iterator(); it.hasNext();) {
                UIComponent child = it.next();
                child.encodeAll(context);
                if (it.hasNext()) {
                    insertSeparatorIfNeed(context, toolbar, writer);
                }
            }
        }
    }

    /**
     * Inserts separator between toolbar items. Uses facet "itemSeparator" if it is set and default separator implementation if
     * facet is not set.
     *
     * @param context - faces context
     * @param component - component
     * @param writer - response writer
     * @throws IOException - in case of IOException during writing to the ResponseWriter
     */
    protected void insertSeparatorIfNeed(FacesContext context, UIComponent component, ResponseWriter writer) throws IOException {
        UIComponent separatorFacet = component.getFacet("itemSeparator");
        boolean isSeparatorFacetRendered = (separatorFacet != null) ? separatorFacet.isRendered() : false;
        if (isSeparatorFacetRendered) {
            writer.startElement(HtmlConstants.TD_ELEM, component);
            writer.writeAttribute(HtmlConstants.CLASS_ATTRIBUTE, "rf-tb-sep", null);
            separatorFacet.encodeAll(context);
            writer.endElement(HtmlConstants.TD_ELEM);
        } else {
            insertDefaultSeparatorIfNeed(context, component, writer);
        }
    }

    /**
     * Inserts default separator. Possible values are: "square", "disc", "grid", "line" - for separators provided by component
     * implementation; "none" - for no separators between toolbar items; URI string value - for custom images specified by the
     * page author.
     *
     * @param context - faces context
     * @param component - component
     * @param writer - response writer
     * @throws IOException - in case of IOException during writing to the ResponseWriter
     */
    protected void insertDefaultSeparatorIfNeed(FacesContext context, UIComponent component, ResponseWriter writer)
        throws IOException {
        String itemSeparator = (String) component.getAttributes().get("itemSeparator");

        if (itemSeparator != null && itemSeparator.trim().length() != 0
            && !itemSeparator.equalsIgnoreCase(ItemSeparators.NONE.toString())) {

            ItemSeparators separator = null;
            if (itemSeparator.equalsIgnoreCase(ItemSeparators.SQUARE.toString())) {
                separator = ItemSeparators.SQUARE;
            } else if (itemSeparator.equalsIgnoreCase(ItemSeparators.DISC.toString())) {
                separator = ItemSeparators.DISC;
            } else if (itemSeparator.equalsIgnoreCase(ItemSeparators.GRID.toString())) {
                separator = ItemSeparators.GRID;
            } else if (itemSeparator.equalsIgnoreCase(ItemSeparators.LINE.toString())) {
                separator = ItemSeparators.LINE;
            }

            writer.startElement(HtmlConstants.TD_ELEM, component);
            String separatorClass = "rf-tb-sep";
            separatorClass = concatClasses(separatorClass, (String) component.getAttributes().get("separatorClass"));
            writer.writeAttribute(HtmlConstants.CLASS_ATTRIBUTE, separatorClass, null);

            if (separator != null) {
                String itemSeparatorClass = "rf-tb-sep-" + separator.toString().toLowerCase();
                writer.startElement(HtmlConstants.DIV_ELEM, component);
                writer.writeAttribute(HtmlConstants.CLASS_ATTRIBUTE, itemSeparatorClass, null);
                writer.writeText("\u00a0", null);
                writer.endElement(HtmlConstants.DIV_ELEM);
            } else {

                String uri = RenderKitUtils.getResourceURL(itemSeparator, context);
                writer.startElement(HtmlConstants.IMG_ELEMENT, component);
                writer.writeAttribute(HtmlConstants.SRC_ATTRIBUTE, uri, null);
                writer.writeAttribute(HtmlConstants.ALT_ATTRIBUTE, "", null);
                writer.endElement(HtmlConstants.IMG_ELEMENT);
            }

            writer.endElement(HtmlConstants.TD_ELEM);
        }
    }

    protected Class<? extends jakarta.faces.component.UIComponent> getComponentClass() {
        return AbstractToolbar.class;
    }

    public boolean getRendersChildren() {
        return true;
    }

    protected boolean isPropertyRendered(String property) {
        return (null != property && !"".equals(property));
    }

    protected String getWidthToolbar(UIComponent component) {
        if (component instanceof AbstractToolbar) {
            String width = ((AbstractToolbar) component).getWidth();
            if (width == null || width.length() == 0) {
                return "100%";
            }
            return HtmlDimensions.formatSize(width);
        } else {
            return "";
        }
    }

    protected String getHeightToolbar(UIComponent component) {
        if (component instanceof AbstractToolbar) {
            return HtmlDimensions.formatSize(((AbstractToolbar) component).getHeight());
        } else {
            return "";
        }
    }

    protected Map<String, Object> getOptions(UIComponent component) {
        if (component == null) {
            return null;
        }
        HashMap<String, Object> results = new HashMap<>();
        if (component instanceof AbstractToolbar) {
            Map<String, Object> tbEvents = new HashMap<>();
            for (ComponentAttribute componentAttribute : ITEMS_HANDLER_ATTRIBUTES.values()) {
                Object attr = component.getAttributes().get(componentAttribute.getComponentAttributeName());
                if (attr != null) {
                    RenderKitUtils.addToScriptHash(tbEvents, componentAttribute.getHtmlAttributeName().substring(2), attr,
                        null, ScriptHashVariableWrapper.eventHandler);
                }
            }
            results.put("id", component.getClientId());
            results.put("events", tbEvents);

            List<AbstractToolbarGroup> groups = getToolBarGroups((AbstractToolbar) component);
            List<Map<String, Object>> tbgListOptions = new LinkedList<>();

            for (AbstractToolbarGroup tbg : groups) {

                Map<String, Object> tbgOptions = new HashMap<>();
                Map<String, Object> tbgEvents = new HashMap<>();
                List<String> tbgIDs = new LinkedList<>();

                for (UIComponent comp : tbg.getChildren()) {
                    tbgIDs.add(encodeClientItemID(comp));
                }

                for (ComponentAttribute componentAttribute : ITEMS_HANDLER_ATTRIBUTES.values()) {
                    Object attr = tbg.getAttributes().get(componentAttribute.getComponentAttributeName());
                    if (attr != null) {
                        RenderKitUtils.addToScriptHash(tbgEvents, componentAttribute.getHtmlAttributeName().substring(2), attr,
                            null, ScriptHashVariableWrapper.eventHandler);
                    }
                }
                if (!tbgEvents.isEmpty()) {
                    tbgOptions.put("events", tbgEvents);
                    tbgOptions.put("ids", tbgIDs);
                    tbgListOptions.add(tbgOptions);
                }
            }

            results.put("groups", tbgListOptions);
        }
        return results;
    }

    protected String encodeClientItemID(UIComponent component) {
        return component != null ? component.getClientId() + "_itm" : "";
    }

    private List<AbstractToolbarGroup> getToolBarGroups(AbstractToolbar toolBar) {
        List<AbstractToolbarGroup> list = new LinkedList<>();
        if (toolBar != null) {
            for (UIComponent comp : toolBar.getChildren()) {
                if (comp instanceof AbstractToolbarGroup) {
                    list.add((AbstractToolbarGroup) comp);
                }
            }
        }
        return list;
    }
}
