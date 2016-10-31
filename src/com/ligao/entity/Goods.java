package com.ligao.entity;
// default package



/**
 * Goods entity. @author MyEclipse Persistence Tools
 */

public class Goods  implements java.io.Serializable {


    // Fields    

     private Integer GId;
     private String GName;
     private String GPic;
     private String GDesc;
     private Double GPrice;
     private Double GDis;
     private String GType;
     private Integer categoryId;
     private Integer g_count;
     private String g_reco;
     private int g_integration;
     private int g_discount;

    // Constructors

    /** default constructor */
    public Goods() {
    }

    
    /** full constructor */
    public Goods(String GName, String GPic, String GDesc, Double GPrice, Double GDis, String GType, Integer categoryId) {
        this.GName = GName;
        this.GPic = GPic;
        this.GDesc = GDesc;
        this.GPrice = GPrice;
        this.GDis = GDis;
        this.GType = GType;
        this.categoryId = categoryId;
    }

   
    // Property accessors

    public String getG_reco() {
		return g_reco;
	}


	public int getG_integration() {
		return g_integration;
	}


	public void setG_integration(int g_integration) {
		this.g_integration = g_integration;
	}


	public int getG_discount() {
		return g_discount;
	}


	public void setG_discount(int g_discount) {
		this.g_discount = g_discount;
	}


	public void setG_reco(String gReco) {
		g_reco = gReco;
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

    public String getGPic() {
        return this.GPic;
    }
    
    public void setGPic(String GPic) {
        this.GPic = GPic;
    }

    public String getGDesc() {
        return this.GDesc;
    }
    
    public void setGDesc(String GDesc) {
        this.GDesc = GDesc;
    }

    public Double getGPrice() {
        return this.GPrice;
    }
    
    public void setGPrice(Double GPrice) {
        this.GPrice = GPrice;
    }

    public Double getGDis() {
        return this.GDis;
    }
    
    public void setGDis(Double GDis) {
        this.GDis = GDis;
    }

    public String getGType() {
        return this.GType;
    }
    
    public void setGType(String GType) {
        this.GType = GType;
    }

    public Integer getCategoryId() {
        return this.categoryId;
    }
    
    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }


	public Integer getG_count() {
		return g_count;
	}


	public void setG_count(Integer gCount) {
		g_count = gCount;
	}
   
}