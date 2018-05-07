package com.umg.asset.transformer.fn;

import com.google.cloud.dataflow.sdk.options.DataflowPipelineOptions;
import com.google.cloud.dataflow.sdk.options.Default;
import com.google.cloud.dataflow.sdk.options.Description;
import com.google.cloud.dataflow.sdk.options.ValueProvider;

/**
 * Created by sxb8999.
 */
public interface FeedOptions extends DataflowPipelineOptions {
			
	 	@Description("Path of the input Bucket to read feed file ")   
	 	@Default.String("gs://catalog-dataflow/extracts/input/streams.gz")	 	
	 	ValueProvider<String> getStreamsFile();	   
	    void setStreamsFile(ValueProvider<String> value);	
	    
	    @Description("Path of the input Bucket to read feed file ") 
	    @Default.String("gs://catalog-dataflow/extracts/input/tracks.gz")
	 	ValueProvider<String> getTracksFile();	   
	    void setTracksFile(ValueProvider<String> value);	
	    
	    @Description("Path of the input Bucket to read feed file ") 
	    @Default.String("gs://catalog-dataflow/extracts/input/users.gz")
	 	ValueProvider<String> getUsersInputFile();	   
	    void setUsersInputFile(ValueProvider<String> value);	
	   
	    @Description("Path of the file to write to")
	    @Default.String("gs://catalog-dataflow/extracts/input/result.txt")
	    ValueProvider<String> getOutputFile();
	    void setOutputFile(ValueProvider<String> value);	   

}
