package org.openmicroscopy.shoola.agents.metadata.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import omero.gateway.model.AnnotationData;
import omero.gateway.model.TextualAnnotationData;

import org.apache.commons.collections.CollectionUtils;
import org.openmicroscopy.shoola.agents.metadata.util.DataToSave;
import org.openmicroscopy.shoola.util.CommonsLangUtils;
import org.openmicroscopy.shoola.util.ui.UIUtilities;
import org.openmicroscopy.shoola.util.ui.border.SeparatorOneLineBorder;
import org.openmicroscopy.shoola.util.ui.omeeditpane.OMEWikiComponent;

/**
 * A {@link AnnotationTaskPaneUI} for displaying comments (
 * {@link TextualAnnotationData})
 * 
 * @author Dominik Lindner &nbsp;&nbsp;&nbsp;&nbsp; <a
 *         href="mailto:d.lindner@dundee.ac.uk">d.lindner@dundee.ac.uk</a>
 */
public class CommentsTaskPaneUI extends AnnotationTaskPaneUI implements
        DocumentListener {

    /** The border of a field that can be edited. */
    static final Border EDIT_BORDER = BorderFactory
            .createLineBorder(Color.LIGHT_GRAY);

    /**
     * Creates a new instance
     * 
     * @param model
     *            Reference to the {@link EditorModel}
     * @param view
     *            Reference to the {@link EditorUI}
     * @param controller
     *            Reference to the {@link EditorControl}
     */
    CommentsTaskPaneUI(EditorModel model, EditorUI view,
            EditorControl controller) {
        super(model, view, controller);
        initComponents();
    }
    
    

    @Override
    List<AnnotationData> getAnnotationsToSave() {
        List<AnnotationData> l = new ArrayList<AnnotationData>();
        String text = commentArea.getText();
        if (CommonsLangUtils.isNotBlank(text))
            l.add(new TextualAnnotationData(text));
        return l;
    }



    @Override
    List<Object> getAnnotationsToRemove() {
        List<Object> l = new ArrayList<Object>();
        if (annotationToRemove != null)
            l.addAll(annotationToRemove);
        return l;
    }



    @Override
    void refreshUI() {
        displayAnnotations(model.getTextualAnnotationsByDate());
    }

    /** Reference to the control. */
    private EditorControl controller;

    /**
     * Area displaying the latest textual annotation made by the currently
     * logged in user if any.
     */
    private OMEWikiComponent commentArea;

    /** The constraints used to lay out the components. */
    private GridBagConstraints constraints;

    /** The collection of annotation to display. */
    private List annotationToDisplay;

    /** The collection of annotations to remove. */
    private List annotationToRemove;

    /** Scrollpane hosting the comment text field */
    private JScrollPane pane;

    /** The add comment button */
    private JButton addButton;

    /** Initializes the components. */
    private void initComponents() {
        commentArea = new OMEWikiComponent(false);
        commentArea.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        commentArea.addPropertyChangeListener(controller);
        // commentArea.setBackground(UIUtilities.BACKGROUND_COLOR);
        commentArea.setForeground(UIUtilities.DEFAULT_FONT_COLOR);
        commentArea.setComponentBorder(EDIT_BORDER);
        commentArea.addFocusListener(new FocusListener() {

            public void focusLost(FocusEvent arg0) {
                if (CommonsLangUtils.isBlank(commentArea.getText())) {
                    pane.getViewport().setPreferredSize(null);
                    revalidate();
                    pane.revalidate();
                }
            }

            public void focusGained(FocusEvent arg0) {
                Dimension d = commentArea.getSize();
                pane.getViewport().setPreferredSize(new Dimension(d.width, 60));
                revalidate();
                pane.revalidate();
            }
        });

        setBorder(new SeparatorOneLineBorder());
        setBackground(UIUtilities.BACKGROUND_COLOR);

        addButton = new JButton("Add comment");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveComment();
            }
        });
        addButton.setEnabled(false);

        buildGUI();
    }

    /**
     * Sets the text of the {@link #commentArea}.
     * 
     * @param text
     *            The value to set.
     */
    private void setAreaText(String text) {
        commentArea.removeDocumentListener(this);
        commentArea.setText(text);
        commentArea.addDocumentListener(this);
    }

    /** Builds and lays out the UI. */
    private void buildGUI() {
        removeAll();

        pane = new JScrollPane(commentArea);
        pane.setBorder(null);

        setLayout(new GridBagLayout());

        constraints = new GridBagConstraints();
        constraints.insets = new Insets(2, 0, 2, 0);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.weighty = 1;
        add(pane, constraints);
        constraints.gridy++;

        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        add(addButton, constraints);
        constraints.gridy++;

        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTHWEST;
    }

    /**
     * Displays the annotations.
     * 
     * @param list
     *            The annotations to display.
     */
    private void displayAnnotations(List list) {
        annotationToDisplay = list;

        boolean enabled = model.canAnnotate();
        if (enabled && model.isMultiSelection()) {
            enabled = !model.isAcrossGroups();
        }
        commentArea.setEnabled(enabled);

        if (!CollectionUtils.isEmpty(list)) {
            Color c = UIUtilities.BACKGROUND_COLOUR_ODD;
            for (Object obj : annotationToDisplay) {
                TextualAnnotationData data = (TextualAnnotationData) obj;
                TextualAnnotationComponent comp = new TextualAnnotationComponent(
                        model, data);
                comp.addPropertyChangeListener(controller);
                comp.setAreaColor(c);
                add(comp, constraints);
                constraints.gridy++;

                if (c == UIUtilities.BACKGROUND_COLOUR_ODD)
                    c = UIUtilities.BACKGROUND_COLOUR_EVEN;
                else
                    c = UIUtilities.BACKGROUND_COLOUR_ODD;
            }
        }
    }

    /**
     * Removes the textual annotation from the view.
     * 
     * @param annotation
     *            The annotation to remove.
     */
    void removeTextualAnnotation(TextualAnnotationData annotation) {
        if (annotationToRemove == null)
            annotationToRemove = new ArrayList();
        annotationToRemove.clear();
        annotationToRemove.add(annotation);
        List l = model.getTextualAnnotationsByDate();
        List toKeep = new ArrayList();
        if (l != null) {
            Iterator i = l.iterator();
            Object o;
            TextualAnnotationData data;
            while (i.hasNext()) {
                o = i.next();
                if (o instanceof TextualAnnotationData) {
                    data = (TextualAnnotationData) o;
                    if (data.getId() != annotation.getId())
                        toKeep.add(data);
                }
            }
        }
        displayAnnotations(toKeep);
        revalidate();
        repaint();
        firePropertyChange(EditorControl.SAVE_PROPERTY, Boolean.valueOf(false),
                Boolean.valueOf(true));
    }

    /**
     * Overridden to lay out the annotations.
     * 
     * @see AnnotationUI#buildUI()
     */
    protected void buildUI() {
        buildGUI();
        if (model.isMultiSelection()) {
            displayAnnotations(null);
        } else {
            displayAnnotations(model.getTextualAnnotationsByDate());
        }
        revalidate();
        repaint();
    }

    /**
     * Returns the collection of annotations to remove.
     * 
     * @see AnnotationUI#getAnnotationToRemove()
     */
    protected List<Object> getAnnotationToRemove() {
        List<Object> l = new ArrayList<Object>();
        if (annotationToRemove != null)
            l.addAll(annotationToRemove);
        return l;
    }

    /**
     * Returns the collection of annotations to add.
     * 
     * @see AnnotationUI#getAnnotationToSave()
     */
    protected List<AnnotationData> getAnnotationToSave() {
        List<AnnotationData> l = new ArrayList<AnnotationData>();
        String text = commentArea.getText();
        if (CommonsLangUtils.isNotBlank(text))
            l.add(new TextualAnnotationData(text));
        return l;
    }

    /**
     * Returns <code>true</code> if we have textual annotation to save
     * <code>false</code> otherwise.
     * 
     * @see AnnotationUI#hasDataToSave()
     */
    protected boolean hasDataToSave() {
        String text = commentArea.getText();
        return CommonsLangUtils.isNotBlank(text);
    }

    /**
     * Clears the UI.
     * 
     * @see AnnotationUI#clearDisplay()
     */
    protected void clearDisplay() {
    }

    /**
     * Clears the data to save.
     * 
     * @see AnnotationUI#clearData(Object)
     */
    protected void clearData(Object oldObject) {
        if (annotationToRemove != null)
            annotationToRemove.clear();
        annotationToDisplay = null;
        setAreaText("");
        addButton.setEnabled(false);
    }

    /** Saves the comment */
    private void saveComment() {
        List<AnnotationData> comments = getAnnotationToSave();
        model.fireAnnotationSaving(
                new DataToSave(comments, Collections.emptyList()), null, false);
    }

    /**
     * Fires property indicating that some text has been entered.
     * 
     * @see DocumentListener#insertUpdate(DocumentEvent)
     */
    public void insertUpdate(DocumentEvent e) {
        addButton.setEnabled(hasDataToSave());
        firePropertyChange(EditorControl.SAVE_PROPERTY, Boolean.FALSE,
                Boolean.TRUE);
    }

    /**
     * Fires property indicating that some text has been entered.
     * 
     * @see DocumentListener#removeUpdate(DocumentEvent)
     */
    public void removeUpdate(DocumentEvent e) {
        addButton.setEnabled(hasDataToSave());
        firePropertyChange(EditorControl.SAVE_PROPERTY, Boolean.FALSE,
                Boolean.TRUE);
    }

    /**
     * Required by the {@link DocumentListener} I/F but no-op implementation in
     * our case.
     * 
     * @see DocumentListener#changedUpdate(DocumentEvent)
     */
    public void changedUpdate(DocumentEvent e) {
    }



    @Override
    void onRelatedNodesSet() {
        addButton.setEnabled(model.canAddAnnotationLink());
    }
    
    
}
