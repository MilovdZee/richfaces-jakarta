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
package org.richfaces.component;

import jakarta.el.MethodExpression;
import jakarta.faces.component.UIComponentBase;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AbortProcessingException;
import jakarta.faces.event.FacesEvent;
import jakarta.faces.event.PhaseId;

import org.richfaces.cdk.annotations.Attribute;
import org.richfaces.cdk.annotations.JsfComponent;
import org.richfaces.cdk.annotations.JsfRenderer;
import org.richfaces.cdk.annotations.Tag;
import org.richfaces.cdk.annotations.TagType;
import org.richfaces.component.attribute.AjaxProps;
import org.richfaces.component.attribute.BypassProps;
import org.richfaces.component.attribute.ImmediateProps;
import org.richfaces.event.DropEvent;
import org.richfaces.event.DropListener;
import org.richfaces.view.facelets.DropHandler;

/**
 * <p>
 *     The &lt;rich:dropTarget&gt; component can be added to a component so that the component can accept dragged items.
 *     The dragged items must be defined with a compatible drop type for the &lt;rich:dragSource&gt; component.
 * </p>
 * @author abelevich
 */
@JsfComponent(type = AbstractDropTarget.COMPONENT_TYPE, family = AbstractDropTarget.COMPONENT_FAMILY,
        generate = "org.richfaces.component.UIDropTarget",
        renderer = @JsfRenderer(type = "org.richfaces.DropTargetRenderer"),
        tag = @Tag(name = "dropTarget", handlerClass = DropHandler.class, type = TagType.Facelets))
public abstract class AbstractDropTarget extends UIComponentBase implements AjaxProps, BypassProps, ImmediateProps {
    public static final String COMPONENT_TYPE = "org.richfaces.DropTarget";
    public static final String COMPONENT_FAMILY = "org.richfaces.DropTarget";

    /**
     * Data to be processed after a drop event
     */
    @Attribute
    public abstract Object getDropValue();

    /**
     * A list of drag zones types, which elements are accepted by a drop zone
     */
    @Attribute
    public abstract Object getAcceptedTypes();

    /**
     * MethodExpression representing an action listener method that will be notified after drop operation.
     * The expression must evaluate to a public method that takes an DropEvent parameter, with a return type of void,
     * or to a public method that takes no arguments with a return type of void.
     * In the latter case, the method has no way of easily knowing where the event came from, but this can be useful in
     * cases where a notification is needed that "some action happened".
     */
    @Attribute
    public abstract MethodExpression getDropListener();

    public void addDropListener(DropListener listener) {
        addFacesListener(listener);
    }

    public DropListener[] getDropListeners() {
        return (DropListener[]) getFacesListeners(DropListener.class);
    }

    public void removeDropListener(DropListener listener) {
        removeFacesListener(listener);
    }

    @Override
    public void queueEvent(FacesEvent event) {
        if (event instanceof DropEvent) {

            if (isImmediate()) {
                event.setPhaseId(PhaseId.APPLY_REQUEST_VALUES);
            } else if (isBypassUpdates()) {
                event.setPhaseId(PhaseId.PROCESS_VALIDATIONS);
            } else {
                event.setPhaseId(PhaseId.INVOKE_APPLICATION);
            }
        }
        super.queueEvent(event);
    }

    @Override
    public void broadcast(FacesEvent event) throws AbortProcessingException {
        super.broadcast(event);

        if (event instanceof DropEvent && (isBypassUpdates() || isImmediate())) {
            FacesContext.getCurrentInstance().renderResponse();
        }
    }
}
