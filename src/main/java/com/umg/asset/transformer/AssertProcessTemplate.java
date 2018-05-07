package com.umg.asset.transformer;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.cloud.dataflow.sdk.Pipeline;
import com.google.cloud.dataflow.sdk.io.TextIO;
import com.google.cloud.dataflow.sdk.options.PipelineOptionsFactory;
import com.google.cloud.dataflow.sdk.transforms.Create;
import com.google.cloud.dataflow.sdk.values.PCollection;
import com.umg.asset.transformer.fn.ExtractDataSetFn;
import com.umg.asset.transformer.fn.FeedOptions;

/**
 * Created by Somnath.
 */
@Component
@Slf4j
public class AssertProcessTemplate {

	@Autowired
	ExtractDataSetFn extractDataSetFn;

	public void run(String[] args) throws Exception {

		// Start by defining the options for the pipeline.
		FeedOptions options = PipelineOptionsFactory.fromArgs(args)
				.withValidation().as(FeedOptions.class);

		// Then create the pipeline.
		Pipeline p = Pipeline.create(options);

		// apply pipeline for Transform
		PCollection<String> output = p.apply(Create.of("Begin-Pipeine")).apply("Extract DataSet", extractDataSetFn);
		output.apply("Write Result",TextIO.Write.to(options.getOutputFile()));
		log.info("Dataflow -completed");

		// Run the pipeline.
		p.run();

	}

}
