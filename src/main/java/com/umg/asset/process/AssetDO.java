package com.umg.asset.process;

import java.io.Serializable;

import lombok.Data;

@Data
public class AssetDO implements Serializable {

	private String user_id;

	private String track_id;
	
	private String region;

	private String cached;

	private String os;

	private String zip_code;

	private String isrc;

	private String source_uri;

	private String device_type;

	private String stream_country;

	private String referral;

	private String type;

	private String access;

	private String birth_year;

	private String country;

	private String version;

	private String product;

	private String timestamp;

	private String message;

	private String album_code;

	private String report_date;

	private String source;

	private String length;

	private String gender;

	private String partner;
	
	private String track_artists;
	
	private String album_artist;


}
