package com.darkprograms.speech.synthesiser;

/**
 *
 * @author santana
 */
class Pair {
    private final String name;
    private final Object value;
    
    public Pair(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }
}
