/**
 * License Agreement.
 *
 *  JBoss RichFaces - Ajax4jsf Component Library
 *
 * Copyright (C) 2007  Exadel, Inc.
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
package org.richfaces.component;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jboss.test.faces.ApplicationServer;
import org.jboss.test.faces.htmlunit.HtmlUnitEnvironment;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.richfaces.CustomizedHtmlUnitEnvironment;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author amarkhel
 * @since 3.3.0
 */
public class QueueRendererTest {
    @Target(value = { ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    private @interface DisableQueue {
    }

    private static final String EXPECTED_QUEUE_SCRIPT = "RichFaces.queue.setQueueOptions({"
        + "\"first\": {\"requestDelay\": 400, \"ignoreDupResponses\": true}," + "\"form\": {\"requestDelay\": 400},"
        + "\"form:firstAttach\": {\"requestGroupingId\": \"request\"},"
        + "\"second\": {\"requestDelay\": 400, \"ignoreDupResponses\": true},"
        + "\"form:linkAttach\": {\"queueId\": \"second\"}," + "\"form:secondAttach\": {}" + "});";
    protected HtmlPage page;
    protected HtmlUnitEnvironment facesEnvironment;
    private boolean queueEnabled = true;
    @Rule
    public final MethodRule rule = new MethodRule() {
        public Statement apply(Statement base, FrameworkMethod method, Object target) {
            QueueRendererTest test = (QueueRendererTest) target;

            DisableQueue annotation = method.getMethod().getAnnotation(DisableQueue.class);
            if (annotation != null) {
                test.queueEnabled = false;
            }

            return base;
        }
    };

    @Before
    public void setUp() throws Exception {
        facesEnvironment = new CustomizedHtmlUnitEnvironment();

        ApplicationServer facesServer = facesEnvironment.getServer();
        facesServer.addResource("/queue.xhtml", "org/richfaces/component/queue.xhtml");
        facesServer.addResource("/nonQueue.xhtml", "org/richfaces/component/nonQueue.xhtml");

        facesEnvironment.getServer().addInitParameter("org.richfaces.queue.enabled", Boolean.toString(queueEnabled));
        facesEnvironment.start();
    }

    @After
    public void tearDown() throws Exception {
        this.page = null;

        this.facesEnvironment.release();
        this.facesEnvironment = null;
    }

    @Test
    @Ignore
    public void testQueue() throws Exception {
        page = facesEnvironment.getPage("/queue.jsf");
        String queueScript = extractQueueScript(page);
        assertNotNull(queueScript, "Queue script must be not null");
        assertEquals(dehydrate(EXPECTED_QUEUE_SCRIPT), dehydrate(queueScript));
        /*
         * String[] queueArray = queueScript.split("},"); //String[] queueNames = new String[queueArray.length]; //String[]
         * queueOptions = new String[queueArray.length]; for(int i = 0; i < queueArray.length - 1; i++){ queueArray[i] =
         * queueArray[i] + "}"; } String expectedArray = getQueuesByView("queue.jsf"); for(int i = 0; i < queueArray.length;
         * i++){ String[] queues = queueArray[i].split(":"); queueNames[i] = queues[0]; queueOptions[i] = queues[1]; }
         */

    }

    @Test
    @DisableQueue
    @Ignore
    public void testQueuingDisabled() throws Exception {
        page = facesEnvironment.getPage("/queue.jsf");
        String queueScript = extractQueueScript(page);
        assertNull(queueScript);
    }

    @Test
    @Ignore
    public void testPageWithoutQueue() throws Exception {
        page = facesEnvironment.getPage("/nonQueue.jsf");
        String queueScript = extractQueueScript(page);
        assertNull("Queue script must be null", queueScript);
    }

    private String dehydrate(String s) {
        return s.replaceAll("\\s+", "");
    }

    private String getTextContent(HtmlElement element) {
        StringBuilder sb = new StringBuilder();

        for (DomNode node = element.getFirstChild(); node != null; node = node.getNextSibling()) {
            sb.append(node.getNodeValue());
        }

        return sb.toString();
    }

    private String extractQueueScript(HtmlPage page) {
        HtmlElement scriptElement = (HtmlElement) page.getElementById(QueueRegistry.QUEUE_SCRIPT_ID);
        if (scriptElement != null) {
            return getTextContent(scriptElement).replaceAll("(^<!--)|(//-->$)", "");
        }
        return null;
    }
}
