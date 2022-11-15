package fr.charlotte.config

import fr.charlotte.Provider

object Utils {
  val VERSION = "1.6.1"
  private var defaultProvider: Provider = null

  def setDefaultProvider(p: Provider): Unit = this.defaultProvider = p

  def getDefaultProvider: Provider = defaultProvider
}
