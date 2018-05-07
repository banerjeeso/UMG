#!/bin/bash

mvn spring-boot:run -Drun.arguments="--project=hd-www-dev,\
--stagingLocation=gs://hd-www-dev-data/staging,\
--dataflowJobFile=gs://hd-www-dev-data/templates/UMGTemplate,\
--numWorkers=10,\
--network=internal,\
--maxNumWorkers=10,\
--zone=us-east1-c,\
--outputFile=gs://hd-www-dev-data/extracts/dataflow-output,\
--runner=TemplatingDataflowPipelineRunner"
