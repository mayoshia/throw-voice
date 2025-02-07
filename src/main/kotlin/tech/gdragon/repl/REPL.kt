/**
 * Heavily inspired by https://github.com/nrepl/nrepl-java-example
 */
package tech.gdragon.repl

import clojure.java.api.Clojure
import mu.KotlinLogging
import java.io.Closeable

class REPL {
  val logger = KotlinLogging.logger { }
  val port = "7888"
  private var _server: Closeable? = null

  init {
    val require = Clojure.`var`("clojure.core", "require")
    require.invoke(Clojure.read("nrepl.server"))

    val start = Clojure.`var`("nrepl.server", "start-server")
    _server = start.invoke(Clojure.read(":port"), Clojure.read(port)) as Closeable

    logger.info {
      "Starting nREPL Server on port $port"
    }
  }

  fun shutdown(): Unit {
    logger.info {
      "Stopping nREPL Server on port $port"
    }
    _server?.close()
  }
}
