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

    public <T> void onPropertyChange(String propertyName, Class<T> propType, OnPropertyChange<T> consumer) {
        this.propertyChangeSupport.addPropertyChangeListener(propertyName, evt ->
            consumer.onChange(propType.cast(evt.getNewValue()))
        );
    }

    public void clearPropertyChanges() {
        for (PropertyChangeListener listener : this.propertyChangeSupport.getPropertyChangeListeners()) {
            this.propertyChangeSupport.removePropertyChangeListener(listener);
        }
    }

    @FunctionalInterface
    public interface OnPropertyChange<T> {
        void onChange(T value);
    }
}
