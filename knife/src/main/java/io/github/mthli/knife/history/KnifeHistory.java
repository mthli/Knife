package io.github.mthli.knife.history;

/**
 * Created by cauchywei on 16/1/8.
 */
public class KnifeHistory {

    interface Action {
        void perform();
    }

    interface HistoryStateChangeListener {
        void onUndoEnabledStateChange(boolean enable);
        void onRedoEnabledStateChange(boolean enable);
    }

    private boolean enabled = true;
    private boolean processing = false;
    private HistoryStateChangeListener stateChangeListener;
    private CapacityLimitedStack<Action> undoActionStack = new CapacityLimitedStack<>();
    private CapacityLimitedStack<Action> redoActionStack = new CapacityLimitedStack<>();


    public void record(Action action) {
        if (enabled) {
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
    public void undo(){

        if (stateChangeListener != null && redoActionStack.isEmpty()) {
            stateChangeListener.onRedoEnabledStateChange(true);
        }

        Action pop = undoActionStack.pop();
        processing = true;
        pop.perform();
        processing = false;
        redoActionStack.push(pop);

        if (stateChangeListener != null && undoActionStack.isEmpty()) {
            stateChangeListener.onUndoEnabledStateChange(false);
        }
    }

    public void redo(){

        if (stateChangeListener != null && undoActionStack.isEmpty()) {
            stateChangeListener.onUndoEnabledStateChange(true);
        }

        Action pop = redoActionStack.pop();
        processing = true;
        pop.perform();
        processing = false;
        undoActionStack.push(pop);

        if (stateChangeListener != null && redoActionStack.isEmpty() ) {
            stateChangeListener.onRedoEnabledStateChange(false);
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
