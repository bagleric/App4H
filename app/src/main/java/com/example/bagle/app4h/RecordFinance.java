package com.example.bagle.app4h;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by bagle on 11/23/2017.
 */
@IgnoreExtraProperties
public class RecordFinance {
    public String description;
    public String amount;
    public boolean isExpense;
    public String id;

//    public String getDescription() {
//        return description;
//    }

//    public void setDescription(String description) {
//        this.description = description;
//    }
//
//    public String getAmount() {
//        return amount;
//    }
//
//    public void setAmount(String amount) {
//        this.amount = amount;
//    }
//
//    public boolean getIsExpense() {
//        return isExpense;
//    }
//
//    public void setIsExpense(boolean isExpense) {
//        this.isExpense = isExpense;
//    }

    public RecordFinance(String description, String amount, boolean isExpense, String id) {
        this.description = description;
        this.amount = amount;
        this.isExpense = isExpense;
        this.id = id;
    }
}
