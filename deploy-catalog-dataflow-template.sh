#!/bin/bash

mvn spring-boot:run -Drun.arguments="--project=hd-www-dev,\
--stagingLocation=gs://hd-www-dev-catalog-data/staging,\
--dataflowJobFile=gs://hd-www-dev-catalog-data/templates/UMGTemplate,\
--numWorkers=10,\
--network=internal,\
--maxNumWorkers=20,\
--zone=us-east1-c,\
--outputFile=gs://hd-www-dev-catalog-data/extracts/catalog-dataflow-output,\
--runner=TemplatingDataflowPipelineRunner"
