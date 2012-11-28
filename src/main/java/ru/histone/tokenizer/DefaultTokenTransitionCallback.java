package ru.histone.tokenizer;

public class DefaultTokenTransitionCallback implements TokenTransitionCallback {
    private TokenContext transition;

    public DefaultTokenTransitionCallback(TokenContext transition) {
        this.transition = transition;
    }

    @Override
    public TokenContext getTransition(NestedLevelHolder nestLevelHolder) {
        return transition;
    }
}
