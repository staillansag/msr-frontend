final String PARAMETERS_FILE = 'parameters.yml'
final String CAAS_CONFIG_FILE_ID = 'caas-subscriptions-v4'
final String REPOSITORY_DEPLOYMENT_PREFIX = 'resources/openshift'

String outputISName

pipeline {
   agent {
      label 'agent-openshift-latest'
   }
   options {
      buildDiscarder(logRotator(daysToKeepStr:'15', numToKeepStr:'15', artifactDaysToKeepStr: '', artifactNumToKeepStr:'3'))
      timeout(time: 120, unit: 'MINUTES')
      timestamps()
      ansiColor('xterm')
      durabilityHint('PERFORMANCE_OPTIMIZED')
   }

   stages{

      /*
      * Loading and reading JSON configuration filel
      */
      stage("Load files") {
         steps {
            script {
               configFileProvider([configFile(fileId: "${CAAS_CONFIG_FILE_ID}", targetLocation: "${CAAS_CONFIG_FILE_ID}.json")]) {
                  caasConfig = readJSON file: "${CAAS_CONFIG_FILE_ID}.json"
               }                           

               /*
                * Set build env variables
               */
               if (! fileExists(PARAMETERS_FILE)) {
                  error("[ERROR] - Image parameters file ${PARAMETERS_FILE} not exists !")
               }

               /*
                * Get Parameters from parameters.yml file
                */
               myParameters = readYaml file: "${PARAMETERS_FILE}"
               fromImage = myParameters.parameters.FROM_IMAGE.trim()
               fromNamespace = myParameters.parameters.FROM_NAMESPACE.trim()
               applicationName = myParameters.parameters.APPLICATION_NAME.trim()
               imageName = myParameters.parameters.IMAGE_NAME.trim()
               maintainerEmail = myParameters.parameters.MAINTAINER_EMAIL.trim()
               namespaceDeploy = myParameters.parameters.DEPLOY_NAMESPACE.trim()

            }
         }
      }

      /*
      * Check parameters not empty and files correct
      */
      stage("Parameters and files analyze"){
          steps{
            script {
               if (fromImage.length() == 0
                    || fromNamespace.length() == 0
                    || applicationName.length() == 0
                    || imageName.length() == 0
                    || maintainerEmail.length() == 0
                    || namespaceDeploy.length() == 0
                  ) {
                  error("[ERROR] - Missing required parameters in ${PARAMETERS_FILE}.")
               }

               try {
                  currentCaasConfig = caasConfig["${namespaceDeploy.replaceAll('_', '-')}"]
               } catch (Exception e) {
                  error("[ERROR] - Entry with key ${namespaceDeploy.replaceAll('_', '-')} not found in ${CAAS_CONFIG_FILE_ID} file")
               }

            }
         }
      }

      /*
      * Check code quality (YAML lint, Dockerfile lint)
      */
      stage("Code analyze"){
          steps{
            println("[INFO] - Not implemented yet")
          }
      }


      stage('Build') {
         steps{
            script {
               openshift.withCluster(currentCaasConfig['url']) {
                  openshift.withProject(currentCaasConfig['namespaceName']){
                     openshift.withCredentials(currentCaasConfig['serviceAccountCredentialId']){

                        imageReleaseDate = sh (script: "date --rfc-3339=seconds", returnStdout: true).trim()

                        /*
                         * Load and process template
                         */
                        def template = readYaml file: "${REPOSITORY_DEPLOYMENT_PREFIX}/build-template.yml"
                        def processedTemplate = openshift.process(template ,
                           "-p", "IMAGE_NAME='${imageName}'",
                           "-p", "GIT_URL='${env.GIT_URL}'",
                           "-p", "GIT_BRANCH='${env.GIT_BRANCH}'",
                           "-p", "IMAGE_RELEASE_DATE='${imageReleaseDate}'",
                           "-p", "IMAGE_RELEASE_NUMBER='${env.BUILD_NUMBER}'",
                           "-p", "FROM_IMAGE='${fromImage}'",
                           "-p", "FROM_NAMESPACE='${fromNamespace}'",
                           "-p", "APPLICATION_NAME='${applicationName}'",
                           "-p", "MAINTAINER_EMAIL='${maintainerEmail}'",
                           "-p", "IMAGE_TAG='${fromImage}':'${env.BUILD_NUMBER}'")

                        println "[INFO] - Generated build template:"
                        print processedTemplate

                        /*
                        * Create BuildConfig, or update if BuildConfig already exist
                        */

                        
                        if (openshift.selector("bc", "bc-${applicationName}-${imageName}").exists()){
                           println("[INFO] - Buildconfig bc-${applicationName}-${imageName} already exists")
                           openshift.apply(processedTemplate)

                           /*
                           * Start build
                           */
                           openshift.raw("start-build", "bc-${applicationName}-${imageName}")
                        }
                        else
                        {
                           println("[INFO] - Creating bc-${applicationName}-${imageName}")
                           openshift.create(processedTemplate)
                        }

                        /*
                        * Follow build
                        * Get Last Build and check his status
                        * Start build if no build running
                        */
                        println("[INFO] - Follow build")
                        def bc = openshift.selector("bc", "bc-${applicationName}-${imageName}")

                        lastVersionBC = bc.object().status.lastVersion

                        if (lastVersionBC == 0)
                        {
                           openshift.raw("start-build", "bc-${applicationName}-${imageName}")
                        }

                        /*
                        * Check build after running
                        */
                        def b = openshift.selector("build", "bc-${applicationName}-${imageName}-${lastVersionBC}")
                        
                        /*
                        * Check build before running
                        */
                        timeout(5) {
                           waitUntil {
                              return b.object().status.phase == 'Running'
                           }
                        }

                        println("[INFO] - Follow build")
                        
                        bc.logs('-f')

                        /*
                        * Check build after running
                        */
                        timeout(1) {
                           waitUntil {
                              return b.object().status.phase != 'Running'
                           }
                        }

                        try {
                          bc.logs("--follow")
                        } catch (Exception e) {
                          println("[ERROR] - Build error")
                        }

                        timeout(5) {
                           waitUntil {
                             return b.object().status.phase != 'Running'
                           }
                        }

                        lastStatus = b.object().status.phase
                        
                        if (lastStatus != 'Complete') {
                           error("[ERROR] Build failed.")
                        }
            
                        println("[INFO] - Adding tag latest to built image")
                        openshift.raw("tag", "is-${applicationName}-${imageName}:'${env.BUILD_NUMBER}'" , "is-${applicationName}-${imageName}:latest" )

                     }
                  }
               }
            }
         }
      }

   }
}
