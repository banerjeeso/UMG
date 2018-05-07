package com.umg.asset.transformer.fn;

import java.io.Serializable;

import lombok.Data;

@Data
public class Users  implements Serializable{
	
	private String region;

    private String zip_code;

    private String referral;

    private String access;

    private String type;

    private String birth_year;

    private String version;

    private String country;

    private String message;

    private String product;

    private String gender;

    private String user_id;

    private String partner;


}
