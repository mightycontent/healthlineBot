package myrest



class DoIndexJob {

    def IndexDocService
    // don't start job if job is still running
    def concurrent = false
    static triggers = {
      simple name: 'indexed', repeatInterval: 20000l // execute job once in n milliseconds
    }

    def execute() {
        // execute job
        //log.info "Executing IndexDocService"
        log.info "Processed ${IndexDocService.send()} request(s)"
    }
}
