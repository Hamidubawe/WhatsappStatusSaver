package com.freestatusaver

import android.net.Uri

class StoyrModel {
    var name: String = ""
    var path: String = ""
    var filename: String = ""
    lateinit var uri : Uri

    constructor()

    constructor(name: String, path: String, filename: String, uri: Uri) {
        this.name = name
        this.path = path
        this.filename = filename
        this.uri = uri
    }


}