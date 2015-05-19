package myrest


class IndexRequest {
    String partner_id
    String profile_id
    String doc_id
    String content
    String callback_url
    String command
    String status = "unknown"

    static constraints = {
        partner_id nullable: false
        profile_id nullable: false
        doc_id nullable: false  // this value is passed in as the handle to which the partner's system will identify
                                // the document.  The healthline system will remember it.
        content nullable: true
        callback_url url: true
        command nullable: true
        status inList: ["unknown", "queued", "ok", "fail"]
    }
}
