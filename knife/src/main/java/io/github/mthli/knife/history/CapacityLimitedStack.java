package io.github.mthli.knife.history;

import java.util.LinkedList;

/**
 * Created by cauchywei on 16/1/8.
 *
 * CapacityLimitedStack is a stack that has a specified max-capacity
 * if we push new element into stack when stack is full,
 * it will remove the element in stack-bottom according to FIFO
 */
public class CapacityLimitedStack<T> extends LinkedList<T> {

    public static final int DEFAULT_MAX_CAPACITY = Integer.MAX_VALUE;
    private int maxCapacity;

    public CapacityLimitedStack(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public CapacityLimitedStack() {
        maxCapacity = DEFAULT_MAX_CAPACITY;
    }

    @Override
    public void push(T t) {
        super.push(t);
        clearExtraElements();
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        if (maxCapacity <= 0) {
            throw new IllegalArgumentException("Illegal max capacity :" + maxCapacity);
        }

        this.maxCapacity = maxCapacity;
        clearExtraElements();

    }

    public void clearExtraElements() {
        int index = size();
        while (index > maxCapacity) {
            remove(--index);
        }
    }
}
