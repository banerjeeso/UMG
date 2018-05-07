var googleapis = require('googleapis');

exports.imageConversionFunction = function imageConversionFunction (event, callback) {
  const file = event.data;
  const bucket = file.bucket;
  const fname = file.name;
  const filePath = "gs://" + bucket + "/" + fname;
  const isDelete = file.resourceState === 'not_exists';
  const template = 'gs://hd-www-dev-catalog-data/templates/imageConversionTemplate';
  const tempLocation = 'gs://hd-www-dev-catalog-data/temp';
  const zone = 'us-central1-b';
  const projectIdd = 'hd-www-dev';
  const jName='image-conversion-dataflow';
  var isOK = false;

  if (fname.startsWith("flow-catalog-batch")) {
      isOK = true;
  }

  if (isOK && isDelete) {
    console.log("Extract input file ${filePath} is deleted.");
  } else if (isOK) {
    console.log("Extract input file ${filePath} is uploaded. Will kick a certona extract job from template ${template}.");


   googleapis.auth.getApplicationDefault(function (err, authClient, projectId) {
     if (err) {
       throw err;
     }

     if (authClient.createScopedRequired && authClient.createScopedRequired()) {
       authClient = authClient.createScoped([
         'https://www.googleapis.com/auth/compute',
         'https://www.googleapis.com/auth/cloud-platform',
         'https://www.googleapis.com/auth/userinfo.email'
       ]);
     }

     var dataflow = googleapis.dataflow({
       version: 'v1b3',
       auth: authClient
     });

     var launchTemplateParameters = {
        'jobName': jName,
        'parameters': {
            'inputFile': filePath
        },
         environment: {
           'tempLocation': tempLocation,
           'zone': zone
       }
     };

     var params = {
       'projectId': projectIdd,
       'gcsPath': template,
       'auth': authClient,
       'resource': launchTemplateParameters  
     };

     var args = {
       url: 'https://dataflow.googleapis.com/v1b3/projects/hd-www-dev/templates',
       method: 'POST',
     };

      dataflow.projects.templates.launch( params, args, function (err, result) {
        console.log(err, result);
     });

    });

  }  // end of else

  callback();
};

