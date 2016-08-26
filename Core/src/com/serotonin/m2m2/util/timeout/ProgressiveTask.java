/**
 * Copyright (C) 2016 Infinite Automation Software. All rights reserved.
 * @author Terry Packer
 */
package com.serotonin.m2m2.util.timeout;

import com.serotonin.util.ProgressiveTaskListener;

/**
 * @author Matthew Lohbihler, Terry Packer
 */
abstract public class ProgressiveTask extends HighPriorityTask {
    private boolean cancelled = false;
    protected boolean completed = false;
    private ProgressiveTaskListener listener;

    public ProgressiveTask(String name, String id, int queueSize, boolean queueable){
    	super(name, id, queueSize, queueable);
    }
    
    public ProgressiveTask(String name, String id, int queueSize, boolean queueable, ProgressiveTaskListener l) {
    	super(name, id, queueSize, queueable);
    	listener = l;
    }

    public boolean cancel() {
        cancelled = true;
        return super.cancel();
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public boolean isCompleted() {
        return completed;
    }

    @Override
    public final void run(long runtime) {
        while (true) {
            if (isCancelled()) {
                declareFinished(true);
                break;
            }

            runImpl();

            if (isCompleted()) {
                declareFinished(false);
                break;
            }
        }
        completed = true;
    }

    protected void declareProgress(float progress) {
        ProgressiveTaskListener l = listener;
        if (l != null)
            l.progressUpdate(progress);
    }

    private void declareFinished(boolean cancelled) {
        ProgressiveTaskListener l = listener;
        if (l != null) {
            if (cancelled)
                l.taskCancelled();
            else
                l.taskCompleted();
        }
    }

    /**
     * Implementers of this method MUST return from it occasionally so that the cancelled status can be checked. Each
     * return must leave the class and thread state with the expectation that runImpl will not be called again, while
     * acknowledging the possibility that it will.
     * 
     * Implementations SHOULD call the declareProgress method with each runImpl execution such that the listener can be
     * notified.
     * 
     * Implementations MUST set the completed field to true when the task is finished.
     */
    abstract protected void runImpl();
}