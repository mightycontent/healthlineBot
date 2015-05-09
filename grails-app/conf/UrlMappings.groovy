class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?" {
            constraints {
                // apply constraints here
            }
        }
        "/tag"(controller: "IndexRequest", action: "save")
        "/tagstatus/$id?(.$format)?"(controller: "IndexRequest", action: "status")
        "/"(view: "/index")
        "500"(view: '/error')
    }
}
