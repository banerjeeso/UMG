package com.umg.asset.transformer.fn;

import java.io.Serializable;

import lombok.Data;

@Data
public class Tracks  implements Serializable{
	
	private String message;

    private String album_code;

    private String track_artists;

    private String isrc;

    private String album_artist;

    private String album_name;

    private String uri;

    private String track_id;

    private String track_name;

    private String version;

}
