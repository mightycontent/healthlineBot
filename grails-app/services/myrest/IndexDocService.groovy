package myrest

import grails.transaction.Transactional
import grails.plugins.rest.client.RestBuilder
import groovy.json.JsonBuilder
import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import groovyx.net.http.ContentType.*


@Transactional
class IndexDocService {
    def send() {
        def rest = new RestBuilder()
        List queued = IndexRequest.findAllWhere(status: "queued")
        queued.each { req ->
            log.info "request: ${req.doc_id} is about to be processed!"
            // call the callback_url and return
            def callBack = req.callback_url ?: 'http://localhost'
            try {
/*
                // make the call using restBuilder
                // In this version we expect the json to be in the body as per healthline spec
                // appaently RSuite can not accept data in body.
                def resp = rest.put(callBack) {
                    auth 'healthline', 'linehealth'
                    //header ('Authorization', "Basic ${"healthline:linehealth".bytes.encodeBase64().toString()}")
                    contentType 'application/json'
                    json {
                        partner_id = req.partner_id
                        profile_id = req.profile_id
                        doc_id = req.doc_id
                        status = req.status
                        tags = tags()
                    }
                }
*/
                // this implementation sends the JSON response as a query param named file
                // the was probably should be a content type of multi-part/form but, team want URLENC
                //

                def builder = new HTTPBuilder(callBack)

                def json = new JsonBuilder()
                def root = json {
                    'partner_id'    req.partner_id
                    'profile_id'    req.profile_id
                    'doc_id'        req.doc_id
                    'status'        req.status
                    'tags'          this.tags
                }
                log.info "json: ${json.toString()}"

                builder.auth.basic('healthline', 'linehealth')
                builder.request(Method.PUT,ContentType.URLENC) { req2->
                    uri.query = ['file': json.toString()]

                    response.success = { resp, reader ->
                        // restbuilder
                        // log.info "Callback to url: ${callBack} returned a status of: ${resp.status}, ${resp.statusCode.getReasonPhrase()}"
                        log.info "Callback to url: ${callBack} returned as status code of ${resp.status}, ${resp.statusLine}"
                    }
                    response.failure = { resp, reader ->
                        //log.info "Drat! Callback to url: ${callBack} returned a status of: ${resp.status}, ${resp.statusCode.getReasonPhrase()}"
                        log.info "Drat, Callback to url: ${callBack} returned as status code of ${resp.status}, ${resp.statusLine}"
                    }
                }

            }
            catch (Exception e) {
                // most likely we can't reach the provided callback
                log.error "In IndexDocServer.send(), caught exception: ${e.message}"
            }

            //update the status to ok. Do this even if callBack failed to prevent retrying infinately*/
            req.status = "ok"
            req.save(flush: true)
        }
        return queued.size()
    }

    List getTags() {
        return tags()
    }

    private static List tags() {
        Map sample = [
                'concept_rank'                  : 1,
                'score'                         : 118.99007,
                'concept consumer friendly name': 'Acute Bronchitis',
                'concept medical name'          : 'Acute bronchitis',
                'marketing category - k1'       : 'bronchitis',
                'marketing category - k2'       : 'respiratory',
                'marketing category - k3'       : 'health',
                'semantic_group'                : 'diseases',
                'semantic_types'                : 'Disease or Syndrome',
                'mesh'                          : null,
                'cpt'                           : null,
                'icd9'                          : null,
                'icd9diag'                      : 466.0,
                'icd9proc'                      : null,
                'icd10cm'                       : ['J20.9', 'J20.8', 'J20.7', 'J20.6', 'J20.5', 'J20.4', 'J20.3', 'J20.2', 'J20.1', 'J20.0'],
                'icd10pcs'                      : null,
                'hcpcs'                         : null,
                'loinc'                         : null,
                'ndc'                           : null,
                'rxnorm'                        : null,
                'snomed'                        : [266380005, 195713004, 155512004, 10509002],
                'treatments'                    : ['Drink Plenty of Fluids', 'Mist (Humidifying) Inhalation Therapy','Rest', 'Symptom Management','Antibiotic Therapy', 'Influenza vaccination', 'High zinc diet'],
                'physicians'                    : ['Internist','Pulmonologist', 'Family Practice Physician'],
                'diagnostics'                   : ['Chest X-Ray', 'History and Physical Exam', 'Respiratory System Examination'],
                'symptoms'                      : ['Cough', 'Fever', 'Wheezing', 'Shortness of Breath'],
                'diseases'                      : null,
                'drug_brands'                   : ['Biaxin XL','Biaxin XL-Pak', 'Biaxin'],
                'drug_generic'                  : ['clarithromycin', 'albuterol', 'codeine-guaifenesin', 'sulfamethoxazole-trimethoprim'],
                'synonyms'                      : null,
                'parents'                       : ['Bronchitis', 'Acute Respiratory Disease', 'Acute Inflammatory Disease'],
                'children'                      : ['Acute Bronchitis Causes', 'Acute Bronchitis Risk Factors', 'Acute Bronchitis Symptoms']
        ]

        return [sample]

    }
}
