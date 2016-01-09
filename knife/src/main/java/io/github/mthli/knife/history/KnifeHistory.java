package io.github.mthli.knife.history;

import android.text.Editable;

import io.github.mthli.knife.history.action.Action;

/**
 * Created by cauchywei on 16/1/8.
 */
public class KnifeHistory {

    public interface HistoryStateChangeListener {
        void onUndoEnabledStateChange(boolean enabled);
        void onRedoEnabledStateChange(boolean enabled);
    }

    public static final int DEFAULT_MAX_CAPACITY = 100;

    private boolean enabled = true;
    private boolean processing = false;
    private HistoryStateChangeListener stateChangeListener;
    private CapacityLimitedStack<Action> undoActionStack = new CapacityLimitedStack<>();
    private CapacityLimitedStack<Action> redoActionStack = new CapacityLimitedStack<>();


    public void record(Action action) {
        if (enabled && action !=null) {
            if (stateChangeListener != null) {
                if (!redoActionStack.isEmpty()) {
                    stateChangeListener.onRedoEnabledStateChange(false);
                }
                if (undoActionStack.isEmpty()) {
                    stateChangeListener.onUndoEnabledStateChange(true);
                }
            }
            redoActionStack.clear();
            undoActionStack.push(action);
        }
    }
    public void undo(Editable editable){

        if (stateChangeListener != null && redoActionStack.isEmpty()) {
            stateChangeListener.onRedoEnabledStateChange(true);
        }

        Action pop = undoActionStack.pop();
        processing = true;
        pop.undo(editable);
        processing = false;
        redoActionStack.push(pop);

        if (stateChangeListener != null && undoActionStack.isEmpty()) {
            stateChangeListener.onUndoEnabledStateChange(false);
        }
    }

    public void redo(Editable editable){

        if (stateChangeListener != null && undoActionStack.isEmpty()) {
            stateChangeListener.onUndoEnabledStateChange(true);
        }

        Action pop = redoActionStack.pop();
        processing = true;
        pop.redo(editable);
        processing = false;
        undoActionStack.push(pop);

        if (stateChangeListener != null  && redoActionStack.isEmpty() ) {
            stateChangeListener.onRedoEnabledStateChange(false);
        }


    }

    public void clear() {
        if (!undoActionStack.isEmpty()) {
            undoActionStack.clear();
            if (stateChangeListener != null) {
                stateChangeListener.onUndoEnabledStateChange(false);
            }
        }

        if (!redoActionStack.isEmpty()) {
            redoActionStack.clear();
            if (stateChangeListener != null) {
                stateChangeListener.onRedoEnabledStateChange(false);
            }
        }
    }
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isProcessing() {
        return processing;
    }

    public boolean isUndoable() {
        return !undoActionStack.isEmpty();
    }

    public boolean isRedoable() {
        return !redoActionStack.isEmpty();
    }

    public int getMaxCapacity() {
        return undoActionStack.getMaxCapacity();
    }

    public void setMaxCapacity(int maxCapacity) {
        undoActionStack.setMaxCapacity(maxCapacity);
    }

    public HistoryStateChangeListener getStateChangeListener() {
        return stateChangeListener;
    }

    public void setStateChangeListener(HistoryStateChangeListener stateChangeListener) {
        this.stateChangeListener = stateChangeListener;
    }
}
