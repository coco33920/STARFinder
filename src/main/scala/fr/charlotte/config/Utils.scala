package fr.charlotte.config

import fr.charlotte.Provider

object Utils {
    val VERSION = "1.2.0-SNAPSHOT"
    private var defaultProvider: Provider = null
    def setDefaultProvider(p: Provider): Unit = this.defaultProvider = p
    def getDefaultProvider: Provider = defaultProvider
}