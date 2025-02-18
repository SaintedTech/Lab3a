package saintindustries.cs408.lab3a;

import android.util.Log;
import android.widget.Toast;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;

public class Controller implements PropertyChangeListener {

    private Model model = new Model();
    private String display = "display";

    private ArrayList<AbstractView> views;
    private ArrayList<Model> models;

    public static String DISPLAYTAG = "display";



    public Controller() {

        /* Initialize View and Model Lists */

        views = new ArrayList<>();
        models = new ArrayList<>();

    }

    public void addModel(Model model) {

        /*
         * Add a new Model to the list, and register this controller as the listener for
         * PropertyChange events
         */

        models.add(model);
        model.addPropertyChangeListener(this);

    }

    public void removeModel(Model model) {

        /*
         * Remove a Model from the list, and un-register this controller as the listener for
         * PropertyChange events
         */

        models.remove(model);
        model.removePropertyChangeListener(this);

    }

    public void addView(AbstractView view) {

        /* Add a new View to the list */

        views.add(view);

    }

    public void removeView(AbstractView view) {

        /* Remove a View from the list */

        views.remove(view);

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

        /*
         * This method is called automatically by "firePropertyChange()" when an
         * element of a Model is changed.  It informs all registered Views of
         * the change so that they can update themselves accordingly.
         */

        for (AbstractView view : views) {
            view.modelPropertyChange(evt);
        }

    }

    protected void setModelProperty(String propertyName, Object newValue) {

        /*
         * This method is called by an AbstractController subclass when the View
         * informs it of a user interaction which requires a change to a Model.
         * Using the property name, it identifies which of the registered Models
         * has the corresponding setter method (using reflection), and then invokes
         * this method so that the Model(s) can be updated properly.
         */

        for (Model model : models) {

            try {

                Method method = model.getClass().getMethod("set" + propertyName, newValue.getClass());
                method.invoke(model, newValue);

            }

            catch (Exception e) {
                e.printStackTrace();
            }

        }

    }


    public void parseInput(Character input){
        Log.i("MyTag", "It is attempting to parse input (Inside contoller)");
        setModelProperty("Input", input);

    }


    }

