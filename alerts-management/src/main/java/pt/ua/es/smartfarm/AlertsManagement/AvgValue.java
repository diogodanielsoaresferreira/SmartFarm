package pt.ua.es.smartfarm.AlertsManagement;

import java.io.Serializable;


public class AvgValue implements Serializable {
    private int count;
    private double sum;

    public AvgValue(Integer count, Double sum) {
        this.count = count;
        this.sum = sum;
    }
    
    public AvgValue add(String aggKey, Double value, AvgValue aggregate) {
        return new AvgValue(aggregate.count + 1, aggregate.sum + value );
    }
    
    public int getCount(){
        return count;
    }
    
    public double getSum(){
        return sum;
    }
    
}
