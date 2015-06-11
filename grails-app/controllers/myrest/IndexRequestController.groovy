package myrest

import grails.converters.JSON
import org.codehaus.groovy.grails.web.json.JSONObject

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class IndexRequestController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]
    def IndexDocService

    // this action gets mapped to url /tagstatus in the /config/UrlMappings
    def status() {
        final List EXCLUDE_PROPS = ['class', 'id', 'content']

        IndexRequest indexRequestInstance = IndexRequest.findByDoc_id(params.doc_id)
        if (indexRequestInstance == null) {
            log.info "tagStatus request not found."
            notFound()
            return
        }

        // doc_id is pass in on url, and is mapped in UrlMappings config
        log.info "received tagStatus request: ${indexRequestInstance.doc_id}"
        Map tags = [:]
        indexRequestInstance.properties.each { key, value ->

            //println "key ${key} value ${value}"
            if (!(key.toString() in EXCLUDE_PROPS)) {
                tags.put(key, value)
            }
        }

        tags.put('tags', IndexDocService.getTags())
        render tags as JSON
    }

    // this action get mapped to /tags in UrlMappings
    @Transactional
    def save(IndexRequest indexRequestInstance) {
        if (indexRequestInstance == null) {
            notFound()
            return
        }
        // simulate an action where we change the status to queued
        indexRequestInstance.status = "queued"

        if (indexRequestInstance.hasErrors()) {
            respond indexRequestInstance.errors, view: 'create'
            return
        }
        indexRequestInstance.save flush: true
        respond indexRequestInstance, [excludes: ['class', 'id', 'content', 'callback_url']]
    }
    // this action get mapped to /tags in UrlMappings
    @Transactional
    def saveJson() {
        // in this method, the body contains the JSON of the form
        //"{doc_id":"8","partner_id":"b975e778-a19d-11e4-89d3-123b93f75cba",
        // "profile_id":"b975e_profile","command":"update",
        // "content" : "<html><head><title>The quick brown fox</title></head><body>The quick brown fox ran over the lazy dog</body></html>",
        // callback_url="http://api.partner.com/handle_callback"}
        JSONObject json = request.JSON
        if (json.isEmpty()) {
            notFound()
            return
        }
        def indexRequestInstance = new IndexRequest()
        // do exclicit binding
        indexRequestInstance.doc_id     =   json.doc_id
        indexRequestInstance.partner_id =   json.partner_id
        indexRequestInstance.command    =   json.command
        indexRequestInstance.profile_id =   json.profile_id
        indexRequestInstance.content    =   json.content
        indexRequestInstance.callback_url = json.callback_url
        indexRequestInstance.status       = "queued"
        if (indexRequestInstance.hasErrors()) {
            respond indexRequestInstance.errors, view: 'create'
            return
        }
        indexRequestInstance.save flush: true
        respond indexRequestInstance, [formats:['json', 'xml'], excludes: ['class', 'id', 'content', 'callback_url']]
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'indexRequest.label', default: 'IndexRequest'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }

    // unused methods
    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond IndexRequest.list(params), model: [indexRequestInstanceCount: IndexRequest.count()]
    }

    def show(IndexRequest indexRequestInstance) {
        if (indexRequestInstance == null) {
            notFound()
            return
        }
        indexRequestInstance.doc_id = indexRequestInstance.id.toString()
        respond indexRequestInstance, [excludes: ['class', 'id']]
    }


    def create() {
        respond new IndexRequest(params)
    }


    def edit(IndexRequest indexRequestInstance) {
        respond indexRequestInstance
    }

    @Transactional
    def update(IndexRequest indexRequestInstance) {
        if (indexRequestInstance == null) {
            notFound()
            return
        }

        if (indexRequestInstance.hasErrors()) {
            respond indexRequestInstance.errors, view: 'edit'
            return
        }

        indexRequestInstance.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'IndexRequest.label', default: 'IndexRequest'), indexRequestInstance.id])
                redirect indexRequestInstance
            }
            '*' { respond indexRequestInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(IndexRequest indexRequestInstance) {

        if (indexRequestInstance == null) {
            notFound()
            return
        }

        indexRequestInstance.delete flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'IndexRequest.label', default: 'IndexRequest'), indexRequestInstance.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NO_CONTENT }
        }
    }

}
