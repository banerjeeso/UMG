#!/bin/bash

gcloud beta functions deploy imageConversionFunction --stage-bucket gs://catalog-dataflow-functions/ --trigger-bucket gs://catalog-dataflow-input
