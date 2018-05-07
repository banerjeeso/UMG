package com.umg.asset.transformer.fn;

import java.io.Serializable;

import lombok.Data;

@Data
public class Streams  implements Serializable{

	  private String cached;

	    private String os;

	    private String device_type;

	    private String stream_country;

	    private String version;

	    private String message;

	    private String timestamp;

	    private String report_date;

	    private String source_uri;

	    private String source;

	    private String track_id;

	    private String length;

	    private String user_id;
}
