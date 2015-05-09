
import myrest.IndexRequest

class BootStrap {

    def init = { servletContext ->
        def req = new IndexRequest(
                partner_id: "b975e778-a19d-11e4-89d3-123b93f75cba",
                profile_id: "b975e_profile",
                content: "<html><head><title>The quick brown fox</title></head><body>The quick brown fox ran over the lazy dog</body></html>",
                callback_url: "http://api.partner.com/healthline/handle_callback",
                command: "update"
        )
        if (!req.save(flush: true)) {
            log.error "trouble saving request"
            req.errors.allErrors.each {
                log.error it
            }
        }
    }
    def destroy = {
    }
}
