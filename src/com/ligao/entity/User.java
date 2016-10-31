package com.ligao.entity;

import java.util.Date;


/**
 * User entity. @author MyEclipse Persistence Tools
 */

public class User  implements java.io.Serializable {


    // Fields    

     private Integer id;
     private String account;
     private String password;
     private Date registertime;
     private Integer integrate;
     private String nickname;
     private String email;
     private String phone;
     private Date birthday;
     private String addr;
     private String recvaddr;

     /**
     * 登录用户名
     */
    private String LoginName;
    /**
    * 帐号名称
    */
     private String Name;
     /**
     * 工厂编码
     */
     private String DeptCode;
     /**
     * 工厂名称
     */
     private String DeptName;

    // Constructors

    /** default constructor */
    public User() {
    }

    
    /** full constructor */
    public User(String account, String password, Date registertime, Integer integrate, String nickname, String email, String phone, Date birthday, String addr) {
        this.account = account;
        this.password = password;
        this.registertime = registertime;
        this.integrate = integrate;
        this.nickname = nickname;
        this.email = email;
        this.phone = phone;
        this.birthday = birthday;
        this.addr = addr;
    }
    
    
    


	// Property accessors

    public String getRecvaddr() {
		return recvaddr;
	}


	public void setRecvaddr(String recvaddr) {
		this.recvaddr = recvaddr;
	}


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

    public String getPassword() {
        return this.password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }

    public Date getRegistertime() {
        return this.registertime;
    }
    
    public void setRegistertime(Date registertime) {
        this.registertime = registertime;
    }

    public Integer getIntegrate() {
        return this.integrate;
    }
    
    public void setIntegrate(Integer integrate) {
        this.integrate = integrate;
    }

    public String getNickname() {
        return this.nickname;
    }
    
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return this.email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return this.phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getBirthday() {
        return this.birthday;
    }
    
    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getAddr() {
        return this.addr;
    }
    
    public void setAddr(String addr) {
        this.addr = addr;
    }


	public String getLoginName() {
		return LoginName;
	}


	public void setLoginName(String loginName) {
		LoginName = loginName;
	}


	public String getName() {
		return Name;
	}


	public void setName(String name) {
		Name = name;
	}


	public String getDeptCode() {
		return DeptCode;
	}


	public void setDeptCode(String deptCode) {
		DeptCode = deptCode;
	}


	public String getDeptName() {
		return DeptName;
	}


	public void setDeptName(String deptName) {
		DeptName = deptName;
	}
   
 







}