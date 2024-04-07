package net.fabricmc.fabric.api.transfer.v1.transaction;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
@ApiStatus.NonExtendable
public interface TransactionContext {
    Transaction openNested();
    int nestingDepth();
    Transaction getOpenTransaction(int nestingDepth);
    void addCloseCallback(CloseCallback closeCallback);
    @FunctionalInterface
    interface CloseCallback {
        void onClose(TransactionContext transaction, Result result);
    }
    void addOuterCloseCallback(OuterCloseCallback outerCloseCallback);
    @FunctionalInterface
    interface OuterCloseCallback {
        void afterOuterClose(Result result);
    }
    
    enum Result {
        ABORTED,
        COMMITTED;
        public boolean wasAborted() {
            return this == ABORTED;
        }
        public boolean wasCommitted() {
            return this == COMMITTED;
        }
    }
}
