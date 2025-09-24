package org.game.stage.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UnitModBase {
    
    public static final Logger logger = LogManager.getLogger(UnitModBase.class);
    
    protected final UnitObject unitObj;
    
    public UnitModBase(UnitObject unitObj) {
        this.unitObj = unitObj;
    }

    public <T extends UnitModBase> T getMod(Class<T> clazz) {
        return unitObj.getUMod(clazz);
    }
    
    protected void onPulse(long now) {
        
    }
    
    protected void onPulseSec(long now) {
        
    }
}