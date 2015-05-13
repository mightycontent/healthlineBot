package myrest



class DoIndexJob {

    def IndexDocService
    // don't start job if job is still running
    def concurrent = false
    static triggers = {
      simple name: 'indexed', repeatInterval: 20000l // execute job once in 5 seconds
    }

    def execute() {
        // execute job
        println "Processed ${IndexDocService.send()} request(s)"
    }
}
