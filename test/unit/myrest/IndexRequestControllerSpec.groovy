package myrest


import grails.test.mixin.*
import spock.lang.*

@TestFor(IndexRequestController)
@Mock(IndexRequest)
class IndexRequestControllerSpec extends Specification {

    def populateValidParams(params) {
        assert params != null
        params['doc_id']        = "myRSuiteId"
        params['partner_id']    = "b975e778-a19d-11e4-89d3-123b93f75cba"
        params['profile_id']    = "b975e_profile"
        params['content']       = "<html><head><title>The quick brown fox</title></head><body>The quick brown fox ran over the lazy dog</body></html>"
        params['callback_url']  = "http://api.partner.com/healthline/handle_callback"
        params['command']       = "update"
    }

    void "Test the index action returns the correct model"() {

        when: "The index action is executed"
        controller.index()

        then: "The model is correct"
        !model.indexRequestInstanceList
        model.indexRequestInstanceCount == 0
    }

    void "Test the create action returns the correct model"() {
        when: "The create action is executed"
        controller.create()

        then: "The model is correctly created"
        model.indexRequestInstance != null
    }

    void "Test the save action correctly persists an instance"() {
        def beforecount = IndexRequest.count

        when: "The save action is executed with an invalid instance"
        request.contentType = FORM_CONTENT_TYPE
        request.method = 'POST'
        def indexRequest = new IndexRequest()
        indexRequest.validate()
        controller.save(indexRequest)

        then: "The create view is rendered again with the correct model"
        model.indexRequestInstance != null
        view == 'create'

        when: "The save action is executed with a valid instance"
        response.reset()
        // assuming save worked
        populateValidParams(params)
        indexRequest = new IndexRequest(params)
        indexRequest.validate()
        controller.save(indexRequest)

        then: "The count is incremented"
        IndexRequest.count() == beforecount + 1

        when: "A domain instance is passed to the show action"
        def indexRequest2 = new IndexRequest(['doc_id':'myRSuiteId'])
        controller.show(indexRequest2)

        then: "we get an OK resonse"
        response.status == 200
    }

    void "Test the show action "() {
        when: "The show action is executed with a null domain"
        controller.show(null)

        then: "A 404 error is returned"
        response.status == 404
    }

    void "Test that the edit action returns the correct model"() {
        when: "The edit action is executed with a null domain"
        controller.edit(null)

        then: "A 404 error is returned"
        response.status == 404

        when: "A domain instance is passed to the edit action"
        populateValidParams(params)
        def indexRequest = new IndexRequest(params)
        controller.edit(indexRequest)

        then: "A model is populated containing the domain instance"
        model.indexRequestInstance == indexRequest
    }

    void "Test the update action performs an update on a valid domain instance"() {
        when: "Update is called for a domain instance that doesn't exist"
        request.contentType = FORM_CONTENT_TYPE
        request.method = 'PUT'
        controller.update(null)

        then: "A 404 error is returned"
        response.redirectedUrl == '/indexRequest/index'
        flash.message != null


        when: "An invalid domain instance is passed to the update action"
        response.reset()
        def indexRequest = new IndexRequest()
        indexRequest.validate()
        controller.update(indexRequest)

        then: "The edit view is rendered again with the invalid instance"
        view == 'edit'
        model.indexRequestInstance == indexRequest

        when: "A valid domain instance is passed to the update action"
        response.reset()
        populateValidParams(params)
        indexRequest = new IndexRequest(params).save(flush: true)
        controller.update(indexRequest)

        then: "A redirect is issues to the show action"
        response.redirectedUrl == "/indexRequest/show/$indexRequest.id"
        flash.message != null
    }

    void "Test that the delete action deletes an instance if it exists"() {
        when: "The delete action is called for a null instance"
        request.contentType = FORM_CONTENT_TYPE
        request.method = 'DELETE'
        controller.delete(null)

        then: "A 404 is returned"
        response.redirectedUrl == '/indexRequest/index'
        flash.message != null

        when: "A domain instance is created"
        response.reset()
        populateValidParams(params)
        def indexRequest = new IndexRequest(params).save(flush: true)

        then: "It exists"
        IndexRequest.count() == 1

        when: "The domain instance is passed to the delete action"
        controller.delete(indexRequest)

        then: "The instance is deleted"
        IndexRequest.count() == 0
        response.redirectedUrl == '/indexRequest/index'
        flash.message != null
    }
}
