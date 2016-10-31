package com.ligao.entity;



/**
 * CookBook entity. @author MyEclipse Persistence Tools
 */

public class CookBook  implements java.io.Serializable {


    // Fields    

     private Integer id;
     private String cname;
     private String cmethod;
     private String csource;
     private String cpic;


    // Constructors

    /** default constructor */
    public CookBook() {
    }

    
    /** full constructor */
    public CookBook(String cname, String cmethod, String csource, String cpic) {
        this.cname = cname;
        this.cmethod = cmethod;
        this.csource = csource;
        this.cpic = cpic;
    }

   
    // Property accessors

    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }

    public String getCname() {
        return this.cname;
    }
    
    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getCmethod() {
        return this.cmethod;
    }
    
    public void setCmethod(String cmethod) {
        this.cmethod = cmethod;
    }

    public String getCsource() {
        return this.csource;
    }
    
    public void setCsource(String csource) {
        this.csource = csource;
    }

    public String getCpic() {
        return this.cpic;
    }
    
    public void setCpic(String cpic) {
        this.cpic = cpic;
    }
   








}