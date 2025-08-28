package me.yunhui.shared.domain;

public abstract class ValueObject {
    @Override
    public abstract boolean equals(Object obj);
    
    @Override
    public abstract int hashCode();
}
