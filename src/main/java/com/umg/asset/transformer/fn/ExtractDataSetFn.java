package com.umg.asset.transformer.fn;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.dataflow.sdk.io.TextIO;
import com.google.cloud.dataflow.sdk.transforms.DoFn;
import com.google.cloud.dataflow.sdk.transforms.PTransform;
import com.google.cloud.dataflow.sdk.transforms.ParDo;
import com.google.cloud.dataflow.sdk.transforms.join.CoGbkResult;
import com.google.cloud.dataflow.sdk.transforms.join.CoGroupByKey;
import com.google.cloud.dataflow.sdk.transforms.join.KeyedPCollectionTuple;
import com.google.cloud.dataflow.sdk.values.KV;
import com.google.cloud.dataflow.sdk.values.PCollection;
import com.google.cloud.dataflow.sdk.values.TupleTag;
import com.umg.asset.process.AssetDO;

@Component
@Slf4j
public class ExtractDataSetFn extends PTransform<PCollection<String>, PCollection<String>> {
	

	
	@Override
	public PCollection<String> apply(PCollection<String> input) {

		FeedOptions options = input.getPipeline().getOptions()
				.as(FeedOptions.class);

		PCollection<String> streamsAttributes = input.apply(
				"ReadLines from streams file", 
				TextIO.Read.named("streams")
						.from(options.getStreamsFile()).withoutValidation()
						.withCompressionType(TextIO.CompressionType.GZIP));
		
		PCollection<String> tracksAttributes = input.apply(
				"ReadLines from tracks file",
				TextIO.Read.named("tracks").from(options.getTracksFile())
						.withoutValidation()
						.withCompressionType(TextIO.CompressionType.GZIP));
		
		PCollection<String> usersAttributes = input.apply(
				"ReadLines from users file",
				TextIO.Read.named("users").from(options.getUsersInputFile())
						.withoutValidation()
						.withCompressionType(TextIO.CompressionType.GZIP));

		return joinDataSet(streamsAttributes, tracksAttributes, usersAttributes);

	}

	public PCollection<String> joinDataSet(PCollection<String> streamsData,
			PCollection<String> tracksData,PCollection<String> usersData) {
	
		final TupleTag<Streams> streamInfoTag = new TupleTag<Streams>();
		final TupleTag<Tracks> tracksInfoTag = new TupleTag<Tracks>();
		
		final TupleTag<Users> usersInfoTag = new TupleTag<Users>();
		final TupleTag<AssetDO> streamInfoTag_2 = new TupleTag<AssetDO>();
		
		
		PCollection<KV<String, Streams>> streamDataInfo = streamsData.apply(ParDo.of(new ExtractStreamInfoDataFn()));
		PCollection<KV<String, Tracks>> tracksDataInfo = tracksData.apply(ParDo.of(new ExtractTracksInfoFn()));	
		PCollection<KV<String, Users>> usersDataInfo = usersData.apply(ParDo.of(new ExtractUserInfoFn()));
		
		//Lets have merge first two collection with command key Track_id
		PCollection<KV<String, CoGbkResult>> kvpCollection = KeyedPCollectionTuple				
				.of(tracksInfoTag, tracksDataInfo)
				.and(streamInfoTag, streamDataInfo)					
				.apply(CoGroupByKey.<String> create());

		PCollection<KV<String, AssetDO>>firstCollection = kvpCollection
				.apply(ParDo
						.named("Process")
						.of(new DoFn<KV<String, CoGbkResult>, KV<String, AssetDO>>() {
							@Override
							public void processElement(ProcessContext c) {
								KV<String, CoGbkResult> e = c.element();

								AssetDO assetDO = new AssetDO();								
							    for (Streams steamValues  : e.getValue().getAll(streamInfoTag)) {
							    	assetDO.setUser_id(steamValues.getUser_id());
							    	assetDO.setLength(steamValues.getLength());
							    	assetDO.setReport_date(steamValues.getReport_date());
							    	assetDO.setVersion(steamValues.getVersion());
								}								
								for (Tracks trackdata : e.getValue().getAll(tracksInfoTag)) {	
									assetDO.setAlbum_artist(trackdata.getAlbum_artist());
									assetDO.setAlbum_code( trackdata.getAlbum_code());									
									assetDO.setTrack_id(trackdata.getTrack_id());
									assetDO.setTrack_artists(trackdata.getTrack_artists());
								}
								
								c.output(KV.of(assetDO.getUser_id(),assetDO));
							}
						}));
	
		
		
		//Lets have merge second two collection with command key user_id
	PCollection<KV<String, CoGbkResult>> sceondCollection = KeyedPCollectionTuple				
						.of(streamInfoTag_2, firstCollection)
						.and(usersInfoTag, usersDataInfo)					
						.apply(CoGroupByKey.<String> create());
		
		PCollection<KV<String, AssetDO>>finalResult = sceondCollection
				.apply(ParDo
						.named("Process-second")
						.of(new DoFn<KV<String, CoGbkResult>, KV<String, AssetDO>>() {
							@Override
							public void processElement(ProcessContext c) {
								KV<String, CoGbkResult> e = c.element();
								
								for (AssetDO assetDO : e.getValue().getAll(streamInfoTag_2)) {			
									Users user=e.getValue().getOnly(usersInfoTag);
									assetDO.setGender(user.getGender());
									assetDO.setCountry(user.getCountry());
									assetDO.setPartner(user.getPartner());
									assetDO.setProduct(user.getProduct());
									assetDO.setBirth_year(user.getBirth_year());
									assetDO.setRegion(user.getRegion());
									assetDO.setZip_code(user.getZip_code());									
									c.output(KV.of(e.getKey(),assetDO));
								}
							}
						}));

		PCollection<String> formattedResults = finalResult
				.apply(ParDo.named("Format").of(
						new DoFn<KV<String, AssetDO>, String>() {
							@Override
							public void processElement(ProcessContext c) {
								
								ObjectMapper mapper = new ObjectMapper();
								AssetDO assetDO = c.element().getValue();
								// Convert object to JSON string and pretty print
								try {
									String jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(assetDO);
									log.info("New album created....."
											+ jsonInString);
									c.output(jsonInString);
								} catch (JsonProcessingException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								
							
							}
						}));

		return formattedResults;
	}

	static class ExtractStreamInfoDataFn extends
			DoFn<String, KV<String, Streams>> {
		private static final long serialVersionUID = -7335104807353946213L;

		@Override
		public void processElement(ProcessContext c) {
			
			ObjectMapper mapper = new ObjectMapper();
			try {
				Streams streamdata=mapper.readValue(c.element(), Streams.class);				
				c.output(KV.of(streamdata.getTrack_id(), streamdata ));
			} catch ( IOException  e) {				
				e.printStackTrace();
			} 
			
		}
	}

	static class ExtractTracksInfoFn extends
			DoFn<String, KV<String, Tracks>> {
		private static final long serialVersionUID = 4783787393271367614L;

		@Override
		public void processElement(ProcessContext c) {
			ObjectMapper mapper = new ObjectMapper();			
			try {
				Tracks trackdata=mapper.readValue(c.element(), Tracks.class);
				c.output(KV.of(trackdata.getTrack_id(),trackdata));
			} catch ( IOException  e) {				
				e.printStackTrace();
			} 
		}
	}
	
	static class ExtractUserInfoFn extends
			DoFn<String, KV<String, Users>> {
	private static final long serialVersionUID = -131071086493812521L;

		@Override
		public void processElement(ProcessContext c) {			
				ObjectMapper mapper = new ObjectMapper();			
			try {
				Users userdata=mapper.readValue(c.element(), Users.class);								
				c.output(KV.of(userdata.getUser_id(),userdata));
			} catch ( IOException  e) {				
				e.printStackTrace();
			} 
		}
}

}

