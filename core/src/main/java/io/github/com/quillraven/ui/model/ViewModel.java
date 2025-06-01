package io.github.com.quillraven.ui.model;

import io.github.com.quillraven.GdxGame;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public abstract class ViewModel {
    protected final GdxGame game;
    protected final PropertyChangeSupport propertyChangeSupport;

    public ViewModel(GdxGame game) {
        this.game = game;
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        this.propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
    }
}
