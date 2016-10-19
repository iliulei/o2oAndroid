package com.freshO2O.entity;

import java.util.Date;


/**
 * Indent entity. @author MyEclipse Persistence Tools
 */

public class Indent  implements java.io.Serializable {


    // Fields    

     private Integer id;
     private String account;
     private Integer GId;
     private String GName;
     private Double GPrice;
     private Integer GNum;
     private Double GAmount;
     private String OState;
     private Date OTime;
     private String OAddr;


    // Constructors

    /** default constructor */
    public Indent() {
    }

    
    /** full constructor */
    public Indent(String account, Integer GId, String GName, Double GPrice, Integer GNum, Double GAmount, String OState, Date OTime, String OAddr) {
        this.account = account;
        this.GId = GId;
        this.GName = GName;
        this.GPrice = GPrice;
        this.GNum = GNum;
        this.GAmount = GAmount;
        this.OState = OState;
        this.OTime = OTime;
        this.OAddr = OAddr;
    }

   
    // Property accessors

    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }

    public String getAccount() {
        return this.account;
    }
    
    public void setAccount(String account) {
        this.account = account;
    }

    public Integer getGId() {
        return this.GId;
    }
    
    public void setGId(Integer GId) {
        this.GId = GId;
    }

    public String getGName() {
        return this.GName;
    }
    
    public void setGName(String GName) {
        this.GName = GName;
    }

    public Double getGPrice() {
        return this.GPrice;
    }
    
    public void setGPrice(Double GPrice) {
        this.GPrice = GPrice;
    }

    public Integer getGNum() {
        return this.GNum;
    }
    
    public void setGNum(Integer GNum) {
        this.GNum = GNum;
    }

    public Double getGAmount() {
        return this.GAmount;
    }
    
    public void setGAmount(Double GAmount) {
        this.GAmount = GAmount;
    }

    public String getOState() {
        return this.OState;
    }
    
    public void setOState(String OState) {
        this.OState = OState;
    }

    public Date getOTime() {
        return this.OTime;
    }
    
    public void setOTime(Date OTime) {
        this.OTime = OTime;
    }

    public String getOAddr() {
        return this.OAddr;
    }
    
    public void setOAddr(String OAddr) {
        this.OAddr = OAddr;
    }
   








}