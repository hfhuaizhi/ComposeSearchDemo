package com.hfhuaizhi.composedemo.app

import android.app.Application
import android.content.Context

class SearchDemoApp : Application() {
    companion object {
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = this
    }
}