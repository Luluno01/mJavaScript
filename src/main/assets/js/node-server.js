"use strict";
exports.__esModule = true;
var http = require("http");
var vm = require("vm");
var assert = require("assert");
var Obj_1 = require("unlib.js/build/Obj");
(function () {
    process.on('uncaughtException', function (err) { console.error(err.stack); });
})();
(function () {
    function reply(res, statusCode, content) {
        res.writeHead(statusCode, { 'Content-Type': 'text/plain' });
        return res.end(content);
    }
    var script = null;
    // process.on('SIGKILL', () => process.exit(0))  // Cause Error: suv_signal_start EINVAL
    var server = http.createServer(function (req, res) {
        var data = '';
        req.on('data', function (chunk) {
            data += chunk;
        });
        req.on('end', function () {
            /* Only one running script is allowed */
            // if(script) {
            //   reply(res, 409, 'A script is running')
            //   return
            // }
            /* Stop running script */
            if (data == 'stop') {
                script = null;
                reply(res, 200, 'Running script (if any) stopped');
                process.kill(process.pid, 2);
                return;
            }
            /* Execute script */
            var execution;
            try {
                execution = JSON.parse(data);
                assert(typeof execution.filename == 'string' && typeof execution.script == 'string' && typeof execution.workingDirectory == 'string');
            }
            catch (err) {
                // Compiling error
                reply(res, 400, "Bad Request Format: " + err.stack + "\nInput: " + data);
                return;
            }
            // Change current working directory (expect '.' or `filesDir`)
            process.chdir(execution.workingDirectory);
            // Create an isolated context
            var context = Obj_1["default"].copyOwnProperties(global);
            context.global = context;
            vm.createContext(context);
            // Create script instance
            execution.script =
                "((require) => {\n" + execution.script + "\n})";
            try {
                script = new vm.Script(execution.script, {
                    lineOffset: -1,
                    columnOffset: 0,
                    displayErrors: true
                });
            }
            catch (err) {
                // Unknown error
                reply(res, 200, err.stack);
                return;
            }
            // Run script
            try {
                script.runInContext(context, {
                    filename: execution.filename,
                    lineOffset: -1,
                    columnOffset: 0,
                    displayErrors: true,
                    breakOnSigint: true // Doesn't work well
                })(require);
            }
            catch (err) {
                console.error(err.stack);
            }
            finally {
                reply(res, 200, 'OK');
            }
        });
    });
    server.listen(0);
    var addr = server.address();
    console.log(addr.port); // Tell JavaVM our listening port
})();
