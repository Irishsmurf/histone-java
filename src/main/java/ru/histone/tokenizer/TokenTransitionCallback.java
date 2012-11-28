package ru.histone.tokenizer;

public interface TokenTransitionCallback {
    public TokenContext getTransition(NestedLevelHolder nestLevelHolder);

    class NestedLevelHolder {
        public int nestLevel = 0;
    }
}
