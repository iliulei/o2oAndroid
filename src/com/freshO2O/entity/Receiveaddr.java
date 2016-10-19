package com.freshO2O.entity;



/**
 * Receiveaddr entity. @author MyEclipse Persistence Tools
 */

public class Receiveaddr  implements java.io.Serializable {


    // Fields    

     private Integer id;
     private String username;
     private String phone;
     private String address;
     private boolean choosed;

    // Constructors

    /** default constructor */
    public Receiveaddr() {
    }

    
    /** full constructor */
    public Receiveaddr(String username, String phone, String address) {
        this.username = username;
        this.phone = phone;
        this.address = address;
    }

   
    // Property accessors

    public boolean isChoosed() {
		return choosed;
	}


	public void setChoosed(boolean choosed) {
		this.choosed = choosed;
	}


	public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return this.phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return this.address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
   








}