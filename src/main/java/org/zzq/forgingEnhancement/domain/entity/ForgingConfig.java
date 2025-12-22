package org.zzq.forgingEnhancement.domain.entity;

import org.zzq.forgingEnhancement.domain.IForgingConfigObserver;

import java.util.ArrayList;
import java.util.List;

public class ForgingConfig {
    private String env;
    private List<IForgingConfigObserver> observers = new ArrayList<>();

    public ForgingConfig(String env) {
        this.env = env;
    }

    public void updateConfig(String env){
        this.env = env;
        notifyObservers();
    }

    public void addObserver(IForgingConfigObserver forgingConfigObserver){
        observers.add(forgingConfigObserver);
    }

    public void removeObserver(IForgingConfigObserver forgingConfigObserver){
        observers.remove(forgingConfigObserver);
    }

    public void notifyObservers(){
        for(IForgingConfigObserver observer : observers){
            observer.onConfigUpdate(this);
        }
    }

    public String getEnv() {
        return env;
    }
}
