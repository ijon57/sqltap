package com.paulasmuth.sqltap

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.nio.SelectChannelConnector
import org.eclipse.jetty.util.thread.QueuedThreadPool
import java.io.PrintStream
import java.io.OutputStream

class HTTPServer(port: Int, num_threads: Int) {
  org.eclipse.jetty.util.log.Log.setLog(null);

  val pool = new QueuedThreadPool
  pool.setMaxThreads(num_threads)

  val server = new Server
  server.setThreadPool(pool)
  server.setGracefulShutdown(1000)
  server.setHandler(new HTTPHandler)
  without_stderr(_ => server.start())

  val connector = new SelectChannelConnector
  connector.setPort(port)
  server.addConnector(connector)
  without_stderr(_ => connector.start())

  SQLTap.log("Listening on http://0.0.0.0:" + port)

  def without_stderr(lambda: Unit => Unit) = {
    val stderr = System.err

    val dummy = new PrintStream(new OutputStream(){
      def write(b: Int) : Unit = ()
    })

    System.setErr(dummy)
    lambda()
    System.setErr(stderr)
  }

}
