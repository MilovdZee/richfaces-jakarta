package org.richfaces.component;

import java.util.Arrays;
import java.util.List;

import org.richfaces.cdk.annotations.Attribute;

/**
 * @author <a href="http://community.jboss.org/people/bleathem">Brian Leathem</a>
 *
 */
public abstract class AbstractOrderingComponent extends AbstractSelectManyComponent {

    @Attribute(defaultValue = "true", hidden = true) // TODO: unhide once javascript API's are available RF-11209
    public abstract boolean isShowButton();

    /**
     * The text to display in the move-to-top button
     */
    @Attribute(defaultValue = "\u21D1 First")
    public abstract String getUpTopText();

    /**
     * The text to display in the move-up button
     */
    @Attribute(defaultValue = "\u2191 Up")
    public abstract String getUpText();

    /**
     * The text to display in the move-down button
     */
    @Attribute(defaultValue = "\u2193 Down")
    public abstract String getDownText();

    /**
     * The text to display in the move-to-bottom button
     */
    @Attribute(defaultValue = "\u21D3 Last")
    public abstract String getDownBottomText();

    protected boolean compareValues(Object previous, Object value) {
        if (previous == null && value != null) {
            return true;
        } else if (previous != null && value == null) {
            return true;
        } else if (previous == null) {
            return false;
        }

        List oldList;
        List newList;

        if (previous instanceof List) {
            oldList = (List) previous;
        } else {
            if (previous instanceof Object[]) {
                oldList = Arrays.asList(previous);
            } else {
                throw new IllegalArgumentException("Ordered List Components must be backed by a List or Array");
            }
        }

        if (value instanceof List) {
            newList = (List) value;
        } else {
            if (value instanceof Object[]) {
                newList = Arrays.asList(value);
            } else {
                throw new IllegalArgumentException("Ordered List Components must be backed by a List or Array");
            }
        }

        return !oldList.equals(newList);
    }
}