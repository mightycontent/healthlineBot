package myrest

import grails.transaction.Transactional
import grails.plugins.rest.client.RestBuilder

@Transactional
class IndexDocService {
    def send() {
        def rest = new RestBuilder()
        List queued = IndexRequest.findAllWhere(status: "queued")
        queued.each { req ->
            log.info "request: ${req.doc_id} is about to be processed!"
            // call the callback_url with and return
            def callBack = req.callback_url ?: 'http://localhost'
            try {
                def resp = rest.put(callBack) {
                    contentType('application/json')
                    json {
                        partner_id = req.partner_id
                        profile_id = req.profile_id
                        doc_id = req.doc_id
                        status = req.status
                        tags = tags()
                    }
                }

            }
            catch (Exception e) {
                log.error "In IndexDocServer.send(), caught exception: ${e.dump()}"
            }

            //update the status to ok.
            req.status = "ok"
            req.save(flush: true)
        }
        return queued.size()
    }

    List getTags() {
        return tags()
    }

    private List tags() {
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
                'treatments'                    : [8129222: 'Drink Plenty of Fluids', 8098518: 'Mist (Humidifying) Inhalation Therapy', 5344572: 'Rest', 8129498: 'Symptom Management', 8098398: 'Antibiotic Therapy', 8107498: 'Influenza vaccination', 8841858: 'High zinc diet'],
                'physicians'                    : [8111909: 'Internist', 8111907: 'Pulmonologist', 8111905: 'Family Practice Physician'],
                'diagnostics'                   : [5345712: 'Chest X-Ray', 5343665: 'History and Physical Exam', 8092555: 'Respiratory System Examination'],
                'symptoms'                      : [3815056: 'Cough', 5047360: 'Fever', 5047919: 'Wheezing', 5344093: 'Shortness of Breath'],
                'diseases'                      : null,
                'drug_brands'                   : [8005095: 'Biaxin XL', 8124118: 'Biaxin XL-Pak', 2793403: 'Biaxin'],
                'drug_generic'                  : [2792288: 'clarithromycin', 2790827: 'albuterol', 8128221: 'codeine-guaifenesin', 8128426: 'sulfamethoxazole-trimethoprim'],
                'synonyms'                      : null,
                'parents'                       : [2790985: 'Bronchitis', 8108501: 'Acute Respiratory Disease', 8117662: 'Acute Inflammatory Disease'],
                'children'                      : [4974716: 'Acute Bronchitis Causes', 4974714: 'Acute Bronchitis Risk Factors', 4974713: 'Acute Bronchitis Symptoms']
        ]
        def concept1 = [
                name    : "conceptName",
                score   : 0.123,
                somelist: ["item1", "item2", "item3"],
                somemap : ['key1': 'value1', 'key2': 'value2']
        ]

        def concept2 = [
                name    : "conceptName",
                score   : 0.123,
                somelist: ["item1", "item2", "item3"],
                somemap : ['key1': 'value1', 'key2': 'value2']
        ]

        return [sample]

    }
}
