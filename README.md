# mJavaScript

JavaScript learning/playground app for android devices.

## Why mJavaScript?

mJavaScript runs your code on your android device via either built-in *WebView* or embedded *Node.js*, allowing you to learn or test JavaScript without a desktop computer.

## Got Problems?

Feel free to open an issue if you got any technical problem or question. Feature requests are welcome, too.

## Screenshots

![Home](https://github.com/Luluno01/mJavaScript/blob/master/screenshots/Screenshot_20180808-000640.jpg?raw=true)
![Edit](https://github.com/Luluno01/mJavaScript/blob/master/screenshots/Screenshot_20180808-000647.jpg?raw=true)
![Edit](https://github.com/Luluno01/mJavaScript/blob/master/screenshots/Screenshot_20180808-000740.jpg?raw=true)
![Output](https://github.com/Luluno01/mJavaScript/blob/master/screenshots/Screenshot_20180808-003114.jpg?raw=true)
![Rerun](https://github.com/Luluno01/mJavaScript/blob/master/screenshots/Screenshot_20180808-003119.jpg?raw=true)
![Save As](https://github.com/Luluno01/mJavaScript/blob/master/screenshots/Screenshot_20180808-003132.jpg?raw=true)
![Select Engine](https://github.com/Luluno01/mJavaScript/blob/master/screenshots/Screenshot_20180808-003506.jpg?raw=true)
![Output (Node.js)](https://github.com/Luluno01/mJavaScript/blob/master/screenshots/Screenshot_20180808-003515.jpg?raw=true)
![Open Project](https://github.com/Luluno01/mJavaScript/blob/master/screenshots/Screenshot_20180808-003540.jpg?raw=true)
![Settings](https://github.com/Luluno01/mJavaScript/blob/master/screenshots/Screenshot_20180808-003546.jpg?raw=true)
![Engines Settings](https://github.com/Luluno01/mJavaScript/blob/master/screenshots/Screenshot_20180808-003550.jpg?raw=true)
![About](https://github.com/Luluno01/mJavaScript/blob/master/screenshots/Screenshot_20180808-003554.jpg?raw=true)
![Help](https://github.com/Luluno01/mJavaScript/blob/master/screenshots/Screenshot_20180808-003557.jpg?raw=true)

## Note

Inheriting from [nodejs-mobile](https://github.com/janeasystems/nodejs-mobile):

> Currently, only a single instance of the Node.js runtime can be started within an application. Restarting the engine after it has finished running is also not supported.

The workaround of this app is running a script server to run provided script in a sandbox with very limited isolation using the built-in module `vm` (see [official documentation](https://nodejs.org/api/vm.html) for more details). So please don't modify any built-in object in Node.js unless you know exactly what you are doing.

Furthermore, if you are running your code via Node.js engine, please be careful not to execute scripts that include but are not limited to the follows:

1. Infinite loop
2. Infinite recursion
3. Endless `interval`
4. Unstoppable server (e.g. an infinite http server)
5. Any lengthy operation

Otherwise you will have to restart (or close) the app completely to interrupt your script. Don't do so unless you know excatly what you are doing.

## Q&A

I'm editing. Coming soon.

## License

I haven't figured out what license should I use. I would appreciate it if someone tell me which license to use.

Basically I'd like to use [WTFPL](www.wtfpl.net) license but it seems like I have to use either GPL or MPL. Well, I'm not a lawyer.

## Libraries

* [nodejs-mobile](https://github.com/janeasystems/nodejs-mobile) (unknown license)
* [Enlightened](https://github.com/0xFireball/Enlightened) (GPLv3 license)
* [NoNonsense-FilePicker](https://github.com/spacecowboy/NoNonsense-FilePicker) (MPL license)

