import * as http from 'http'
import * as vm from 'vm'
import * as assert from 'assert'
import { AddressInfo } from 'net'
import Obj from 'unlib.js/build/Obj'


type Execution = {
  filename: string
  script: string
  workingDirectory: string
};

(function() {
  process.on('uncaughtException', (err: Error) => { console.error(err.stack) })
})();

(function() {
  function reply(res: http.ServerResponse, statusCode: number, content: string) {
    res.writeHead(statusCode, { 'Content-Type': 'text/plain' })
    return res.end(content)
  }
  let script: vm.Script = null
  // process.on('SIGKILL', () => process.exit(0))  // Cause Error: suv_signal_start EINVAL
  const server = http.createServer((req: http.IncomingMessage, res: http.ServerResponse) => {
    let data: string = ''
    req.on('data', chunk => {
      data += chunk
    })
    req.on('end', () => {
      /* Only one running script is allowed */
      // if(script) {
      //   reply(res, 409, 'A script is running')
      //   return
      // }

      /* Stop running script */
      if(data == 'stop') {
        script = null
        reply(res, 200, 'Running script (if any) stopped')
        process.kill(process.pid, 2)
        return
      }

      /* Execute script */
      let execution: Execution
      try {
        execution = JSON.parse(data)
        assert(typeof execution.filename == 'string' && typeof execution.script == 'string' && typeof execution.workingDirectory == 'string')
      } catch(err) {
        // Compiling error
        reply(res, 400, `Bad Request Format: ${err.stack}\nInput: ${data}`)
        return
      }
      // Change current working directory (expect '.' or `filesDir`)
      process.chdir(execution.workingDirectory)
      // Create an isolated context
      let context: any = Obj.copyOwnProperties(global)
      context.global = context
      vm.createContext(context)
      // Create script instance
      execution.script =
`((require) => {
${execution.script}
})`
      try {
        script = new vm.Script(execution.script, {
          lineOffset: -1,
          columnOffset: 0,
          displayErrors: true
        })
      } catch(err) {
        // Unknown error
        reply(res, 200, (err as Error).stack)
        return
      }
      // Run script
      try {
        script.runInContext(context, {
          filename: execution.filename,
          lineOffset: -1,
          columnOffset: 0,
          displayErrors: true,
          breakOnSigint: true  // Doesn't work well
        } as any)(require)
      } catch(err) {
        console.error(err.stack)
      } finally {
        reply(res, 200, 'OK')
      }
    })
  })
  server.listen(0)
  let addr: AddressInfo = server.address() as AddressInfo
  console.log(addr.port)  // Tell JavaVM our listening port
})()
