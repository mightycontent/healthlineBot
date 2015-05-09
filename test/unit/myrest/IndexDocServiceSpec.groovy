package myrest

import grails.plugins.rest.client.RestBuilder
import grails.test.mixin.TestFor
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.web.client.MockRestServiceServer
import spock.lang.Specification

import static org.hamcrest.CoreMatchers.anything
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(IndexDocService)
class IndexDocServiceSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    def "Test PUT request"() {
        given:"A REST client instance"
        def rest = new RestBuilder()
        MockRestServiceServer mockServer = mockRSuiteHHP(rest)

        when:"A request is issued"
        def resp = rest.put("http://cms.hhp.org/rsuite/index_callback") {
            contentType('application/json')
            json {
                doc_id = "12345"
            }
        }

        then:"A success status is returned"
        resp != null
        resp.status == 201
    }

    public static MockRestServiceServer mockRSuiteHHP(RestBuilder rest) {
        final mockServer = MockRestServiceServer.createServer(rest.restTemplate)

        mockServer.expect(requestTo("http://cms.hhp.org/rsuite/index_callback"))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(content().contentType("application/json"))
                .andExpect(content().string('{"doc_id":"12345"}'))
                .andRespond(withStatus(HttpStatus.CREATED))

        mockServer
    }
}
