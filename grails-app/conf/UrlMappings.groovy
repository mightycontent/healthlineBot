class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?" {
            constraints {
                // apply constraints here
            }
        }
        "/tag"(controller: "IndexRequest", action: "saveJson")
        "/tagstatus/$doc_id?(.$format)?"(controller: "IndexRequest", action: "status")
        "/"(view: "/index")
        "500"(view: '/error')
    }
}
